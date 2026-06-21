
package renderer;

import org.junit.jupiter.api.Test;
import lighting.*;
        import primitives.*;
        import scene.Scene;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import geometries.impl.Plane;

/**
 * Version purement spéculaire (sans aucun flou glossy et sans composante diffuse).
 * Tous les reflets sont nets et de type miroir parfait.
 */
public class AracadeSceneSans {

    @Test
    public void testArcadeScene() {
        // ==================== CONFIGURATION DE LA SCÈNE ====================
        Scene scene = new Scene("ArcadeScene_PureSpecular");
        scene.setBackground(new Color(10, 10, 20));
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 40), new Double3(0.2)));

        // ==================== MATÉRIAUX 100% SPÉCULAIRES (SANS DIFFUSE, SANS GLOSSY) ====================

        // 1. Anciennes sphères mates -> Converties en surfaces réfléchissantes nettes
        Material sharpSpecularMat1 = new Material()
                .setKD(new Double3(0.0))               // <-- Plus de composante diffuse
                .setKS(new Double3(0.85))
                .setShininess(300)
                .setKR(new Double3(0.5))               // Réflexion nette
                .setBlurReflectionRadius(0.0)
                .setSampleCount(1);

        // 2. Anciennes sphères glossy -> Converties en miroirs parfaits et nets
        Material sharpSpecularMat2 = new Material()
                .setKD(new Double3(0.0))               // <-- Plus de composante diffuse
                .setKS(new Double3(0.9))
                .setShininess(400)
                .setKR(new Double3(0.75))              // Forte réflexion
                .setBlurReflectionRadius(0.0)          // <-- Plus de flou glossy
                .setSampleCount(1);                    // 1 seul rayon requis

        // 3. Autres sphères brillantes -> Nettoyées également
        Material shinyMat = new Material()
                .setKD(new Double3(0.0))               // <-- Plus de composante diffuse
                .setKS(new Double3(0.95))
                .setShininess(500)
                .setKR(new Double3(0.65))
                .setBlurReflectionRadius(0.0)          // Reflet 100% net
                .setSampleCount(1);

        // Matériau pour la pyramide purement spéculaire
        Material pyramidMat = new Material()
                .setKD(new Double3(0.0))               // <-- Plus de composante diffuse
                .setKS(new Double3(0.85))
                .setShininess(200)
                .setKR(new Double3(0.4))
                .setBlurReflectionRadius(0.0)          // Reflet net
                .setSampleCount(1);

        // Sol miroir PARFAIT
        Material mirrorFloor = new Material()
                .setKD(new Double3(0.0))               // <-- Plus de composante diffuse
                .setKS(new Double3(0.95))
                .setShininess(1000)
                .setKR(new Double3(0.95))
                .setBlurReflectionRadius(0.0)
                .setSampleCount(1);

        // ==================== CRÉATION DES 8 SPHÈRES ====================
        double arcRadius = 60.0;
        double arcDistance = -80.0;
        double[] sphereRadii = {8, 7.5, 9, 6.5, 7, 8.5, 7.5, 8};
        Color[] sphereColors = {
                new Color(255, 60, 60),   // Rouge
                new Color(60, 255, 60),   // Vert
                new Color(60, 60, 255),   // Bleu
                new Color(255, 255, 60),  // Jaune
                new Color(255, 60, 255),  // Magenta
                new Color(60, 255, 255),  // Cyan
                new Color(255, 130, 40),  // Orange
                new Color(160, 60, 160)   // Violet
        };

        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI / 8) * i;
            double x = arcRadius * Math.cos(angle);
            double y = 20 + 15 * Math.sin(angle * 2);
            double z = arcDistance + arcRadius * Math.sin(angle) * 0.3;

            Point sphereCenter = new Point(x, y, z);
            Material currentSphereMat;

            if (i < 2) {
                currentSphereMat = sharpSpecularMat1;
            } else if (i < 4) {
                currentSphereMat = sharpSpecularMat2;
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
                .setSuperSampling(4) // Gardé pour avoir des contours géométriques lisses
                .build();

        camera.renderImage();
        camera.writeToImage("arcade_scene_pure_specular"); // <-- Nouveau nom de fichier
    }
}