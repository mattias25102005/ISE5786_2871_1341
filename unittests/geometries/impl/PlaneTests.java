package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
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
    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the plane (1 point)
        List<Point> result = plane.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 1)));
        assertEquals(1, result.size(), "EP: Ray should intersect plane");
        assertEquals(new Point(1, 1, 1), result.get(0), "EP: Wrong intersection point");

        // TC02: Ray does not intersect the plane (0 points)
        // Le rayon commence au-dessus du plan et monte (s'éloigne)
        assertNull(plane.findIntersections(new Ray(new Point(0, 1, 2), new Vector(1, 0, 1))),
                "EP: Ray points away from plane, no intersection");

        // =============== Boundary Values Tests ==================

        // ---- Group: Ray is parallel to the plane ----

        // TC11: Ray included in the plane (0 points selon les règles du Ray Tracing)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                "BVA: Ray parallel and included in plane");

        // TC12: Ray not included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                "BVA: Ray parallel and not included");

        // ---- Group: Ray is orthogonal to the plane ----

        // TC13: Ray starts before the plane (1 point)
        assertEquals(1, plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))).size(),
                "BVA: Ray orthogonal and starts before plane");

        // TC14: Ray starts in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                "BVA: Ray orthogonal and starts in plane");

        // TC15: Ray starts after the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "BVA: Ray orthogonal and starts after plane");

        // ---- Group: Special cases ----

        // TC16: Ray starts at the plane (but not orthogonal/parallel)
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 1), new Vector(1, 1, 1))),
                "BVA: Ray starts at the plane boundary");

        // TC17: Ray starts at the reference point Q of the plane
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))),
                "BVA: Ray starts at the plane's reference point");
    }
}