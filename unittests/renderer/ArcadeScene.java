package renderer;

import org.junit.jupiter.api.Test;
import lighting.*;
import primitives.*;
import scene.Scene;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import geometries.impl.Plane;

/**
 * Version optimisée pour un rendu beaucoup plus rapide,
 * avec un super-sampling léger (grille 2x2) pour casser l'effet d'escalier.
 */
public class ArcadeScene {

    @Test
    public void testArcadeScene() {
        // ==================== CONFIGURATION DE LA SCÈNE ====================
        Scene scene = new Scene("ArcadeScene_Optimized_Smooth");
        scene.setBackground(new Color(10, 10, 20));
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 40), new Double3(0.2)));

        // ==================== MATÉRIAUX OPTIMISÉS ====================

        // 1. Purement MAT (Calcul ultra rapide : pas de rayons secondaires)
        Material diffuseMat = new Material()
                .setKD(new Double3(0.85))
                .setKS(new Double3(0.0))
                .setShininess(1);

        // 2. GLOSSY OPTIMISÉ (Échantillonnage réduit pour accélérer le rendu)
        Material highGlossyMat = new Material()
                .setKD(new Double3(0.2))
                .setKS(new Double3(0.8))
                .setShininess(180)
                .setKR(new Double3(0.6))
                .setBlurReflectionRadius(2.5)
                .setSampleCount(18)                   // Réduit de 64 à 18 : DIVISE LE TEMPS PAR 3.5 !
                .setSamplingType(SamplingType.JITTERED);

        // 3. Brillant Standard (Rapide)
        Material shinyMat = new Material()
                .setKD(new Double3(0.3))
                .setKS(new Double3(0.7))
                .setShininess(200)
                .setKR(new Double3(0.4))
                .setBlurReflectionRadius(0.5)
                .setSampleCount(9);                   // Réduit à 9 rayons

        // Matériau pour la pyramide
        Material pyramidMat = new Material()
                .setKD(new Double3(0.5))
                .setKS(new Double3(0.4))
                .setShininess(100)
                .setKR(new Double3(0.15))
                .setBlurReflectionRadius(1.0)
                .setSampleCount(9);

        // Sol miroir PARFAIT (Très rapide : 1 seul rayon par pixel)
        Material mirrorFloor = new Material()
                .setKD(new Double3(0.05))
                .setKS(new Double3(0.95))
                .setShininess(500)
                .setKR(new Double3(0.85))
                .setBlurReflectionRadius(0.0)
                .setSampleCount(1);

        // ==================== CRÉATION DES 8 SPHÈRES ====================
        double arcRadius = 60.0;
        double arcDistance = -80.0;
        double[] sphereRadii = {8, 7.5, 9, 6.5, 7, 8.5, 7.5, 8};
        Color[] sphereColors = {
                new Color(255, 60, 60),   // Rouge  -> MAT 1
                new Color(60, 255, 60),   // Vert   -> MAT 2
                new Color(60, 60, 255),   // Bleu   -> GLOSSY 1
                new Color(255, 255, 60),  // Jaune  -> GLOSSY 2
                new Color(255, 60, 255),  // Magenta-> Brillant
                new Color(60, 255, 255),  // Cyan   -> Brillant
                new Color(255, 130, 40),  // Orange -> Brillant
                new Color(160, 60, 160)   // Violet -> Brillant
        };

        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI / 8) * i;
            double x = arcRadius * Math.cos(angle);
            double y = 20 + 15 * Math.sin(angle * 2);
            double z = arcDistance + arcRadius * Math.sin(angle) * 0.3;

            Point sphereCenter = new Point(x, y, z);
            Material currentSphereMat;

            if (i < 2) {
                currentSphereMat = diffuseMat;
            } else if (i < 4) {
                currentSphereMat = highGlossyMat;
            } else {
                currentSphereMat = shinyMat;
            }

            scene.geometries.add(
                    new Sphere(sphereCenter, sphereRadii[i])
                            .setMaterial(currentSphereMat)
                            .setEmission(sphereColors[i].reduce(4))
            );
        }

        // ==================== PYRAMIDE ET SOL ====================
        double pyramidBaseSize = 6;
        Point pyramidCenter = new Point(25, 0, -80);
        Point pyramidApex = new Point(25, 15, -80);

        Point p0 = new Point(-pyramidBaseSize, -pyramidBaseSize, -pyramidBaseSize).add(pyramidCenter.subtract(Point.ZERO));
        Point p1 = new Point(pyramidBaseSize, -pyramidBaseSize, -pyramidBaseSize).add(pyramidCenter.subtract(Point.ZERO));
        Point p2 = new Point(pyramidBaseSize, -pyramidBaseSize, pyramidBaseSize).add(pyramidCenter.subtract(Point.ZERO));
        Point p3 = new Point(-pyramidBaseSize, -pyramidBaseSize, pyramidBaseSize).add(pyramidCenter.subtract(Point.ZERO));

        scene.geometries.add(new Triangle(p0, p1, p2).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p0, p2, p3).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p0, pyramidApex, p1).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p1, pyramidApex, p2).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p2, pyramidApex, p3).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p3, pyramidApex, p0).setMaterial(pyramidMat));

        Plane ground = (Plane) new Plane(new Point(0, -20, 0), new Vector(0, 1, 0)).setMaterial(mirrorFloor);
        scene.geometries.add(ground);

        // ==================== LUMIÈRES ====================
        scene.lights.add(new PointLight(new Color(255, 255, 255), new Point(40, 50, -40)).setKl(0.001).setKq(0.0002));
        scene.lights.add(new PointLight(new Color(255, 200, 100), new Point(-40, 40, -100)).setKl(0.001).setKq(0.0002));
        scene.lights.add(new DirectionalLight(new Color(80, 80, 100), new Vector(-0.5, -0.8, -0.3)));
        scene.lights.add(new SpotLight(new Color(150, 100, 255), new Point(0, 60, -60), new Vector(0, -1, 0)).setKl(0.0005).setKq(0.0001).setNarrowBeam(0.1));

        // ==================== CONFIGURATION DE LA CAMÉRA ====================
        int size = 800;

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 30, 50))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setVpSize(300, 300)
                .setVpDistance(260)
                .setResolution(size, size)
                .setImageWriter(new ImageWriter(size, size))
                .setRayTracer(new SimpleRayTracer(scene))
                .setSuperSampling(4) // <-- Léger lissage ajouté (grille 2x2), très rapide à calculer
                .build();

        camera.renderImage();
        camera.writeToImage("arcade_scene_optimized_smooth");
    }
}