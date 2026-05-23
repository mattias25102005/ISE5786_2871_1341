package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class DirectionalLight extends Light implements LightSource {

    private final Vector _dir;

    /**
     * Constructeur pour une lumière directionnelle.
     * @param intensity L'intensité de la lumière
     * @param dir Le vecteur direction de la lumière
     */
    public DirectionalLight(Color intensity, Vector dir) {
        super(intensity);
        this._dir = dir.normalize(); // L'énoncé demande explicitement de normaliser avant de stocker
    }

    @Override
    public Color getIntensity(Point p) {
        // Pour une lumière directionnelle, l'intensité est la même partout dans la scène
        return getIntensity();
    }

    @Override
    public Vector getL(Point p) {
        // Le vecteur L est la direction de la lumière arrivant sur le point.
        // Comme la lumière va "dans le sens" de _dir, le vecteur allant de la source vers le point est simplement _dir.
        return _dir;
    }
}