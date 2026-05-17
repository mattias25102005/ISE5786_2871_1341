package geometries.impl;

import geometries.api.Geometry; // Ajouté pour la clarté
import primitives.*;
import java.util.List;
import static primitives.Util.*;

/**
 * Class representing a sphere in 3D Cartesian coordinates.
 */
public class Sphere extends Geometry { // Hérite de Geometry (qui hérite d'Intersectable)

    /** Center point of the sphere */
    private final Point _center;
    /** Radius of the sphere */
    private final double _radius; // Assure-toi que c'est accessible via RadialGeometry ou ici

    /**
     * Constructs a sphere with center and radius.
     * @param center center point
     * @param radius sphere radius
     */
    public Sphere(final Point center, final double radius) {
        this._center = center;
        this._radius = radius;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    // --- MODIFICATIONS POUR LE MODÈLE NVI ---

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Le rayon commence au centre de la sphère : une seule intersection à la distance du rayon
            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        double tm = alignZero(v.dotProduct(l));
        double dSquared = alignZero(l.lengthSquared() - tm * tm);
        double rSquared = _radius * _radius;

        // Si la distance au carré est supérieure ou égale au rayon au carré, pas d'intersection
        if (alignZero(dSquared - rSquared) >= 0) return null;

        double th = alignZero(Math.sqrt(rSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Seuls les t > 0 sont devant le rayon
        boolean t1Valid = t1 > 0;
        boolean t2Valid = t2 > 0;

        if (t1Valid && t2Valid) {
            return List.of(
                    new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2))
            );
        } else if (t1Valid) {
            return List.of(new Intersection(this, ray.getPoint(t1)));
        } else if (t2Valid) {
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }

        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sphere other)) return false;
        return _center.equals(other._center) && isZero(_radius - other._radius);
    }
}