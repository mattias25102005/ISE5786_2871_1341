package renderer;

import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static primitives.Util.isZero;
import static primitives.Util.alignZero;

/**
 * Represents a camera in 3D space for rendering scenes using ray tracing.
 * Supports Multi-threading and Adaptive Super-Sampling.
 */
public class Camera implements Cloneable {
    /** Camera position (eye point) */
    private Point p0;
    /** Forward direction vector (towards scene) */
    private Vector vTo;
    /** Up direction vector */
    private Vector vUp;
    /** Right direction vector (vTo x vUp) */
    private Vector vRight;
    /** View plane width */
    private double width = 0;
    /** View plane height */
    private double height = 0;
    /** Distance to view plane */
    private double distance = 0;
    /** Number of pixels in X (columns) */
    private int nX = 1;
    /** Number of pixels in Y (rows) */
    private int nY = 1;

    /** Image writer used to output pixels */
    private ImageWriter _imageWriter;
    /** Ray tracer implementation used to shade rays */
    private RayTracerBase _rayTracer;

    // Champs calculés pour économiser des calculs répétés
    private Point pVpCenter;
    private double pixelWidth;
    private double pixelHeight;

    /** Number of rays per pixel for super sampling (Classic Grid) */
    private int samples = 1;

    // --- CONFIGURATION POUR LE MULTI-THREADING (Étape 1) ---
    /** Number of threads to use for rendering (1 = sequential) */
    private int threadsCount = 1;

    // --- CONFIGURATION POUR L'ADAPTIVE SUPER-SAMPLING (Étape 2) ---
    /** Flag to enable or disable Adaptive Super-Sampling */
    private boolean useAdaptiveSampling = false;
    /** Maximum recursion depth level for adaptive split */
    private int maxAdaptiveLevel = 3;

    /** Private default constructor used by Builder */
    private Camera() {}

    /**
     * Returns a builder for constructing a Camera instance.
     * @return a new Camera.Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    // --- MÉTHODES DE RENDU ---

    /**
     * Renders the image by casting rays for each pixel.
     * Supports both parallel multi-threaded execution and sequential execution.
     * @return this Camera for chaining
     * @throws MissingResourceException if ImageWriter or RayTracer is not set
     */
    public Camera renderImage() {
        if (_imageWriter == null) throw new MissingResourceException("Missing ImageWriter", "Camera", "_imageWriter");
        if (_rayTracer == null) throw new MissingResourceException("Missing RayTracer", "Camera", "_rayTracer");

        // Cas 1 : Rendu Parallèle (Multi-threading actif si threadsCount > 1)
        if (threadsCount > 1) {
            ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

            for (int i = 0; i < nY; i++) {
                final int row = i;
                // Chaque ligne de pixels devient une tâche exécutée par le pool de threads
                executor.submit(() -> {
                    for (int j = 0; j < nX; j++) {
                        castRay(nX, nY, j, row);
                    }
                });
            }

            executor.shutdown();
            try {
                // Attente de la fin de tous les calculs (Timeout de 24h par sécurité)
                if (!executor.awaitTermination(24, TimeUnit.HOURS)) {
                    System.err.println("Le rendu a dépassé le délai imparti.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Le rendu multi-threadé a été interrompu", e);
            }
        }
        // Cas 2 : Rendu Séquentiel classique (Single-thread si threadsCount == 1)
        else {
            for (int i = 0; i < nY; i++) {
                for (int j = 0; j < nX; j++) {
                    castRay(nX, nY, j, i);
                }
            }
        }
        return this;
    }

    /** Casts a ray through the pixel (j,i), traces it and writes the color */
    private void castRay(int nX, int nY, int j, int i) {
        // Option A : Rendu avec Adaptive Super-Sampling (Multi-thread compatible)
        if (useAdaptiveSampling) {
            Color color = adaptiveSamplePixel(j, i);
            _imageWriter.writePixel(j, i, color);
            return;
        }

        // Option B : Pas d'échantillonnage (1 seul rayon par pixel)
        if (samples == 1) {
            Ray ray = constructRay(j, i);
            _imageWriter.writePixel(j, i, _rayTracer.traceRay(ray));
            return;
        }

        // Option C : Super-sampling classique (Grille régulière fixe)
        Color color = Color.BLACK;
        for (Ray ray : constructBeam(j, i)) {
            color = color.add(_rayTracer.traceRay(ray));
        }
        _imageWriter.writePixel(j, i, color.reduce(samples));
    }

    /** Entry point for the Adaptive Super-Sampling computation on a specific pixel. */
    private Color adaptiveSamplePixel(int j, int i) {
        double xCenter = alignZero((j - (nX - 1) / 2.0) * pixelWidth);
        double yCenter = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);

        Point pixelCenter = pVpCenter;
        if (!isZero(xCenter)) pixelCenter = pixelCenter.add(vRight.scale(xCenter));
        if (!isZero(yCenter)) pixelCenter = pixelCenter.add(vUp.scale(yCenter));

        double halfWidth = pixelWidth / 2.0;
        double halfHeight = pixelHeight / 2.0;

        Point pNW = pixelCenter.add(vRight.scale(-halfWidth)).add(vUp.scale(halfHeight));
        Point pNE = pixelCenter.add(vRight.scale(halfWidth)).add(vUp.scale(halfHeight));
        Point pSW = pixelCenter.add(vRight.scale(-halfWidth)).add(vUp.scale(-halfHeight));
        Point pSE = pixelCenter.add(vRight.scale(halfWidth)).add(vUp.scale(-halfHeight));

        Color cNW = _rayTracer.traceRay(new Ray(p0, pNW.subtract(p0)));
        Color cNE = _rayTracer.traceRay(new Ray(p0, pNE.subtract(p0)));
        Color cSW = _rayTracer.traceRay(new Ray(p0, pSW.subtract(p0)));
        Color cSE = _rayTracer.traceRay(new Ray(p0, pSE.subtract(p0)));

        return adaptiveSampleRecursive(pixelCenter, pixelWidth, pixelHeight, 1, cNW, cNE, cSW, cSE);
    }

