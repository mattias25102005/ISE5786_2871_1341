package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

class PlaneTests {
    private final double DELTA = 0.000001;

    /** Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Plane pl = new Plane(p0, new Vector(0, 0, 1));
        
        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple test - point quelconque sur le plan
        assertEquals(new Vector(0, 0, 1), pl.getNormal(new Point(1, 1, 1)), "Bad normal for plane (EP)");

        // =============== Boundary Values Tests ==================
        // TC11: Test avec le point p0 qui a servi à définir le plan (Exigence Page 6)
        assertDoesNotThrow(() -> pl.getNormal(p0), "getNormal failed with the reference point p0");
        assertEquals(new Vector(0, 0, 1), pl.getNormal(p0), "Bad normal at reference point p0");
    }

    /** Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}. */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple valid plane
        assertDoesNotThrow(() -> new Plane(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0)),
                "Failed to construct a valid plane");

        // =============== Boundary Values Tests ==================
        // TC11: Three points on the same line (Colinéaires)
        assertThrows(IllegalArgumentException.class, 
            () -> new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
            "Constructed a plane with collinear points");

        // TC12: Two points are the same (Points identiques) - Exigence Page 6
        assertThrows(IllegalArgumentException.class,
            () -> new Plane(new Point(1, 2, 3), new Point(1, 2, 3), new Point(0, 1, 0)),
            "Constructed a plane with identical points");
    }
}