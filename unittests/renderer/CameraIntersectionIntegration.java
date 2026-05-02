package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

// Importation de l'interface de base
import geometries.api.Intersectable;

// Importation des implémentations spécifiques (le .impl est crucial ici)
import geometries.impl.Sphere;
import geometries.impl.Plane;
import geometries.impl.Triangle;

import primitives.*;
import java.util.List;

/**
 * Integration tests for Camera and Geometries intersections.
 */
public class CameraIntersectionIntegration {

    /**
     * Helper method to count intersections for all rays from a camera through a geometry.
     *
     * * @param camera   The camera
     * @param geometry The geometry to test
     * @param expected Expected total number of intersections
     */
    private void assertIntersectionsCount(Camera camera, Intersectable geometry, int expected) {
        int count = 0;
        int nX = 3; // L'énoncé suggère une résolution 3x3 pour ces tests
        int nY = 3;

        // On boucle sur tous les pixels (Act)
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                Ray ray = camera.constructRay(j, i);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }

        // Vérification du résultat (Assert)
        assertEquals(expected, count, "Wrong number of intersections");
    }

    @Test
    public void testCameraRaySphereIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3);

        // TC01: Sphère devant la caméra (2 intersections)
        Camera camera1 = builder.build();
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2);

        // TC02: Grosse sphère englobant tout le champ de vision (18 intersections)
        // 9 rayons x 2 points d'intersection par rayon = 18
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 2.5), 18);
        // TC03: Sphère moyenne (10 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -2), 2), 9);
    }
    @Test
    public void testCameraRayPlaneIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3);
        Camera camera = builder.build();

        // TC01: Plan perpendiculaire à vTo (9 intersections)
        // Tous les rayons de la grille 3x3 touchent le plan
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9);

        // TC02: Plan incliné (9 intersections)
        // Le plan est penché mais couvre toujours tout le champ de vision
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, -2)), 9);

        // TC03: Plan fortement incliné (6 intersections)
        // Le plan est tellement incliné que les rayons du bas ne le touchent pas
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -5), new Vector(0, 1, -1)), 6);
    }
    @Test
    public void testCameraRayTriangleIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3);
        Camera camera = builder.build();

        // TC01: Petit triangle (1 intersection)
        // Seul le rayon central (1,1) passe à travers
        assertIntersectionsCount(camera, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1);

        // TC02: Grand triangle vertical (2 intersections)
        // Le triangle est haut et fin, deux rayons le traversent
        assertIntersectionsCount(camera, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2);
    }
}
