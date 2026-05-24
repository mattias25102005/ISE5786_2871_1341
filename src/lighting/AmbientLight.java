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
}