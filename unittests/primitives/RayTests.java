package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {

    /** Default constructor for tests. */
    public RayTests() {}

    /** Test method for {@link primitives.Ray#getOrigin()}. */
    @Test
    void testGetOrigin() {
        Point p = new Point(1, 2, 3);
        Ray ray = new Ray(p, new Vector(0, 0, 1));
        
        // TC01: Simple test
        assertEquals(p, ray.getOrigin(), "getOrigin() failed");
    }

    /** Test method for {@link primitives.Ray#getDirection()}. */
    @Test
    void testGetDirection() {
        Vector v = new Vector(1, 0, 0);
        Ray ray = new Ray(new Point(0, 0, 0), v);
        
        // TC01: Direction should be normalized
        assertEquals(1, ray.getDirection().length(), 0.000001, "Direction should be normalized");
        assertEquals(v.normalize(), ray.getDirection(), "getDirection() failed");
    }
    /** Test point computation along ray for various t values. */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: t est positif (point devant l'origine)
        assertEquals(new Point(2, 1, 1), ray.getPoint(1), "getPoint() pour t > 0 échoué");

        // TC02: t est négatif (point derrière l'origine)
        assertEquals(new Point(0, 1, 1), ray.getPoint(-1), "getPoint() pour t < 0 échoué");

        // =============== Boundary Values Tests ==================
        // TC11: t est égal à zéro (doit retourner l'origine exacte)
        assertEquals(new Point(1, 1, 1), ray.getPoint(0), "getPoint() pour t = 0 doit retourner l'origine");
    }
}