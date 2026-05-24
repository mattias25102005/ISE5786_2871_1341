package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.PointLight;
import primitives.*;

/**
 * Tests unitaires pour la classe PointLight (Exigence 6).
 */
class PointLightTests {
    /**
     * Constructeur par défaut pour la classe de tests (évite l'avertissement Javadoc).
     */
    PointLightTests() {}

    /**
     * Teste l'atténuation d'une PointLight en fonction de la distance.
     */
    @Test
    void testGetIntensity() {
        Point lightPos = new Point(0, 0, 0);
        Color baseColor = new Color(120, 120, 120);

        // Configuration avec chaînage : kc = 1, kl = 1, kq = 0
        PointLight light = new PointLight(baseColor, lightPos).setKl(1);

        Point target = new Point(0, 0, 2); // Distance d = 2
        // Facteur d'atténuation = 1 + 1*2 + 0 = 3. Couleur finale attendue = baseColor / 3
        Color expectedColor = baseColor.reduce(3);

        assertEquals(expectedColor, light.getIntensity(target),
                "L'intensité avec atténuation par distance linéaire est incorrecte");
    }

    /**
     * Vérifie que getL renvoie le vecteur normalisé de la source vers le point.
     */
    @Test
    void testGetL() {
        Point lightPos = new Point(0, 0, 0);
        PointLight light = new PointLight(new Color(255, 255, 255), lightPos);

        Point target = new Point(0, 5, 0);
        Vector expectedL = new Vector(0, 1, 0); // Vecteur normalisé allant de (0,0,0) vers (0,5,0)

        assertEquals(expectedL, light.getL(target),
                "Le vecteur L de la PointLight est incorrect");
    }
}