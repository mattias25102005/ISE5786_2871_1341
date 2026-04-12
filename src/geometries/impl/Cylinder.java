package geometries.impl;

import primitives.Point;
import primitives.Ray;
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
        Point p0 = _axis.getOrigin();
        Vector v = _axis.getDirection();

        // On calcule la projection du point sur l'axe : t = v * (P - P0)
        double t;
        try {
            t = v.dotProduct(point.subtract(p0));
        } catch (IllegalArgumentException e) {
            // Si le point est exactement p0, il est au centre de la base inférieure
            return v.scale(-1);
        }

        // Cas 1 : Le point est sur la base inférieure (t = 0 ou très proche de 0)
        if (primitives.Util.isZero(t)) {
            return v.scale(-1);
        }

        // Cas 2 : Le point est sur la base supérieure (t = height)
        if (primitives.Util.isZero(t - _height)) {
            return v;
        }

        // Cas 3 : Le point est sur la surface latérale (on utilise la logique du Tube)
        return super.getNormal(point);
    }
}