package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing a plane in 3D Cartesian coordinates.
 */
public class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private final Point point;

    /**
     * Normal vector to the plane.
     */
    private final Vector normal;

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param point point on the plane
     * @param normal normal vector to the plane
     */
    public Plane(final Point point, final Vector normal) {
        this.point = point;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane from three points.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(final Point p1, final Point p2, final Point p3) {
        this.point = p1;
        final Vector u = p2.subtract(p1);
        final Vector v = p3.subtract(p1);
        this.normal = u.crossProduct(v).normalize();
    }

    /**
     * Returns the normal vector of the plane.
     *
     * @param point point on the plane
     * @return plane normal
     */
    @Override
    public Vector getNormal(final Point point) {
        return normal;
    }

    /**
     * Returns the normal vector of the plane.
     *
     * @return plane normal
     */
   // public Vector getNormal() {
       // return normal;
   // }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plane other)) return false;
        return point.equals(other.point) && normal.equals(other.normal);
    }

    @Override
    public String toString() {
        return "Plane{point=" + point + ", normal=" + normal + "}";
    }
}