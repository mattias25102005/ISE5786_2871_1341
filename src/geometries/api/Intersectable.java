package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import java.util.Objects;

/**
 * Interface pour les objets pouvant être intersectés par un rayon.
 */
public abstract class Intersectable {

    protected Intersectable() {}

    /**
     * PDS pour lier une géométrie à un point d'intersection avec système de cache.
     */
    public static class Intersection {
        /** La géométrie touchée */
        public final Geometry geometry;
        /** Le point d'intersection */
        public final Point point;
        /** Le matériau de la géométrie au point d'intersection */
        public final Material material;

        // --- CHAMPS DE CACHE GEOMETRIQUE (Partie B - Point 6.א à 6.ד) ---
        /** Vecteur normal à la surface au point d'intersection */
        public Vector n;
        /** Vecteur reliant la caméra/observateur au point d'intersection */
        public Vector v;
        /** Vecteur directionnel provenant de la source de lumière courante */
        public Vector l;
        /** Produit scalaire utile pour les calculs de diffusion (n . l) */
        public double nDotL;

        /**
         * Constructeur d'intersection.
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = (geometry == null) ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Intersection other)) return false;
            return this.geometry == other.geometry && Objects.equals(this.point, other.point);
        }

        @Override
        public String toString() {
            return "Intersection{geometry=" + geometry + ", point=" + point + "}";
        }
    }

    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream().map(inter -> inter.point).toList();
    }
}