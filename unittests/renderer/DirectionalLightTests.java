package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.DirectionalLight;
import primitives.*;

/**
 * Tests unitaires pour la classe DirectionalLight (Exigence 6).
 */
class DirectionalLightTests {

    @Test
    void testGetIntensity() {
        Color intensity = new Color(255, 255, 255);
        Vector dir = new Vector(0, 0, -1);
        DirectionalLight light = new DirectionalLight(intensity, dir);

        // L'intensité doit être identique peu importe le point dans la scène
        assertEquals(intensity, light.getIntensity(new Point(0, 0, 0)),
                "L'intensité directionnelle doit être constante à l'origine");
        assertEquals(intensity, light.getIntensity(new Point(100, -50, 20)),
                "L'intensité directionnelle doit être constante n'importe où");
    }

    @Test
    void testGetL() {
        Vector dir = new Vector(1, 1, 1);
        DirectionalLight light = new DirectionalLight(new Color(255, 255, 255), dir);

        Point p = new Point(5, 5, 5);
        // getL doit renvoyer la direction normalisée de la lumière (Exigence 7.F)
        assertEquals(dir.normalize(), light.getL(p),
                "Le vecteur L doit correspondre à la direction de la lumière normalisée");
    }
}