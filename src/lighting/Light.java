package lighting;

import primitives.Color;

/**
 * Représente une source lumineuse abstraite : encapsule l'intensité de base.
 * Les classes concrètes (PointLight, DirectionalLight, SpotLight, etc.) étendent
 * cette classe pour fournir des comportements spécifiques.
 */
abstract class Light {
    /** L'intensité de la source (couleur) */
    protected final Color _intensity;

    /**
     * Constructeur protégé pour initialiser l'intensité de la lumière.
     * @param intensity couleur/intensité de la source
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Retourne l'intensité (couleur) de la source.
     * @return la couleur/intensité de la lumière
     */
    public Color getIntensity() {
        return _intensity;
    }
}