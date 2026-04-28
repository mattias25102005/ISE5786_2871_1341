package renderer;

import primitives.*;
import java.util.MissingResourceException;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;
import static primitives.Util.*;

public class Camera implements Cloneable {
    // Attributs de Camera (doivent rester private)
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double width = 0;
    private double height = 0;
    private double distance = 0;
    private int nX = 1;
    private int nY = 1;

    // Champs calculés pour "économiser des calculs répétés" (indiqué page 2)
    private Point pVpCenter;
    private double pixelWidth;
    private double pixelHeight;

    private Camera() {}

    public static Builder getBuilder() {
        return new Builder();
    }

    // --- CLASSE BUILDER ---
    public static class Builder {
        private final Camera _camera = new Camera();
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y; // Valeur par défaut demandée

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

        // --- MÉTHODES DE VALIDATION (Demandées image 4) ---

        private void checkResolution() {

            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }
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

        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("Wrong View Plane size");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Wrong View Plane distance");

            // Initialisation des champs calculés (indiqué fin de l'image 4)
            _camera.pVpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            // On peut appeler clone() directement sur l'objet camera
            return _camera.clone();
        }
    }

    // --- MÉTHODE CONSTRUCT RAY (Utilisant les champs calculés) ---
    public Ray constructRay(int j, int i) {
        double yi = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);
        double xj = alignZero((j - (nX - 1) / 2.0) * pixelWidth);

        Point pij = pVpCenter;
        if (!isZero(xj)) pij = pij.add(vRight.scale(xj));
        if (!isZero(yi)) pij = pij.add(vUp.scale(yi));

        return new Ray(p0, pij.subtract(p0));
    }

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