package primitives;

/**
 * Class representing a ray in 3D Cartesian coordinates.
 */
public class Ray {

    /**
     * Starting point of the ray.
     */
    public final Point origin;

    /**
     * Direction vector of the ray (always normalized).
     */
    public final Vector direction;

    /**
     * Constructs a ray from origin point and direction vector.
     * The direction vector is normalized.
     *
     * @param origin starting point
     * @param direction direction vector
     */
    public Ray(final Point origin, final Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ray other)) return false;
        return origin.equals(other.origin) && direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray{origin=" + origin + ", direction=" + direction + "}";
    }
}