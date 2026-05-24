package primitives;

import geometries.api.Intersectable.Intersection;
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