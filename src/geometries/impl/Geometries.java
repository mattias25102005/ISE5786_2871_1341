package geometries.impl;
import geometries.api.Intersectable;
import primitives.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe Composite pour regrouper plusieurs formes géométriques
 */
public class Geometries extends Intersectable {
    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Constructeur vide
     */
    public Geometries() {}

    /**
     * Constructeur avec une liste d'objets
     * @param geometries Liste d'objets intersectables
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Ajoute des objets à la collection
     * @param geometries Objets à ajouter
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable item : geometries) {
            var itemIntersections = item.findIntersections(ray);

            if (itemIntersections != null) {
                // Initialisation paresseuse (Lazy Initialization)
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(itemIntersections);
            }
        }

        // Retourne null si aucune intersection n'a été trouvée
        return result;
    }
}