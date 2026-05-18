package lighting;

import org.junit.jupiter.api.Test;
import primitives.*;
import static org.junit.jupiter.api.Assertions.*;

public class PointLightTests {

    @Test
    public void testGetL() {
        PointLight light = new PointLight(new Color(255, 255, 255), new Point(0, 0, 100));
        Point target = new Point(0, 0, 0);

        Vector l = light.getL(target);

        // Du point (0,0,0) vers la lumière (0,0,100) -> direction (0,0,1)
        assertEquals(new Vector(0, 0, 1), l, "Le vecteur getL doit pointer du point vers la lumière");
        assertEquals(1.0, l.length(), 0.00001, "Le vecteur doit être unitaire");
    }

    @Test
    public void testGetIntensity() {
        PointLight light = new PointLight(new Color(500, 500, 500), new Point(0, 0, 100));
        light.setKl(0.001).setKq(0.0002);

        Point target = new Point(0, 0, 0); // distance = 100

        // 500 / (1 + 0.001*100 + 0.0002*10000) = 500 / (1 + 0.1 + 2) = 500 / 3.1
        Color expectedColor = new Color(500, 500, 500).reduce(3.1);

        assertEquals(expectedColor, light.getIntensity(target));
    }
}