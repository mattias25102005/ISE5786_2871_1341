package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import geometries.impl.Tube; // Assure-toi que cet import est là

/**
 * Unit tests for geometries.impl.Tube class
 */
class TubeTests {

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Simple test of Tube normal
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        // ATTENTION : Dans ton Tube.java, le rayon (1.0) est AVANT l'axe (ray)
        Tube tube = new Tube(1.0, ray);

        // Un point à la surface (distance 1 de l'axe Z, sur l'axe X)
        Vector normal = tube.getNormal(new Point(1, 0, 5));

        // La normale doit être (1, 0, 0)
        assertEquals(new Vector(1, 0, 0), normal, "Bad normal for Tube");

        // =============== Boundary Values Tests ==================
        // TC11: Le point est à 90 degrés du point de départ du rayon (projection t=0)
        // C'est un cas limite important demandé par la Page 3
        Vector normalBVA = tube.getNormal(new Point(1, 0, 0));
        assertEquals(new Vector(1, 0, 0), normalBVA, "Bad normal for Tube (boundary t=0)");
    }
}