package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing a plane in 3D Cartesian coordinates.
 */
public final class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private final Point _point;

    /**
     * Normal vector to the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a plane from a point and a normal vector.
     * * @param point  point on the plane
     * @param normal normal vector to the plane
     */
    public Plane(final Point point, final Vector normal) {
        this._point = point;
        // On normalise le vecteur pour s'assurer qu'il a une longueur de 1
        this._normal = normal.normalize();
    }

    /**
     * Constructs a plane from three points.
     * The normal is calculated using the vector product of (p2-p1) and (p3-p1).
     * * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @throws IllegalArgumentException if the points are collinear
     */
    public Plane(final Point p1, final Point p2, final Point p3) {
        this._point = p1;

        // Calcul de deux vecteurs à partir des trois points
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        // La normale est le produit vectoriel de v1 et v2, ensuite normalisé
        // Note: crossProduct lancera une exception si les points sont alignés
        this._normal = v1.crossProduct(v2).normalize();
    }

    @Override
    public Vector getNormal(final Point point) {
        return _normal;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plane other)) return false;
        return _point.equals(other._point)
                && _normal.equals(other._normal);
    }

    @Override
    public String toString() {
        return "Plane{point=" + _point + ", normal=" + _normal + "}";
    }
}