    /** Recursive function that checks if a sub-quadrant is homogeneous or needs subdivision. */
    private Color adaptiveSampleRecursive(Point center, double w, double h, int level,
                                          Color cNW, Color cNE, Color cSW, Color cSE) {
        // Utilisation de la méthode .equals() tolérante de votre classe Color
        if (level >= maxAdaptiveLevel || (cNW.equals(cNE) && cNW.equals(cSW) && cNW.equals(cSE))) {
            return cNW.add(cNE, cSW, cSE).reduce(4);
        }

        double halfW = w / 2.0;
        double halfH = h / 2.0;
        double quarterW = w / 4.0;
        double quarterH = h / 4.0;

        Point pCenter = center;
        Point pN = center.add(vUp.scale(halfH));
        Point pS = center.add(vUp.scale(-halfH));
        Point pE = center.add(vRight.scale(halfW));
        Point pW = center.add(vRight.scale(-halfW));

        Color cCenter = _rayTracer.traceRay(new Ray(p0, pCenter.subtract(p0)));
        Color cN = _rayTracer.traceRay(new Ray(p0, pN.subtract(p0)));
        Color cS = _rayTracer.traceRay(new Ray(p0, pS.subtract(p0)));
        Color cE = _rayTracer.traceRay(new Ray(p0, pE.subtract(p0)));
        Color cW = _rayTracer.traceRay(new Ray(p0, pW.subtract(p0)));

        Point centerNW = center.add(vRight.scale(-quarterW)).add(vUp.scale(quarterH));
        Point centerNE = center.add(vRight.scale(quarterW)).add(vUp.scale(quarterH));
        Point centerSW = center.add(vRight.scale(-quarterW)).add(vUp.scale(-quarterH));
        Point centerSE = center.add(vRight.scale(quarterW)).add(vUp.scale(-quarterH));

        Color qNW = adaptiveSampleRecursive(centerNW, halfW, halfH, level + 1, cNW, cN, cW, cCenter);
        Color qNE = adaptiveSampleRecursive(centerNE, halfW, halfH, level + 1, cN, cNE, cCenter, cE);
        Color qSW = adaptiveSampleRecursive(centerSW, halfW, halfH, level + 1, cW, cCenter, cSW, cS);
        Color qSE = adaptiveSampleRecursive(centerSE, halfW, halfH, level + 1, cCenter, cE, cS, cSE);

        return qNW.add(qNE, qSW, qSE).reduce(4);
    }

