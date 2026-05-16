package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe Composite pour regrouper plusieurs formes géométriques
 */
public class Geometries extends Intersectable {
    /** Internal list of geometries contained in this composite. */
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

    // --- MODIFICATIONS POUR LE DESIGN NVI (IMAGE 1) ---

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;

        for (Intersectable item : geometries) {
            // IMPORTANT : On appelle la méthode PUBLIQUE calcIntersections
            // selon la consigne 3.ד.ב de l'image
            var itemIntersections = item.calcIntersections(ray);

            if (itemIntersections != null) {
                // Initialisation paresseuse
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(itemIntersections);
            }
        }

        return result;
    }
}