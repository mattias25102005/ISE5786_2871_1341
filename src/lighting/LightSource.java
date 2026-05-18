package lighting;

import primitives.Color;
import primitives.Vector;
import primitives.Point;

/**
 * Interface représentant une source de lumière dans la scène.
 */
public interface LightSource {

    /**
     * Calcule et retourne l'intensité de la lumière qui atteint un point spécifique.
     * * @param p Le point de la scène qui reçoit la lumière
     * @return La couleur (intensité) de la lumière sur ce point
     */
    Color getIntensity(Point p);

    /**
     * Calcule et retourne le vecteur directionnel normalisé de la lumière
     * arrivant sur un point spécifique.
     * * @param p Le point éclairé
     * @return Le vecteur directionnel unitaire (de la source vers le point)
     */
    Vector getL(Point p);
}