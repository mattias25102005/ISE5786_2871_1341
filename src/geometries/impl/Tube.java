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
        Point p0 = _axis.getOrigin();
        Vector v = _axis.getDirection();

        double t = v.dotProduct(point.subtract(p0));

        if (Util.isZero(t)) {
            return point.subtract(p0).normalize();
        }

        Point o = p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    // --- MODIFICATIONS POUR LE DESIGN NVI (IMAGE 1) ---

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // On remplace findGeoIntersectionsHelper par calcIntersectionsHelper
        // et GeoPoint par Intersection
        return null;
    }
}