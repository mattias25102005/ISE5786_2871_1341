package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.SpotLight;
import primitives.*;

/**
 * Tests unitaires pour la classe SpotLight (Exigence 6).
 */
class SpotLightTests {

    @Test
    void testGetIntensity() {
        Point lightPos = new Point(0, 0, 0);
        Vector lightDir = new Vector(0, 0, 1); // Éclaire vers l'axe +Z
        Color baseColor = new Color(100, 100, 100);

        // Utilisation du chaînage de méthodes fluide propre à SpotLight (Exigence 7.E)
        SpotLight light = new SpotLight(baseColor, lightPos, lightDir).setKc(1).setKl(0).setKq(0);

        // Cas 1 : Le point est pile dans l'axe à une distance de 2 (d=2, produit scalaire cosinus = 1)
        Point targetInFront = new Point(0, 0, 2);
        assertEquals(baseColor, light.getIntensity(targetInFront),
                "L'intensité du SpotLight face au faisceau devrait être maximale");

        // Cas 2 : Le point est derrière la source (produit scalaire <= 0), aucune lumière reçue
        Point targetBehind = new Point(0, 0, -2);
        assertEquals(Color.BLACK, light.getIntensity(targetBehind),
                "Le point situé derrière le SpotLight ne devrait recevoir aucune intensité");
    }

    @Test
    void testGetL() {
        Point lightPos = new Point(0, 0, 0);
        Vector lightDir = new Vector(0, 0, 1);
        SpotLight light = new SpotLight(new Color(255, 255, 255), lightPos, lightDir);

        Point target = new Point(3, 0, 0);
        Vector expectedL = new Vector(1, 0, 0); // Direction normalisée vers le point cible

        assertEquals(expectedL, light.getL(target),
                "Le vecteur L du SpotLight est incorrect");
    }
}