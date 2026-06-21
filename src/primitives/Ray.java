package primitives;

import geometries.api.Intersectable.Intersection;
import renderer.Blackboard;
import primitives.SamplingType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a ray in 3D Cartesian coordinates.
 */
public class Ray {

    /**
     * Starting point of the ray.
     */
    private final Point _origin;

    /**
     * Direction vector of the ray (always normalized).
     */
    private final Vector _direction;

    /**
     * Constructs a ray from origin point and direction vector.
     * The direction vector is normalized.
     *
     * @param origin starting point
     * @param direction direction vector
     */
    public Ray(final Point origin, final Vector direction) {
        this._origin = origin;
        this._direction = direction.normalize();
    }

    /**
     * NOUVEAU CONSTRUCTEUR (Point 4)
     * Construit un rayon en décalant son origine de la surface pour éviter l'auto-intersection.
     *
     * @param head      L'origine brute sur la surface géométrique
     * @param direction La direction du rayon (va être normalisée)
     * @param normal    Le vecteur normal à la surface au point d'origine
     */
    public Ray(Point head, Vector direction, Vector normal) {
        this._direction = direction.normalize();

        // Reprise exacte de la constante et de la logique de décalage demandée (Point 4)
        double DELTA = 0.1;
        double nv = normal.dotProduct(this._direction);

        if (Util.isZero(nv)) {
            this._origin = head;
        } else {
            Vector deltaVector = normal.scale(nv > 0 ? DELTA : -DELTA);
            this._origin = head.add(deltaVector);
        }
    }

    /**
     * Génère un faisceau de rayons (Beam) échantillonné autour de ce rayon.
     * Cette méthode centralisée remplace les anciennes implémentations de Camera et SimpleRayTracer.
     *
     * @param distance    La distance entre l'origine du rayon et le plan de projection (1 pour le Glossy, distance du ViewPlane pour la Caméra)
     * @param width       La largeur de la zone de flou/pixel
     * @param height      La hauteur de la zone de flou/pixel
     * @param sampleCount Le nombre total de rayons souhaités
     * @param type        Le type d'échantillonnage (GRID, RANDOM, JITTERED)
     * @return Liste de rayons formant le faisceau
     */
    public List<Ray> generateBeam(double distance, double width, double height, int sampleCount, SamplingType type) {
        List<Ray> beam = new ArrayList<>();

        // Si la zone est nulle ou 1 seul échantillon demandé, on conserve uniquement ce rayon idéal
        if ((width <= 0 && height <= 0) || sampleCount <= 1) {
            beam.add(this);
            return beam;
        }

        // 1. Point central sur le plan à la distance donnée
        Point center = this.getPoint(distance);

        // 2. Construction d'un repère local 2D (u, w) orthogonal à la direction du rayon
        Vector arbitrary = new Vector(1, 0, 0);
        try {
            _direction.crossProduct(arbitrary);
        } catch (IllegalArgumentException e) {
            arbitrary = new Vector(0, 1, 0); // Cas où la direction est colinéaire à (1,0,0)
        }

        Vector u = _direction.crossProduct(arbitrary).normalize();
        Vector w = _direction.crossProduct(u).normalize();

        // 3. Récupération des décalages 2D normalisés [-0.5, 0.5] du Blackboard
        List<Double3> offsets = Blackboard.generateSamples(sampleCount, type);

        // 4. Transformation des décalages en rayons 3D
        for (Double3 offset : offsets) {
            double deltaX = offset._d1() * width;
            double deltaY = offset._d2() * height;

            Point targetPoint = center;
            if (!Util.isZero(deltaX)) targetPoint = targetPoint.add(u.scale(deltaX));
            if (!Util.isZero(deltaY)) targetPoint = targetPoint.add(w.scale(deltaY));

            Vector beamDir = targetPoint.subtract(_origin).normalize();
            beam.add(new Ray(_origin, beamDir));
        }

        return beam;
    }

    /**
     * Returns the origin point of the ray.
     *
     * @return origin point
     */
    public Point getOrigin() {
        return _origin;
    }

    /**
     * Returns the direction vector of the ray.
     *
     * @return normalized direction vector
     */
    public Vector getDirection() {
        return _direction;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ray other)) return false;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public String toString() {
        return "Ray{origin=" + _origin + ", direction=" + _direction + "}";
    }

    /**
     * Get a point on the ray at a specific distance t from the origin.
     * @param t distance from the origin (scalar)
     * @return the point P = P0 + t * v
     */
    public Point getPoint(double t) {
        if (Util.isZero(t)) {
            return _origin;
        }
        return _origin.add(_direction.scale(t));
    }

    /**
     * Trouve le point le plus proche de l'origine du rayon parmi une liste de points.
     * @param points Liste des points d'intersections (peut être null)
     * @return Le point le plus proche ou null si la liste est vide/null
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestIntersection(points.stream()
                .map(point -> new Intersection(null, point))
                .toList()).point;
    }

    /**
     * Trouve l'intersection la plus proche de l'origine du rayon.
     * (RAJOUT ÉTAPE 4.א)
     * @param intersections Liste des intersections (peut être null)
     * @return L'intersection la plus proche ou null
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distance = _origin.distanceSquared(intersection.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = intersection;
            }
        }
        return closest;
    }
}