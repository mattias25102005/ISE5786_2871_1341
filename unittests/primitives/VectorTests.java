package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Vector class
 */
class VectorTests {
    private final double DELTA = 0.000001;

    @Test
    void testAdd() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-1, -2, -5);
        // TC01: Simple add
        assertEquals(new Vector(0, 0, -2), v1.add(v2), "add() wrong result");
        
        // BVA: Add vector that results in Zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.add(new Vector(-1, -2, -3)),
            "add() to zero vector should throw exception");
    }

    @Test
    void testSubtract() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(5, 4, 3);
        // TC01: Simple subtract (Exigence du document !)
        assertEquals(new Vector(-4, -2, 0), v1.subtract(v2), "subtract() wrong result");
    }

    @Test
    void testScale() {
        Vector v1 = new Vector(1, 2, 3);
        // TC01: Simple scale
        assertEquals(new Vector(2, 4, 6), v1.scale(2), "scale() wrong result");
        
        // BVA: Scale by 0
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0),
            "scale by 0 should throw exception");
    }

    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        // TC01: Orthogonal vectors
        assertEquals(0, v1.dotProduct(v2), DELTA, "dotProduct() for orthogonal vectors should be 0");
        
        // TC02: Simple dot product
        assertEquals(-2, v1.dotProduct(new Vector(1, 0, -1)), DELTA, "dotProduct() wrong result");
    }

    @Test
    void testCrossProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);

        // EP: Test length and orthogonality
        assertEquals(v1.length() * v2.length(), vr.length(), DELTA, "crossProduct() length is wrong");
        assertTrue(Util.isZero(vr.dotProduct(v1)), "crossProduct() result not orthogonal to v1");
        assertTrue(Util.isZero(vr.dotProduct(v2)), "crossProduct() result not orthogonal to v2");

        // BVA: Parallel vectors
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(new Vector(2, 4, 6)),
            "crossProduct() for parallel vectors should throw exception");
    }

    @Test
    void testLengthSquared() {
        Vector v = new Vector(1, 2, 2);
        // TC01: Simple length squared
        assertEquals(9, v.lengthSquared(), DELTA, "lengthSquared() wrong result");
    }

    @Test
    void testLength() {
        Vector v = new Vector(0, 3, 4);
        // TC01: Simple length
        assertEquals(5, v.length(), DELTA, "length() wrong result");
    }

    @Test
    void testNormalize() {
        Vector v = new Vector(1, 2, 3);
        Vector u = v.normalize();
        // TC01: Test if result is unit vector
        assertEquals(1, u.length(), DELTA, "normalized vector is not a unit vector");
        // TC02: Test if result is parallel to original
        assertThrows(IllegalArgumentException.class, () -> v.crossProduct(u),
            "normalized vector is not parallel to original vector");
    }
}