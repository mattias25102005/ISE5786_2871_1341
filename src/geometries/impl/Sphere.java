package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class representing a sphere in 3D Cartesian coordinates.
 */
public class Sphere extends RadialGeometry {

    /**
     * Center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from center and radius.
     *
     * @param center center point
     * @param radius radius
     */
    public Sphere(final Point center, final double radius) {
        super(radius);
        this._center = center;
    }

    /**
     * Returns the normal vector to the sphere at a given point.
     *
     * @param point point on the sphere
     * @return normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        // Formule : Normal = (P - Center) / ||P - Center||
        return point.subtract(_center).normalize();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sphere other)) return false;
        return _center.equals(other._center) && _radius == other._radius;
    }

    @Override
    public String toString() {
        return "Sphere{center=" + _center + ", radius=" + _radius + "}";
    }
}