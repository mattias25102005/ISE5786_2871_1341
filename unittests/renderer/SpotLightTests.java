package lighting;

import org.junit.jupiter.api.Test;
import primitives.*;
import static org.junit.jupiter.api.Assertions.*;

public class SpotLightTests {

    @Test
    public void testGetIntensity() {
        // Spot positionné en (0,0,100) pointant droit vers le bas (0,0,-1)
        SpotLight spot = new SpotLight(new Color(500, 500, 500), new Point(0, 0, 100), new Vector(0, 0, -1));
        spot.setKl(0.001).setKq(0.0001);

        // Point sur l'axe
        Point targetOnAxis = new Point(0, 0, 0); // distance = 100, cos = 1
        // 500 / (1 + 0.001*100 + 0.0001*10000) = 500 / (1 + 0.1 + 1) = 500 / 2.1
        Color expectedOnAxis = new Color(500, 500, 500).reduce(2.1);
        assertEquals(expectedOnAxis, spot.getIntensity(targetOnAxis));

        // Point derrière
        Point targetBehind = new Point(0, 0, 150);
        assertEquals(Color.BLACK, spot.getIntensity(targetBehind));
    }
}