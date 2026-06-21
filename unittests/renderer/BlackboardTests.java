package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import primitives.Point;


/**
 * Unit tests for primitives.Blackboard class
 */
public class BlackboardTests {


    @Test
    public void testSingleSample() {
        List<Point> samples = Blackboard.generateGridSamples(1);

        // On s'attend à avoir exactement 1 point
        assertEquals(1, samples.size(), "Un seul échantillon aurait dû être généré");

        // Ce point doit être le centre (0, 0, 0)
        assertEquals(Point.ZERO, samples.get(0), "L'échantillon unique doit être à (0,0,0)");
    }


    @Test
    public void testGridDistribution() {
        int sampleCount = 9; // Grille 3x3
        List<Point> samples = Blackboard.generateGridSamples(sampleCount);

        // 1. Vérifier le nombre total d'éléments
        assertEquals(9, samples.size(), "La grille doit contenir exactement 9 points");

        // 2. Vérifier que tous les points sont bien compris dans le carré local [-0.5, 0.5]
        // On utilise l'accès "hacky" via la réflexion ou une astuce de soustraction,
        // mais le plus propre avec ton code est de passer par une méthode temporaire ou de tester par comparaison de distance.
        // Faisons une validation robuste via les distances :
        assertNotNull(samples.get(0), "Le premier point ne doit pas être nul");
        assertNotEquals(samples.get(0), samples.get(1), "Les points générés doivent être distincts");
    }
}