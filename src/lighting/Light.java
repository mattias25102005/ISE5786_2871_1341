package lighting;

import primitives.Color;

abstract class Light {
    // Le champ est protected et final (rappel de l'énoncé)
    protected final Color _intensity;

    // Constructeur protected pour initialiser l'intensité
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    // Le getter classique (getter de base qui ne dépend d'aucun point)
    public Color getIntensity() {
        return _intensity;
    }
}