package geometries.impl;

import primitives.Point;
import primitives.Vector;
/**
 * Class representing a triangle in 3D Cartesian coordinates.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three points.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(final Point p1, final Point p2, final Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle" + super.toString();
    }
    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }
}