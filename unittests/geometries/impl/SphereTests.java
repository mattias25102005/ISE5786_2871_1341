package geometries.impl;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
/** Unit tests for Sphere geometry. */
class SphereTests {

    /** Default constructor for tests. */
    public SphereTests() {}

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
    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 1, 0))),
                "Ray's line out of sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        List<Point> result = sphere.findIntersections(new Ray(new Point(-1, 0, 0),
                new Vector(3, 1, 0)));

        assertEquals(2, result.size(), "Wrong number of points");

        // Tri des points pour s'assurer que l'ordre ne fasse pas échouer le test
        // On compare sur l'axe X (en utilisant Double3._d1() car tu n'as pas de getX)
        result = result.stream()
                .sorted(Comparator.comparingDouble(p -> p.distance(new Point(-1,0,0))))
                .toList();

        assertEquals(List.of(p1, p2), result, "Ray crosses sphere - points mismatch");

        // TC03: Ray starts inside the sphere (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0))).size(),
                "Ray starts inside sphere should find 1 intersection");

        // TC04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))),
                "Ray starts after sphere should find 0 intersections");

        // =============== Boundary Values Tests ==================

        // **** Group: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 1))).size(),
                "Ray starts at surface and goes inside");

        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 1))),
                "Ray starts at surface and goes outside");

        // **** Group: Ray's line goes through the center
        // TC13: Ray starts before sphere and passes through center (2 points)
        assertEquals(2, sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0))).size(),
                "Line through center, starts before sphere");

        // TC14: Ray starts at sphere surface and passes through center (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0))).size(),
                "Line through center, starts at surface");

        // TC15: Ray starts inside (not at center) and passes through center (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -0.5, 0), new Vector(0, 1, 0))).size(),
                "Line through center, starts inside sphere");

        // TC16: Ray starts at the center (1 point)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 1, 0))).size(),
                "Ray starts at the center");

        // **** Group: Ray's line is tangent to the sphere
        // TC17: Ray starts before the tangent point (0 points d'après l'énoncé Ray-Tracing)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts before tangent point");

        // TC18: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts at tangent point");

        // TC19: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts after tangent point");

        // **** Group: Special cases
        // TC20: Ray's line is outside, ray is orthogonal to ray start to sphere center line
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(0, 0, 1))),
                "Ray is outside and orthogonal to the line to center");
    }
}