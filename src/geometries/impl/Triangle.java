package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.*;

/**
 * Class representing a triangle in 3D Cartesian coordinates.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // 1. On réutilise la logique du plan pour trouver l'intersection
        // On récupère le plan du triangle (hérité de Polygon)
        List<Point> intersections = _plane.findIntersections(ray);

        if (intersections == null) return null;

        // 2. Vérification si le point est à l'intérieur du triangle
        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        // On utilise EXACTEMENT tes noms de variables (vertices sans underscore si c'est le cas dans Polygon)
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);
        Vector v3 = _vertices.get(2).subtract(p0);

        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // Si tous ont le même signe, le point est dans le triangle
        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            // CRITIQUE : On doit changer la géométrie de l'intersection de "Plane" à "this" (Triangle)
            return List.of(new Intersection(this, intersections.get(0)));
        }

        return null;
    }
}