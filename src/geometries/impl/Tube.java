package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Class representing an infinite tube in 3D Cartesian coordinates.
 */
public class Tube extends RadialGeometry {

    /**
     * Axis ray of the tube.
     */
    protected final Ray axis;

    /**
     * Constructs a tube from axis ray and radius.
     *
     * @param axis axis ray
     * @param radius radius
     */
    public Tube(final Ray axis, final double radius) {
        super(radius);
        this.axis = axis;
    }

    /**
     * Returns the normal vector to the tube at a given point.
     *
     * @param point point on the tube
     * @return normal vector
     */
    @Override
    public Vector getNormal(final Point point) {
        return null;
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tube other)) return false;
        return axis.equals(other.axis) && radius == other.radius;
    }

    @Override
    public String toString() {
        return "Tube{axis=" + axis + ", radius=" + radius + "}";
    }
}