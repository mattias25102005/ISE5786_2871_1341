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
    protected final Ray _axis;

    /**
     * Constructs a tube from radius and axis ray.
     *
     * @param radius radius
     * @param axis axis ray
     */
    public Tube(final double radius, final Ray axis) {
        super(radius);
        this._axis = axis;
    }

    /**
     * Returns the normal vector to the tube at a given point.
     *
     * @param point point on the tube
     * @return normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}