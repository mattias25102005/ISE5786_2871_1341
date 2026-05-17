package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.*;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 */
public class Polygon extends Geometry {
    /** Ordered list of polygon vertices */
    protected final List<Point> _vertices;
    /** Plane containing the polygon */
    protected final Plane       _plane;
    /** Number of vertices */
    private final int     _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     * @param vertices ordered list of vertices (at least 3)
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size     = vertices.length;

        _plane    = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return;

        Vector  n        = _plane.getNormal(vertices[0]);
        Vector  edge1    = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector  edge2    = vertices[0].subtract(vertices[_size - 1]);

        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) { return _plane.getNormal(point); }

    // --- MODIFICATIONS POUR LE DESIGN NVI ---

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // 1. Intersection avec le plan du polygone
        // IMPORTANT : On appelle la méthode publique calcIntersections du plan
        var intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        // 2. Préparation des vecteurs v_i (sommets - origine du rayon)
        int size = _vertices.size();
        Vector[] vectors = new Vector[size];
        for (int i = 0; i < size; i++) {
            vectors[i] = _vertices.get(i).subtract(p0);
        }

        // 3. Calcul des normales n_i et vérification des signes
        double[] signals = new double[size];
        for (int i = 0; i < size; i++) {
            // n = (v_i x v_i+1)
            Vector n = vectors[i].crossProduct(vectors[(i + 1) % size]).normalize();
            signals[i] = alignZero(v.dotProduct(n));
        }

        boolean allPositive = true;
        boolean allNegative = true;        Vector v1 = _vertices.get(0).subtract(p0);


        for (double s : signals) {
            if (isZero(s)) return null; // Le point est sur une arête
            if (s > 0) allNegative = false;
            if (s < 0) allPositive = false;
        }

        if (allPositive || allNegative) {
            // On retourne l'Intersection en remplaçant la géométrie du plan par 'this' (le polygone)
            return List.of(new Intersection(this, intersections.get(0)));
        }

        return null;
    }
}