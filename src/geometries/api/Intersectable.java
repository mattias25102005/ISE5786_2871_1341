package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import lighting.LightSource; // Assure-toi que l'import est présent pour LightSource
import java.util.List;
import java.util.Objects;

/**
 * Interface pour les objets pouvant être intersectés par un rayon.
 */
public abstract class Intersectable {

    /**
     * Constructeur protégé pour Intersectable. Les implémentations concrètes
     * appelleront ce constructeur lors de l'instanciation.
     */
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

        // --- CHAMPS DE CACHE GEOMETRIQUE ET LUMIÈRE (Mis à jour selon l'énoncé) ---
        /** normale en ce point (mise en cache) */
        public Vector normal;
        /** vecteur direction du rayon incident (mise en cache) */
        public Vector v;
        /** produit scalaire entre v et la normale (mise en cache) */
        public double vNormal;
        /** source lumineuse courante (mise en cache) */
        public LightSource light;
        /** vecteur L (direction lumière -> point) mis en cache */
        public Vector l;
        /** produit scalaire entre l et la normale (mise en cache) */
        public double lNormal;

        /**
         * Constructeur d'intersection reliant une géométrie et un point.
         * @param geometry la géométrie touchée
         * @param point le point d'intersection
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

    /**
     * Calcule toutes les intersections (avec informations complètes) pour un rayon donné.
     * @param ray le rayon d'intersection
     * @return liste d'Intersection ou null s'il n'y a pas d'intersection
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Implémentation spécifique par la géométrie pour calculer les intersections.
     * @param ray le rayon à tester
     * @return liste d'intersections (implémentation spécifique)
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Retourne uniquement les points d'intersection (projection des Intersection).
     * @param ray le rayon testé
     * @return liste de points ou null si aucune intersection
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream().map(inter -> inter.point).toList();
    }
}