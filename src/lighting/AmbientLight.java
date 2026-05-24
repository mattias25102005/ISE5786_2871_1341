package lighting;

import primitives.Color;

/**
 * Lumière ambiante : éclaire uniformément la scène sans direction.
 */
public class AmbientLight extends Light {

    /** Lumière ambiante nulle (intensité noire, coefficient 0) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, 0.0);

    /**
     * Constructeur principal
     * @param Ia Intensité de base de la couleur
     * @param ka Coefficient d'atténuation de la lumière ambiante
     */
    public AmbientLight(Color Ia, double ka) {
        // Appelle le constructeur de Light avec Ia * ka
        super(Ia.scale(ka));
    }

    /**
     * NOUVEAU CONSTRUCTEUR (Surcharge pour la compatibilité avec les tests du prof)
     * Permet de créer une lumière ambiante avec un coefficient ka par défaut à 1.0
     * @param Ia Intensité de la couleur
     */
    public AmbientLight(Color Ia) {
        super(Ia); // Équivaut à Ia.scale(1.0)
    }
}