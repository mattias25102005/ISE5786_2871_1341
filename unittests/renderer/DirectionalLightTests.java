package lighting;

import org.junit.jupiter.api.Test;
import primitives.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe DirectionalLight basés sur les principes du cours.
 */
public class DirectionalLightTests {

    /**
     * Test de la méthode getIntensity pour une lumière directionnelle.
     * La lumière directionnelle (comme le Soleil) ne s'atténue pas avec la distance,
     * l'intensité doit être identique en tout point de la scène.
     */
    @Test
    public void testGetIntensity() {
        Color lightColor = new Color(255, 128, 0);
        Vector direction = new Vector(0, 0, -1);
        DirectionalLight light = new DirectionalLight(lightColor, direction);

        // On teste sur un point arbitraire de la scène
        Point p = new Point(10, -50, 300);

        assertEquals(lightColor, light.getIntensity(p),
                "L'intensité d'une lumière directionnelle doit rester constante et égale à l'intensité d'origine");
    }

    /**
     * Test de la méthode getL pour une lumière directionnelle.
     * Le vecteur retourné doit pointer du point vers la source de lumière (vecteur inversé).
     */
    @Test
    public void testGetL() {
        Vector initialDirection = new Vector(1, 1, 1);
        DirectionalLight light = new DirectionalLight(new Color(255, 255, 255), initialDirection);

        Point p = new Point(0, 5, -20);

        // CORRECTION : On fait le calcul étape par étape pour aider l'IDE et respecter le framework
        Vector scaledVector = initialDirection.scale(-1);
        Vector expectedL = scaledVector.normalize();

        // Le vecteur l doit être normalisé et inversé par rapport à la direction d'émission
        assertEquals(expectedL, light.getL(p),
                "Le vecteur getL doit pointer du point vers la lumière (direction d'émission inversée)");
        assertEquals(1.0, light.getL(p).length(), 0.00001,
                "Le vecteur getL doit être unitaire (normalisé)");
    }
}