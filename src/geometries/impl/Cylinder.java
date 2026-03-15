package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Class representing a finite cylinder in 3D Cartesian coordinates.
 */
public class Cylinder extends Tube {

    /**
     * Height of the cylinder.
     */
    private final double _height;

    /**
     * Constructs a cylinder from radius, axis ray, and height.
     *
     * @param radius radius
     * @param axis axis ray
     * @param height cylinder height
     */
    public Cylinder(final double radius, final Ray axis, final double height) {
        super(radius, axis);
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this._height = height;
    }

    /**
     * Returns the normal vector to the cylinder at a given point.
     *
     * @param point point on the cylinder
     * @return normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}