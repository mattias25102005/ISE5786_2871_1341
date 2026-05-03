package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import primitives.*;

/** Unit tests for Geometries composite. */
public class GeometriesTests {

    /** Default constructor for tests. */
    public GeometriesTests() {}


    /** Test composite findIntersections behavior. */
    @Test
    public void testFindIntersections() {
        // --- LES OBJETS DU TEST ---
        // 1. Un triangle à plat sur Z = 1
        Triangle triangle = new Triangle(
                new Point(-10, -10, 1),
                new Point(10, -10, 1),
                new Point(0, 10, 1));

        // 2. Une sphère centrée plus haut, entre Z=2 et Z=4 (rayon 1, centre à Z=3)
        Sphere sphere = new Sphere(new Point(0, 0, 3), 1d);

        // 3. Un plan encore plus haut à Z=5
        Plane plane = new Plane(new Point(0, 0, 5), new Vector(0, 0, 1));

        // On regroupe tout dans le composite
        Geometries composite = new Geometries(sphere, triangle, plane);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Rayon qui part de Z = -0.5 et monte tout droit
        // Il doit toucher : Triangle (Z=1), Sphère entrée (Z=2), Sphère sortie (Z=4)
        Ray ray = new Ray(new Point(0, 0, -0.5), new Vector(0, 0, 1));

        List<Point> result = composite.findIntersections(ray);

        assertNotNull(result, "L'intersection ne devrait pas être null");

        assertEquals(4, result.size(), "On devrait avoir exactement 4 points (1 triangle + 2 sphère)");

        // =============== Boundary Values Tests ==================

        // TC11: Liste de formes vide
        assertNull(new Geometries().findIntersections(ray), "BVA: Empty list");

        // TC12: Aucune forme n'est coupée (Rayon qui part sur le côté)
        assertNull(composite.findIntersections(new Ray(new Point(20, 20, 20), new Vector(1, 1, 1))),
                "BVA: No intersection");

        // TC13: Une seule forme est coupée (On vise juste le Plan à Z=5, loin sur le côté)
        assertEquals(1, composite.findIntersections(new Ray(new Point(20, 20, 0), new Vector(0, 0, 1))).size(),
                "BVA: Only one shape (Plane)");

        // TC14: Toutes les formes sont coupées
        // On garde le même rayon mais on s'assure que le Plan est aussi sur son chemin
        // (Dans ce setup, le plan est déjà à Z=5 au dessus de tout, donc il sera touché !)
        assertEquals(4, composite.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))).size(),
                "BVA: All shapes (Triangle + Sphere + Plane)");
    }
}