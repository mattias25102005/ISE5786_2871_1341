package geometries.impl;
import primitives.*;
import java.util.List;
import static primitives.Util.*;
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
    @Override
    public List<Point> findIntersections(Ray ray) {
        // 1. On vérifie d'abord l'intersection avec le plan du triangle
        var intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        // 2. On récupère les 3 sommets
        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        // 3. Calcul des vecteurs entre l'origine et les sommets
        Vector v1 = p1.subtract(p0);
        Vector v2 = p2.subtract(p0);
        Vector v3 = p3.subtract(p0);

        // 4. Calcul des normales aux plans formés par les arêtes
        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        // 5. Produit scalaire pour vérifier la position relative
        double d1 = alignZero(v.dotProduct(n1));
        double d2 = alignZero(v.dotProduct(n2));
        double d3 = alignZero(v.dotProduct(n3));

        // Le point est à l'intérieur si tous les signes sont identiques (tous > 0 ou tous < 0)
        // Les cas d'égalité à zéro (sur l'arête) retournent null selon tes BVA
        if ((d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0)) {
            return intersections;
        }

        return null;
    }
}
