package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract base class for geometries defined by a radius.
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * Radius of the geometry.
     */
    protected final double radius;

    /**
     * Squared radius of the geometry.
     */
    protected final double radiusSquared;

    /**
     * Constructs a radial geometry with a given radius.
     *
     * @param radius radius value
     * @throws IllegalArgumentException if radius is not positive
     */
    public RadialGeometry(final double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    @Override
    public String toString() {
        return "radius=" + radius;
    }
}