package geometries.impl;

import static primitives.Util.isZero;

import java.util.List;
import primitives.*;
import static primitives.Util.*;
import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * @author Dan Zilberstein
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
     * <p>
     * The vertices must:
     * </p>
     * <ul>
     * <li>Contain at least three points</li>
     * <li>Be ordered along the polygon edge path</li>
     * <li>Lie in the same plane</li>
     * <li>Form a convex polygon</li>
     * </ul>
     * @param  vertices                 polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex
     *                                  polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size     = vertices.length;

        // Create the supporting plane using the first three vertices.
        // The plane stores the constant normal of the polygon.
        _plane    = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return; // no need for more tests for a Triangle

        Vector  n        = _plane.getNormal(vertices[0]);
        // Subtracting identical vertices would create a zero vector (illegal)
        Vector  edge1    = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector  edge2    = vertices[0].subtract(vertices[_size - 1]);

        // Cross product of consecutive edges determines orientation.
        // All edge pairs must produce the same sign relative to the normal,
        // otherwise the polygon is concave or vertices are unordered.
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            // Test that the point is in the same plane as calculated originally
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            // Test the consequent edges have
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) { return _plane.getNormal(point); }

    @Override
    public List<Point> findIntersections(Ray ray) {
        // 1. Intersection avec le plan du polygone
        var intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.getOrigin();
        Vector v = ray.getDirection();

        // 2. Préparation des vecteurs v_i (depuis l'origine du rayon vers chaque sommet)
        // On crée une liste de vecteurs pour la boucle
        int size = _vertices.size();
        Vector[] vectors = new Vector[size];
        for (int i = 0; i < size; i++) {
            vectors[i] = _vertices.get(i).subtract(p0);
        }

        // 3. Calcul des normales n_i pour chaque arête (v_i CROSS v_i+1)
        // Et calcul du produit scalaire avec la direction du rayon
        double[] signals = new double[size];
        for (int i = 0; i < size; i++) {
            // On prend le sommet actuel et le suivant (avec modulo pour revenir au premier à la fin)
            Vector n = vectors[i].crossProduct(vectors[(i + 1) % size]).normalize();
            signals[i] = alignZero(v.dotProduct(n));
        }

        // 4. Vérification du signe : ils doivent TOUS être strictement positifs ou TOUS strictement négatifs
        boolean allPositive = true;
        boolean allNegative = true;

        for (double s : signals) {
            if (isZero(s)) return null; // Le point est sur une arête ou un sommet
            if (s > 0) allNegative = false;
            if (s < 0) allPositive = false;
        }

        // Si le signe est constant, le point est à l'intérieur
        if (allPositive || allNegative) {
            return intersections;
        }

        return null;
    }
}