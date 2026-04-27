package geometries.impl;
import primitives.*;
import java.util.List;
import static primitives.Util.*;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Class representing a sphere in 3D Cartesian coordinates.
 */
public class Sphere extends RadialGeometry {

    /**
     * Center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from center and radius.
     *
     * @param center center point
     * @param radius radius
     */
    public Sphere(final Point center, final double radius) {
        super(radius);
        this._center = center;
    }

    /**
     * Returns the normal vector to the sphere at a given point.
     *
     * @param point point on the sphere
     * @return normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        // Formule : Normal = (P - Center) / ||P - Center||
        return point.subtract(_center).normalize();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sphere other)) return false;
        return _center.equals(other._center) && _radius == other._radius;
    }

    @Override
    public String toString() {
        return "Sphere{center=" + _center + ", radius=" + _radius + "}";
    }


    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Le rayon commence au centre : le seul point est à une distance r
            return List.of(ray.getPoint(_radius));
        }

        double tm = alignZero(v.dotProduct(l));
        double dSquared = alignZero(l.lengthSquared() - tm * tm);
        double rSquared = _radius * _radius;

        // Si la distance au centre est >= au rayon, pas d'intersection (ou tangence)
        if (alignZero(dSquared - rSquared) >= 0) return null;

        double th = alignZero(Math.sqrt(rSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // On ne garde que les points strictement devant le rayon (t > 0)
        if (t2 <= 0) return null; // Les deux points sont derrière ou sur l'origine

        if (t1 > 0) {
            // Le rayon traverse la sphère (2 points devant)
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        } else {
            // Le rayon est à l'intérieur de la sphère (seul t2 est devant)
            return List.of(ray.getPoint(t2));
        }
    }

}