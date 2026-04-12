package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for geometries.impl.Triangle class
 */
class TriangleTests {

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here
        Triangle tr = new Triangle(
            new Point(0, 0, 1), 
            new Point(1, 0, 1), 
            new Point(0, 1, 1)
        );
        double sqrt3 = Math.sqrt(1d / 3);
        Point p = new Point(0.2, 0.2, 1);
        Vector normal = tr.getNormal(p);

        // 1. Vérifier que la normale n'est pas nulle
        assertNotNull(normal, "getNormal() returned null");

        // 2. Vérifier que la normale est de longueur 1 (Unit vector)
        assertEquals(1, normal.length(), 0.00000001, "Triangle normal is not a unit vector");

        // 3. Vérifier la direction (Le triangle est sur le plan Z=1, donc normale = (0,0,1) ou (0,0,-1))
        assertTrue(normal.equals(new Vector(0, 0, 1)) || normal.equals(new Vector(0, 0, -1)), 
                   "Bad normal for triangle");
    }
}