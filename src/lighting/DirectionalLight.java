package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Lumière directionnelle : même intensité partout, définie par une direction.
 */
public class DirectionalLight extends Light implements LightSource {

    /** Direction normalisée de la lumière */
    private final Vector _dir;

    /**
     * Constructeur pour une lumière directionnelle.
     * @param intensity L'intensité de la lumière
     * @param dir Le vecteur direction de la lumière (sera normalisé)
     */
    public DirectionalLight(Color intensity, Vector dir) {
        super(intensity);
        this._dir = dir.normalize(); // L'énoncé demande explicitement de normaliser avant de stocker
    }

    /**
     * Pour une lumière directionnelle, l'intensité ne dépend pas du point.
     * @param p point d'évaluation (ignoré)
     * @return intensité de la lumière
     */
    @Override
    public Color getIntensity(Point p) {
        // Pour une lumière directionnelle, l'intensité est la même partout dans la scène
        return getIntensity();
    }

    /**
     * Retourne la direction de la lumière (vecteur L) en un point donné.
     * @param p point cible (non utilisé)
     * @return vecteur directionnel normalisé
     */
    @Override
    public Vector getL(Point p) {
        // Le vecteur L est la direction de la lumière arrivant sur le point.
        // Comme la lumière va "dans le sens" de _dir, le vecteur allant de la source vers le point est simplement _dir.
        return _dir;
    }

    /**
     * Pour une lumière directionnelle, la distance est considérée comme infinie.
     */
    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
}