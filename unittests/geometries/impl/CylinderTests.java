package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

class CylinderTests {

    /** Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cyl = new Cylinder(1.0, ray, 2.0);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Point sur le côté latéral
        assertEquals(new Vector(1, 0, 0), cyl.getNormal(new Point(1, 0, 1)), "Bad normal for cylinder side");

        // TC02: Point sur la base supérieure
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0.5, 0, 2)), "Bad normal for cylinder top base");

        // TC03: Point sur la base inférieure
        assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0.5, 0, 0)), "Bad normal for cylinder bottom base");

        // =============== Boundary Values Tests ==================
        // TC11: Point au centre de la base supérieure
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0, 0, 2)), "Bad normal for cylinder top center");

        // TC12: Point au centre de la base inférieure
        assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0, 0, 0)), "Bad normal for cylinder bottom center");
    }
}