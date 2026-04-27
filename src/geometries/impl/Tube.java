package geometries.impl;

import primitives.Util;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

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
        // On utilise les noms exacts de ton fichier Ray.java
        Point p0 = _axis.getOrigin();    // Changé getP0() -> getOrigin()
        Vector v = _axis.getDirection(); // Changé getDir() -> getDirection()

        // Le reste du calcul est identique
        double t = v.dotProduct(point.subtract(p0));

        if (Util.isZero(t)) {
            return point.subtract(p0).normalize();
        }

        Point o = p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}