package renderer;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 * @author Dan Zilberstein
 */
class TransparencyReflectionTests {
    /** Default constructor to satisfy JavaDoc generator */
    TransparencyReflectionTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene          _scene         = new Scene("Test scene");
    /** Camera builder for the tests with triangles */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    @SuppressWarnings("java:S109")
    void testTwoSpheres() {
        _scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50D).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setKT(0.3)), //
                new Sphere(new Point(0, 0, -50), 25D).setEmission(new Color(RED)) //
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
        _scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004).setKq(0.0000006));

        _cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(150, 150) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("refractionTwoSpheres");
    }

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    @SuppressWarnings("java:S109")
    void testTwoSpheresOnMirrors() {
        _scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400D).setEmission(new Color(0, 50, 100)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
                                .setKT(new Double3(0.5, 0, 0))), //
                new Sphere(new Point(-950, -900, -1000), 200D).setEmission(new Color(100, 50, 20)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(1)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(new Double3(0.5, 0, 0.4))));
        _scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26), Double3.ONE));
        _scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001).setKq(0.000005));

        _cameraBuilder
                .setLocation(new Point(0, 0, 10000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(10000).setVpSize(2500, 2500) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("reflectionTwoSpheresMirrored");
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    @SuppressWarnings("java:S109")
    void testTrianglesTransparentSphere() {
        _scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Sphere(new Point(60, 50, -50), 30D).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.6)));
        _scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38), Double3.ONE));
        _scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        _cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(200, 200) //
                .setResolution(600, 600) //
                .build() //
                .renderImage() //
                .writeToImage("refractionShadow");
    }
    /**
     * Production d'une image complexe personnalisée (Partie 3 - Point 8)
     * Mixant au moins 4 géométries avec ombres partielles, réflexion et réfraction.
     */
    @org.junit.jupiter.api.Test
    void testCustomSceneFinalMagnifiqueEtRapide() {
        // Fond de scène sombre
        _scene.setAmbientLight(new AmbientLight(new Color(10, 10, 10), Double3.ONE));

        _scene.geometries.add(
                // 1. Sol miroir Glossy - Effet flou lisse (GRID 9)
                new geometries.impl.Triangle(new Point(-200, -60, -150), new Point(200, -60, -150), new Point(0, -60, 100))
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.2).setShininess(30).setKR(0.8)
                                .setBlurReflectionRadius(4.0) // Flou élégant
                                .setSampleCount(9)            // 9 rayons en GRID = fini les grains, c'est lisse !
                                .setSamplingType(SamplingType.GRID)),

                // 2. Sphère principale au centre - Verre dépoli Diffuse (GRID 9)
                new geometries.impl.Sphere(new Point(0, 0, -50), 40D).setEmission(new Color(0, 30, 150))
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.4).setShininess(100).setKT(0.7)
                                .setBlurRefractionRadius(3.5) // Bel effet givré opaque
                                .setSampleCount(9)            // Échantillonnage GRID pour adoucir la sphère
                                .setSamplingType(SamplingType.GRID)),

                // 3. Petite sphère opaque et brillante nichée derrière à gauche
                new geometries.impl.Sphere(new Point(-45, -20, -100), 18D).setEmission(new Color(150, 0, 0))
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5).setShininess(80)
                                .setBlurReflectionRadius(2.0)
                                .setSampleCount(4)            // Léger flou rapide
                                .setSamplingType(SamplingType.GRID)),

                // 4. Seconde petite sphère miroir à droite (Miroir pur, très rapide)
                new geometries.impl.Sphere(new Point(45, -20, -80), 15D).setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material()
                                .setKD(0.1).setKS(0.9).setShininess(150).setKR(0.7)
                                .setBlurReflectionRadius(0)   // On la laisse nette pour économiser un max de temps
                                .setSampleCount(1))
        );

        // Sources de lumières directionnelles
        _scene.lights.add(
                new lighting.SpotLight(new Color(700, 400, 400), new Point(60, 80, 100), new Vector(-0.6, -0.8, -2))
                        .setKl(4E-5).setKq(2E-7)
        );
        _scene.lights.add(
                new lighting.PointLight(new Color(300, 300, 300), new Point(-100, 150, 50))
                        .setKl(0.0005).setKq(0.00005)
        );

        // Configuration de la caméra avec SUPER SAMPLING de l'image intégrée
        _cameraBuilder
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(1000).setVpSize(200, 200)
                .setResolution(600, 600)   // Le sweet spot : plus net que 400, mais 2x plus rapide que 800 !
                .setSuperSampling(4)       // Anti-aliasing activé pour lisser tous les contours de la scène
                .build()
                .renderImage()
                .writeToImage("customSceneFinalMagnifique");
    }
}
