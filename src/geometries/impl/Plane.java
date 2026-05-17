package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.*;

/**
 * Class representing a plane in 3D Cartesian coordinates.
 */
public final class Plane extends Geometry {

    /** A point on the plane */
    private final Point _point;
    /** Normal vector to the plane */
    private final Vector _normal;

    /**
     * Constructs a plane from a point and a normal vector.
     * @param point a point on the plane
     * @param normal the normal vector to the plane
     */
    public Plane(final Point point, final Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    /**
     * Constructs a plane from three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(final Point p1, final Point p2, final Point p3) {
        this._point = p1;
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        this._normal = v1.crossProduct(v2).normalize();
    }

    @Override
    public Vector getNormal(final Point point) {
        return _normal;
    }

    // --- MODIFICATIONS POUR LE DESIGN NVI (ÉTAPE 3 & 4) ---

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        // 1. Calcul du dénominateur : n ⋅ v
        double nv = _normal.dotProduct(v);

        // Si nv est proche de 0, le rayon est parallèle au plan
        if (isZero(nv)) {
            return null;
        }

        // 2. Calcul du numérateur : n ⋅ (Q - P0)
        Vector p0q0;
        try {
            p0q0 = _point.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Si P0 == _point, le rayon commence sur le plan (t=0),
            // on considère qu'il n'y a pas d'intersection devant le rayon.
            return null;
        }

        double nP0Q0 = alignZero(_normal.dotProduct(p0q0));

        // 3. Calcul de t = (n ⋅ (Q - P0)) / (n ⋅ v)
        double t = alignZero(nP0Q0 / nv);

        // On ne retourne l'Intersection que si t > 0 (le point est devant le rayon)
        if (t > 0) {
            return List.of(new Intersection(this, ray.getPoint(t)));
        }

        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plane other)) return false;
        return _point.equals(other._point) && _normal.equals(other._normal);
    }

    @Override
    public String toString() {
        return "Plane{point=" + _point + ", normal=" + _normal + "}";
    }
}