
package geometries.api; // Adapte selon ton package exact
import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * Interface pour tous les objets géométriques pouvant être intersectés par un rayon.
 */
public abstract class Intersectable {
    /**
     * Trouve toutes les intersections entre un rayon et la forme géométrique.
     * @param ray Le rayon incident
     * @return Une liste de points d'intersection, ou null s'il n'y en a pas.
     */
    public abstract List<Point> findIntersections(Ray ray);
}