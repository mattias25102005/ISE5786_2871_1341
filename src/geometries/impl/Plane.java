package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing a plane in 3D Cartesian coordinates.
 */
public final class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private final Point _point;

    /**
     * Normal vector to the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param point point on the plane
     * @param normal normal vector to the plane
     */
    public Plane(final Point point, final Vector normal) {
        this._point = point;
        this._normal = normal;
    }

    /**
     * Constructs a plane from three points.
     * At this stage, only one reference point is stored and the normal is null.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(final Point p1, final Point p2, final Point p3) {
        this._point = p1;
        this._normal = null;
    }

    @Override
    public Vector getNormal(final Point point) {
        return _normal;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plane other)) return false;
        return _point.equals(other._point)
                && (_normal == null ? other._normal == null : _normal.equals(other._normal));
    }

    @Override
    public String toString() {
        return "Plane{point=" + _point + ", normal=" + _normal + "}";
    }
}