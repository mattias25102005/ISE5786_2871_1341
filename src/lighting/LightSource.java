package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface représentant une source de lumière externe dans la scène.
 */
public interface LightSource {

    /**
     * Calcule et retourne l'intensité de la lumière qui atteint le point p.
     * @param p Le point éclairé
     * @return L'intensité sous forme de Color
     */
    public Color getIntensity(Point p);

    /**
     * Calcule et retourne le vecteur direction de la lumière arrivant sur le point p (normalisé).
     * @param p Le point éclairé
     * @return Le vecteur direction normalisé (L)
     */
    public Vector getL(Point p);

    /**
     * Calcule la distance entre la source de lumière et un point donné.
     * @param point Le point par rapport auquel on cherche la distance
     * @return La distance sous forme de double
     */
    public double getDistance(Point point);
}