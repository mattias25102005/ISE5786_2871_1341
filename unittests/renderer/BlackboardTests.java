package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import primitives.Double3;
import primitives.SamplingType;

/**
 * Unit tests for renderer.Blackboard class
 */
public class BlackboardTests {

    @Test
    public void testSingleSample() {
        // Utilisation de la nouvelle signature qui retourne des Double3
        List<Double3> samples = Blackboard.generateSamples(1, SamplingType.GRID);

        // On s'attend à avoir exactement 1 point
        assertEquals(1, samples.size(), "Un seul échantillon aurait dû être généré");

        // Ce point doit être le centre (0, 0, 0) -> Double3.ZERO
        assertEquals(Double3.ZERO, samples.get(0), "L'échantillon unique doit être à (0,0,0)");
    }

    @Test
    public void testGridDistribution() {
        int sampleCount = 9; // Grille 3x3
        List<Double3> samples = Blackboard.generateSamples(sampleCount, SamplingType.GRID);

        // 1. Vérifier le nombre total d'éléments
        assertEquals(9, samples.size(), "La grille doit contenir exactement 9 points");

        // 2. Vérifier les limites de la boîte [-0.5, 0.5] grâce aux accesseurs publics de Double3
        for (Double3 sample : samples) {
            assertTrue(sample._d1() >= -0.5 && sample._d1() <= 0.5, "La coordonnée X est hors limites");
            assertTrue(sample._d2() >= -0.5 && sample._d2() <= 0.5, "La coordonnée Y est hors limites");
            assertEquals(0.0, sample._d3(), 0.00001, "La coordonnée Z doit rester à 0");
        }

        // 3. S'assurer que les points sont distincts
        assertNotEquals(samples.get(0), samples.get(1), "Les points générés doivent être distincts");
    }
}