    /** Constructs a beam of rays through a pixel for Classic Super-sampling. */
    private List<Ray> constructBeam(int j, int i) {
        // 1. On construit le rayon central du pixel comme d'habitude
        Ray baseRay = constructRay(j, i);

        // 2. On génère le faisceau en appelant la méthode du RAYON !
        // Tu peux remplacer SamplingType.GRID par SamplingType.JITTERED ici si tu veux du Jittered pour la caméra !
        return baseRay.generateBeam(distance, pixelWidth, pixelHeight, samples, SamplingType.GRID);
    }

    public Camera printGrid(int interval, Color color) {
        if (_imageWriter == null) throw new MissingResourceException("Missing ImageWriter", "Camera", "_imageWriter");
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    public void writeToImage(String fileName) {
        if (_imageWriter == null) throw new MissingResourceException("Missing ImageWriter", "Camera", "_imageWriter");
        _imageWriter.writeToImage(fileName);
    }

    public Ray constructRay(int j, int i) {
        double yi = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);
        double xj = alignZero((j - (nX - 1) / 2.0) * pixelWidth);

        Point pij = pVpCenter;
        if (!isZero(xj)) pij = pij.add(vRight.scale(xj));
        if (!isZero(yi)) pij = pij.add(vUp.scale(yi));

        return new Ray(p0, pij.subtract(p0));
    }

    // --- CLASSE BUILDER ENRICHE ---
    public static class Builder {
        private final Camera _camera = new Camera();
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y;

        /**
         * ÉTAPE 1 : Configuration du Multi-threading.
         * @param threads nombre de cœurs/threads à allouer (1 = séquentiel)
         * @return le Builder courant
         */
        public Builder setThreadsCount(int threads) {
            if (threads < 1) throw new IllegalArgumentException("Threads count must be >= 1");
            _camera.threadsCount = threads;
            return this;
        }

        /**
         * ÉTAPE 2 : Configuration de l'Adaptive Super-Sampling.
         */
        public Builder setAdaptiveSampling(boolean enable, int maxLevel) {
            if (maxLevel < 1) throw new IllegalArgumentException("Max adaptive level must be >= 1");
            _camera.useAdaptiveSampling = enable;
            _camera.maxAdaptiveLevel = maxLevel;
            return this;
        }

        public Builder setSuperSampling(int samples) {
            if (samples < 1) throw new IllegalArgumentException("Samples must be >= 1");
            int sqrt = (int) Math.sqrt(samples);
            if (sqrt * sqrt != samples) {
                throw new IllegalArgumentException("Samples count must be a perfect square (e.g. 4, 9, 16)");
            }
            _camera.samples = samples;
            return this;
        }

        public Builder() {}

        public Builder setLocation(Point p0) {
            this._camera.p0 = p0;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            this._camera.vTo = vTo;
            this._vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this._target = target;
            this._vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            this._camera.width = width;
            this._camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            this._camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            this._camera.nX = nX;
            this._camera.nY = nY;
            return this;
        }

        public Builder setImageWriter(ImageWriter imageWriter) {
            this._camera._imageWriter = imageWriter;
            return this;
        }

        public Builder setRayTracer(RayTracerBase rayTracer) {
            this._camera._rayTracer = rayTracer;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._camera._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unknown RayTracer type");
            }
            return this;
        }

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            if (_camera.vTo == null && _target != null)
                _camera.vTo = _target.subtract(_camera.p0);

            if (_camera.vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            _camera.vTo = _camera.vTo.normalize();
            Vector vUpNorm = _vUp.normalize();

            if (!isZero(_camera.vTo.dotProduct(vUpNorm)))
                throw new IllegalArgumentException("vTo and vUp not orthogonal");

            _camera.vRight = _camera.vTo.crossProduct(vUpNorm).normalize();
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("Wrong VP size");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Wrong VP distance");

            _camera.pVpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._rayTracer == null) {
                this.setRayTracer(new Scene("default"), RayTracerType.SIMPLE);
            }

            if (_camera._imageWriter == null) {
                _camera._imageWriter = new ImageWriter(_camera.nX, _camera.nY);
            }

            return _camera.clone();
        }
    }

    @Override
    public Camera clone() {
        try {
            Camera cloned = (Camera) super.clone();
            cloned._imageWriter = this._imageWriter;
            cloned._rayTracer = this._rayTracer;
            cloned.samples = this.samples;
            cloned.threadsCount = this.threadsCount;
            cloned.useAdaptiveSampling = this.useAdaptiveSampling;
            cloned.maxAdaptiveLevel = this.maxAdaptiveLevel;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}