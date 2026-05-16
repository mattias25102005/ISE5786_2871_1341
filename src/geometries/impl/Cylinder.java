package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;

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
     */
    public Cylinder(final double radius, final Ray axis, final double height) {
        super(radius, axis);
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this._height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.getOrigin();
        Vector v = _axis.getDirection();

        double t;
        try {
            t = v.dotProduct(point.subtract(p0));
        } catch (IllegalArgumentException e) {
            return v.scale(-1);
        }

        if (primitives.Util.isZero(t)) {
            return v.scale(-1);
        }

        if (primitives.Util.isZero(t - _height)) {
            return v;
        }

        return super.getNormal(point);
    }

    // --- MODIFICATION POUR LA COHÉRENCE NVI ---
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Appelle la méthode helper du parent (Tube) qui doit aussi être renommée calcIntersectionsHelper
        return super.calcIntersectionsHelper(ray);
    }
}