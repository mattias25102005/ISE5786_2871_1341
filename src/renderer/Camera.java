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
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double width = 0;
    private double height = 0;
    private double distance = 0;
    private int nX = 0;
    private int nY = 0;

    private ImageWriter _imageWriter;
    private RayTracerBase _rayTracer;

    private Point pVpCenter;
    private double pixelWidth;
    private double pixelHeight;

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

    // --- CLASSE BUILDER ---
    public static class Builder {
        private final Camera _camera = new Camera();
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y;

        public Builder() {
            // Valeur par défaut pour éviter "Resolution must be positive" dans CameraTests
            _camera.nX = 1;
            _camera.nY = 1;
        }

        public Builder setLocation(Point p0) {
            this._camera.p0 = p0;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            this._camera.vTo = vTo;
            this._vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this._target = target;
            this._vUp = vUp;
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

        public Camera build() {
            // Validation résolution
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            if (_camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            // Calcul direction si target
            if (_camera.vTo == null && _target != null)
                _camera.vTo = _target.subtract(_camera.p0);

            if (_camera.vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            // Initialisation RayTracer par défaut si vide (Étape 5)
            if (_camera._rayTracer == null) {
                this.setRayTracer(new Scene("default"), RayTracerType.SIMPLE);
            }

            // Initialize default ImageWriter when none provided so tests can render
            if (_camera._imageWriter == null) {
                _camera._imageWriter = new ImageWriter(_camera.nX, _camera.nY);
            }

            // Calculs géométriques
            _camera.vTo = _camera.vTo.normalize();
            Vector vUpNorm = _vUp.normalize();
            if (!isZero(_camera.vTo.dotProduct(vUpNorm)))
                throw new IllegalArgumentException("vTo and vUp not orthogonal");

            _camera.vRight = _camera.vTo.crossProduct(vUpNorm).normalize();
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();

            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("Wrong VP size");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Wrong VP distance");

            _camera.pVpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;

            return _camera.clone();
        }
    }

    @Override
    public Camera clone() {
        try {
            Camera cloned = (Camera) super.clone();
            // IMPORTANT : Copier manuellement les références pour RenderTests
            cloned._imageWriter = this._imageWriter;
            cloned._rayTracer = this._rayTracer;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}