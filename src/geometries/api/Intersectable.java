package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import java.util.List;
import java.util.Objects;

/**
 * Interface pour les objets pouvant être intersectés par un rayon.
 */
public abstract class Intersectable {

    /**
     * Default constructor for Intersectable (documented to satisfy Javadoc).
     */
    protected Intersectable() {}

    /**
     * PDS pour lier une géométrie à un point d'intersection.
     */
    public static class Intersection {
        /** La géométrie touchée */
        public final Geometry geometry;
        /** Le point d'intersection */
        public final Point point;
        /** Le matériau de la géométrie au point d'intersection */
        public final Material material;

        /**
         * Constructeur d'intersection (MODIFIÉ selon l'étape 3.ב)
         * @param geometry la géométrie intersectée
         * @param point le point d'intersection
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;

            // Logique demandée : si la géométrie est null, on utilise un Material par défaut.
            // Sinon, on récupère le matériau de la géométrie.
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
     * Méthode NVI - Appelée par le moteur de rendu
     * @param ray le rayon utilisé pour calculer les intersections
     * @return liste d'Intersection ou null si aucune intersection
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Méthode abstraite à implémenter dans les formes (Sphere, Plane, etc.)
     * @param ray le rayon à tester
     * @return liste d'Intersection ou null si aucune
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Retourne uniquement les points d'intersection.
     * @param ray le rayon à tester
     * @return liste de points d'intersection ou null si aucune
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream().map(inter -> inter.point).toList();
    }
}