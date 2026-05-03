package renderer;

import primitives.*;
import java.util.MissingResourceException;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;
import static primitives.Util.*;

/**
 * Camera represents a pinhole camera with a view plane. It stores the
 * camera position (p0), orientation basis vectors (vTo, vUp, vRight),
 * view plane size, distance and image resolution. The camera provides
 * ray construction for individual pixels using the configured viewport
 * parameters.
 */
public class Camera implements Cloneable {
    /** Camera location (eye position) in world coordinates. */
    private Point p0;

    /** Forward direction vector pointing to the view plane center (normalized). */
    private Vector vTo;

    /** Up direction vector (normalized), orthogonal to vTo. */
    private Vector vUp;

    /** Right direction vector (normalized), orthogonal to vTo and vUp. */
    private Vector vRight;

    /** View plane width (world units). */
    private double width = 0;

    /** View plane height (world units). */
    private double height = 0;

    /** Distance from camera position to view plane (world units). */
    private double distance = 0;

    /** Number of horizontal pixels (resolution). */
    private int nX = 1;

    /** Number of vertical pixels (resolution). */
    private int nY = 1;

    /** Cached center point of the view plane (p0 + vTo * distance). */
    private Point pVpCenter;

    /** Width of a single pixel in world units (width / nX). */
    private double pixelWidth;

    /** Height of a single pixel in world units (height / nY). */
    private double pixelHeight;

    /** Private empty constructor used by the Builder. Not for public use. */
    private Camera() {}

    /**
     * Returns a new Builder instance for configuring and creating a Camera.
     * @return a fresh Camera.Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    // --- BUILDER CLASS ---
    /**
     * Builder follows the builder pattern to configure a Camera instance.
     * It validates parameters and computes cached fields before returning
     * a cloned Camera object.
     */
    public static class Builder {
        /** Public no-arg constructor for the Builder (documented to avoid default-constructor warning). */
        public Builder() {}
        /** Internal Camera being configured by this builder. */
        private final Camera _camera = new Camera();
        /** Optional look-at target point used to derive vTo when provided. */
        private Point _target = null;
        /** Optional up vector (defaults to global Y axis). */
        private Vector _vUp = Vector.AXIS_Y; // default value

        /**
         * Set the camera location (eye position).
         * @param p0 camera position in world coordinates
         * @return this Builder for chaining
         */
        public Builder setLocation(Point p0) {
            this._camera.p0 = p0;
            return this;
        }

        /**
         * Set the camera orientation directly using forward (vTo) and up vectors.
         * Both vectors will be normalized and validated for orthogonality.
         * @param vTo forward direction vector
         * @param vUp up direction vector
         * @return this Builder for chaining
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            this._camera.vTo = vTo;
            this._vUp = vUp;
            return this;
        }

        /**
         * Set the camera orientation by specifying a look-at target and an up vector.
         * The builder computes vTo = (target - p0) and normalizes it.
         * @param target point to look at
         * @param vUp up direction vector
         * @return this Builder for chaining
         */
        public Builder setDirection(Point target, Vector vUp) {
            this._target = target;
            this._vUp = vUp;
            return this;
        }

        /**
         * Set the camera orientation by specifying only a look-at target.
         * The up vector defaults to the builder's current _vUp value.
         * @param target point to look at
         * @return this Builder for chaining
         */
        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        /**
         * Set the physical view plane size in world units.
         * @param width view plane width
         * @param height view plane height
         * @return this Builder for chaining
         */
        public Builder setVpSize(double width, double height) {
            this._camera.width = width;
            this._camera.height = height;
            return this;
        }

        /**
         * Set the distance from the camera to the view plane.
         * @param distance distance to view plane (positive)
         * @return this Builder for chaining
         */
        public Builder setVpDistance(double distance) {
            this._camera.distance = distance;
            return this;
        }

        /**
         * Set image resolution in pixels.
         * @param nX horizontal pixel count
         * @param nY vertical pixel count
         * @return this Builder for chaining
         */
        public Builder setResolution(int nX, int nY) {
            this._camera.nX = nX;
            this._camera.nY = nY;
            return this;
        }

        // --- MÉTHODES DE VALIDATION (Demandées image 4) ---

        /**
         * Validate that resolution values are positive.
         * @throws IllegalArgumentException if nX or nY are non-positive
         */
        private void checkResolution() {

            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }
        /**
         * Validate camera location and derive orientation vectors.
         * Ensures p0 exists, computes vTo if target provided, normalizes vectors,
         * checks orthogonality and computes vRight and final vUp.
         */
        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            // 1. Calcul du vTo si on a un target
            if (_camera.vTo == null && _target != null)
                _camera.vTo = _target.subtract(_camera.p0);

            if (_camera.vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            // 2. Normalisation
            _camera.vTo = _camera.vTo.normalize();
            Vector vUpNormalized = _vUp.normalize(); // On utilise une variable locale pour tester

            // 3. Vérification de l'orthogonalité (doit être ZÉRO)
            // Si le produit scalaire n'est PAS zéro, alors ils ne sont pas orthogonaux -> Erreur
            if (!isZero(_camera.vTo.dotProduct(vUpNormalized))) {
                throw new IllegalArgumentException("vTo and vUp are not orthogonal");
            }

            // 4. Calcul des vecteurs finaux (on est sûr qu'ils sont orthogonaux ici)
            _camera.vRight = _camera.vTo.crossProduct(vUpNormalized).normalize();
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        /**
         * Validate view plane size and distance, then compute cached fields:
         * - pVpCenter: center of the view plane
         * - pixelWidth / pixelHeight: size of a single pixel in world units
         * @throws IllegalArgumentException if view plane size or distance are invalid
         */
        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("Wrong View Plane size");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Wrong View Plane distance");

          // Compute cached fields
            _camera.pVpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        /**
         * Validate configuration and return a fully-initialized Camera.
         * @return a cloned Camera ready for use
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            // On peut appeler clone() directement sur l'objet camera
            return _camera.clone();
        }
    }

    // --- Ray construction using cached view-plane fields ---
    /**
     * Construct a primary ray that passes from the camera through the center of
     * the pixel at column j and row i.
     * @param j pixel column index (0..nX-1)
     * @param i pixel row index (0..nY-1)
     * @return a Ray originating at the camera position pointing toward the pixel
     */
    public Ray constructRay(int j, int i) {
        double yi = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);
        double xj = alignZero((j - (nX - 1) / 2.0) * pixelWidth);

        Point pij = pVpCenter;
        if (!isZero(xj)) pij = pij.add(vRight.scale(xj));
        if (!isZero(yi)) pij = pij.add(vUp.scale(yi));

        return new Ray(p0, pij.subtract(p0));
    }

    /**
     * Clone the Camera object. Builder returns a clone to prevent exposing the
     * internal mutable builder instance.
     */
    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            // Cette partie ne sera jamais atteinte car on implémente Cloneable
            throw new RuntimeException(e);
        }
    }
}

