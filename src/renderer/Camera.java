package renderer;

import primitives.*;
import scene.Scene;
import java.util.MissingResourceException;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;

/**
 * Represents a camera in 3D space for rendering scenes using ray tracing.
 * The camera defines the viewpoint, orientation, and viewport settings.
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
    /** Center point of the view plane */
    private Point pVpCenter;
    /** Width of a single pixel on the view plane */
    private double pixelWidth;
    /** Height of a single pixel on the view plane */
    private double pixelHeight;

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
     * @return this Camera for chaining
     * @throws MissingResourceException if ImageWriter or RayTracer is not set
     */
    public Camera renderImage() {
        if (_imageWriter == null) throw new MissingResourceException("Missing ImageWriter", "Camera", "_imageWriter");
        if (_rayTracer == null) throw new MissingResourceException("Missing RayTracer", "Camera", "_rayTracer");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(nX, nY, j, i);
            }
        }
        return this;
    }

    /** Casts a ray through the pixel (j,i), traces it and writes the color
     * @param nX number of pixels in X
     * @param nY number of pixels in Y
     * @param j pixel column index
     * @param i pixel row index
     */
    private void castRay(int nX, int nY, int j, int i) {
        Ray ray = constructRay(j, i);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(j, i, color);
    }

    /**
     * Prints a grid on the image for debugging purposes.
     * @param interval the grid interval
     * @param color the grid color
     * @return this Camera for chaining
     * @throws MissingResourceException if ImageWriter is not set
     */
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

    /**
     * Writes the rendered image to a file using the configured ImageWriter.
     * @param fileName the destination file path or name
     */
    public void writeToImage(String fileName) {
        if (_imageWriter == null) throw new MissingResourceException("Missing ImageWriter", "Camera", "_imageWriter");
        _imageWriter.writeToImage(fileName);
    }

    /**
     * Constructs a ray from the camera through the center of the given pixel.
     * @param j the pixel column (x)
     * @param i the pixel row (y)
     * @return a Ray starting at the camera position going through the pixel
     */
    public Ray constructRay(int j, int i) {
        double yi = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);
        double xj = alignZero((j - (nX - 1) / 2.0) * pixelWidth);

        Point pij = pVpCenter;
        if (!isZero(xj)) pij = pij.add(vRight.scale(xj));
        if (!isZero(yi)) pij = pij.add(vUp.scale(yi));

        return new Ray(p0, pij.subtract(p0));
    }

    // --- CLASSE BUILDER ---
    /**
     * Builder for configuring and creating Camera instances.
     */
    public static class Builder {
        /** Camera instance being configured by the builder */
        private final Camera _camera = new Camera();
        /** Optional target point to look at */
        private Point _target = null;
        /** Preferred up vector (defaults to world Y axis) */
        private Vector _vUp = Vector.AXIS_Y;

        /** Public default builder constructor */
        public Builder() {}

        /** Sets the camera location (eye point)
         * @param p0 camera position
         * @return this Builder
         */
        public Builder setLocation(Point p0) {
            this._camera.p0 = p0;
            return this;
        }

        /** Sets camera direction using direction vectors vTo and vUp
         * @param vTo forward direction vector
         * @param vUp up direction vector
         * @return this Builder
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            this._camera.vTo = vTo;
            this._vUp = vUp;
            return this;
        }

        /** Sets camera direction by specifying a target point to look at and an up vector
         * @param target target point to look at
         * @param vUp up direction vector
         * @return this Builder
         */
        public Builder setDirection(Point target, Vector vUp) {
            this._target = target;
            this._vUp = vUp;
            return this;
        }

        /** Sets camera direction by specifying only a target point (uses default up)
         * @param target target point to look at
         * @return this Builder
         */
        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        /** Sets the view plane size (width, height)
         * @param width view plane width
         * @param height view plane height
         * @return this Builder
         */
        public Builder setVpSize(double width, double height) {
            this._camera.width = width;
            this._camera.height = height;
            return this;
        }

        /** Sets the distance from camera to view plane
         * @param distance distance to the view plane
         * @return this Builder
         */
        public Builder setVpDistance(double distance) {
            this._camera.distance = distance;
            return this;
        }

        /** Sets the image resolution (nx, ny)
         * @param nX number of pixels in X
         * @param nY number of pixels in Y
         * @return this Builder
         */
        public Builder setResolution(int nX, int nY) {
            this._camera.nX = nX;
            this._camera.nY = nY;
            return this;
        }

        /** Sets the ImageWriter used to output the rendered image
         * @param imageWriter the ImageWriter
         * @return this Builder
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            this._camera._imageWriter = imageWriter;
            return this;
        }

        /** Sets a custom RayTracer implementation
         * @param rayTracer the RayTracer implementation
         * @return this Builder
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            this._camera._rayTracer = rayTracer;
            return this;
        }

        /** Convenience: sets RayTracer by choosing a RayTracerType
         * @param scene the scene to render
         * @param type the ray tracer type
         * @return this Builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._camera._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unknown RayTracer type");
            }
            return this;
        }

        // --- MÉTHODES DE VALIDATION ---

        /** Validates resolution values are positive */
        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

        /** Validates that location and direction are provided and consistent */
        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            // Calcul de la direction si target est fourni
            if (_camera.vTo == null && _target != null)
                _camera.vTo = _target.subtract(_camera.p0);

            if (_camera.vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            // Normalisation et validation géométrique
            _camera.vTo = _camera.vTo.normalize();
            Vector vUpNorm = _vUp.normalize();

            if (!isZero(_camera.vTo.dotProduct(vUpNorm)))
                throw new IllegalArgumentException("vTo and vUp not orthogonal");

            _camera.vRight = _camera.vTo.crossProduct(vUpNorm).normalize();
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        /** Validates view plane size/distance and initializes computed fields */
        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("Wrong VP size");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Wrong VP distance");

            // Initialisation des champs calculés
            _camera.pVpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        /**
         * Builds and returns a validated Camera instance configured by this builder.
         * @return a constructed Camera instance
         */
        public Camera build() {
            // Appel des étapes de validation et calculs internes
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            // Logique d'initialisation par défaut (Étape 5 / RenderTests)
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
    /**
     * Creates a shallow clone of this Camera.
     * @return a cloned Camera instance
     */
    public Camera clone() {
        try {
            Camera cloned = (Camera) super.clone();
            // IMPORTANT : Conserver la copie manuelle des références requises pour vos tests
            cloned._imageWriter = this._imageWriter;
            cloned._rayTracer = this._rayTracer;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}