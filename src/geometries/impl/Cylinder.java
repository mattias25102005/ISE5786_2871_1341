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
    private final double height;

    /**
     * Constructs a cylinder from axis ray, radius, and height.
     *
     * @param axis axis ray
     * @param radius radius
     * @param height cylinder height
     */
    public Cylinder(final Ray axis, final double radius, final double height) {
        super(axis, radius);
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.height = height;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cylinder other)) return false;
        return super.equals(other) && height == other.height;
    }

    @Override
    public String toString() {
        return "Cylinder{axis=" + axis + ", radius=" + radius + ", height=" + height + "}";
    }
}