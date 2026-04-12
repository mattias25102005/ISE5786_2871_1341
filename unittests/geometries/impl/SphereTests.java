package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

class SphereTests {

    /** Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here
        Sphere sphere = new Sphere(new Point(0, 0, 1), 1.0);
        Vector normal = sphere.getNormal(new Point(0, 0, 2));
        
        // La normale doit être (0,0,1)
        assertEquals(new Vector(0, 0, 1), normal, "Bad normal for sphere");
    }
}