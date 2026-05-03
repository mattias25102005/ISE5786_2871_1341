package lighting;

import primitives.Color;

/**
 * Classe représentant la lumière ambiante (Immuable)
 */
public class AmbientLight {
    // Le champ doit être final pour l'immutabilité
    private final Color _intensity;

    // Constante initialisée avec un objet AmbientLight de couleur noire
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    // Constructeur qui initialise la couleur
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    // Getter pour récupérer l'intensité
    public Color getIntensity() {
        return _intensity;
    }
}