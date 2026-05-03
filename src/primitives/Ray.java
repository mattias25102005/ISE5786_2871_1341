package primitives;

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
    }/**
     * Trouve le point le plus proche de l'origine du rayon parmi une liste de points.
     * @param points Liste des points d'intersections (peut être null)
     * @return Le point le plus proche ou null si la liste est vide/null
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null; //
        }

        Point closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Point p : points) {
            double distance = _origin.distance(p);
            if (distance < minDistance) {
                minDistance = distance;
                closest = p;
            }
        }
        return closest;
    }
}