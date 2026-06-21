package renderer;

import org.junit.jupiter.api.Test;
import geometries.impl.*;
import lighting.*;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Étape 5 : Matrice de performances sur une scène 3D complexe et esthétique.
 * Version avec 5 sources de lumière, 7 sphères avancées et 1 CUBE (sans getters X,Y,Z).
 */
class MiniProject2PerformanceTest {

    private static final int    RESOLUTION = 1001;
    private static final double SIZE       = 500D;
    private static final double DISTANCE   = 150D;

    @Test
    void executePerformanceMatrixCustomScene() {
        // -------------------------------------------------------------------------
        // 1. CRÉATION DE LA SCÈNE ET CONFIGURATION DES 5 LUMIÈRES OBLIGATOIRES
        // -------------------------------------------------------------------------
        Scene scene = new Scene("Magnificent Custom Scene");
        scene.setBackground(new Color(10, 15, 25)); // Fond bleu nuit très sombre

        // Lumière 1 : Lumière Ambiante
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), 0.15));

        // Lumière 2 : Lumière Directionnelle (Dorée)
        scene.lights.add(new DirectionalLight(
                new Color(200, 180, 150),
                new Vector(1, -1, -1)
        ));

        // Lumière 3 : PointLight Rouge (Gauche)
        scene.lights.add(new PointLight(
                new Color(300, 0, 0),
                new Point(-150, 100, -50)
        ).setKl(0.0001).setKq(0.00001));

        // Lumière 4 : PointLight Bleue (Droite)
        scene.lights.add(new PointLight(
                new Color(0, 200, 300),
                new Point(150, 100, -50)
        ).setKl(0.0001).setKq(0.00001));

        // Lumière 5 : SpotLight (Centre)
        scene.lights.add(new SpotLight(
                new Color(400, 400, 300),
                new Point(0, 200, 50),
                new Vector(0, -1, -2)
        ).setKl(0.0001).setKq(0.00001));

        // -------------------------------------------------------------------------
        // 2. GÉOMÉTRIES ET MATÉRIAUX AVANCÉS (7 Sphères + Sol + 1 Cube)
        // -------------------------------------------------------------------------
        Material floorMaterial = new Material().setKD(0.4).setKS(0.3).setShininess(10);

        // Le Sol
        scene.geometries.add(
                new Triangle(new Point(-300, -80, -100), new Point(300, -80, -100), new Point(0, -80, -300))
                        .setMaterial(floorMaterial).setEmission(new Color(20, 20, 30)),
                new Triangle(new Point(-300, -80, -100), new Point(0, -80, -300), new Point(-400, -80, -200))
                        .setMaterial(floorMaterial).setEmission(new Color(15, 15, 25))
        );

        // Sphère centrale 1 : Effet Miroir
        scene.geometries.add(new Sphere(new Point(0, 0, -150), 50D)
                .setMaterial(new Material().setKD(0.2).setKS(1.0).setShininess(100).setKR(0.8))
                .setEmission(new Color(10, 10, 10)));

        // Sphère gauche 2 : Effet Verre 1
        scene.geometries.add(new Sphere(new Point(-90, -20, -120), 30D)
                .setMaterial(new Material().setKD(0.2).setKS(0.8).setShininess(80).setKT(0.7))
                .setEmission(new Color(0, 20, 0)));

        // Sphère droite 3 : Métal brillant (Cuivre)
        scene.geometries.add(new Sphere(new Point(90, -20, -120), 30D)
                .setMaterial(new Material().setKD(0.5).setKS(0.8).setShininess(50))
                .setEmission(new Color(80, 50, 20)));

        // Sphère arrière gauche 4 : Émeraude
        scene.geometries.add(new Sphere(new Point(-60, 40, -180), 25D)
                .setMaterial(new Material().setKD(0.6).setKS(0.5).setShininess(60).setKR(0.3))
                .setEmission(new Color(10, 60, 20)));

        // Sphère avant-plan 5 : Petite sphère sombre
        scene.geometries.add(new Sphere(new Point(0, -40, -90), 15D)
                .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(30).setKT(0.2))
                .setEmission(new Color(40, 40, 40)));

        // Sphère avant gauche 6 : Transparente pure
        scene.geometries.add(new Sphere(new Point(-45, -50, -100), 20D)
                .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(100).setKT(0.85))
                .setEmission(new Color(5, 5, 5)));

        // Sphère arrière droite 7 : Chrome poli miroir
        scene.geometries.add(new Sphere(new Point(60, 30, -160), 22D)
                .setMaterial(new Material().setKD(0.1).setKS(1.0).setShininess(120).setKR(0.95))
                .setEmission(new Color(15, 15, 15)));

        // AJOUT DU CUBE : Placé à droite au sol (sans utilisation des getters X, Y, Z)
        Material cubeMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(30);
        Color cubeColor = new Color(150, 40, 20); // Rouge brique
        addCubeToScene(scene, new Point(40, -80, -110), 35, cubeMaterial, cubeColor);

        // -------------------------------------------------------------------------
        // 3. CONFIGURATION DE LA CAMÉRA
        // -------------------------------------------------------------------------
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setResolution(RESOLUTION, RESOLUTION)
                .setLocation(new Point(0, 0, 150))
                .setDirection(new Point(0, 0, -150), Vector.AXIS_Y)
                .setVpDistance(DISTANCE)
                .setVpSize(SIZE, SIZE)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        long startTime, endTime;
        int cores = Runtime.getRuntime().availableProcessors();

        // --- Configuration 1 : Sans Optimisation & Sans Threads ---
        Camera camera1 = cameraBuilder
                .setImageWriter(new ImageWriter(RESOLUTION, RESOLUTION))
                .setSuperSampling(64)
                .setAdaptiveSampling(false, 1)
                .setThreadsCount(1)
                .build();

        System.out.println("Rendu 1/4 : Optimisation [OFF] | Multi-Thread [OFF]...");
        startTime = System.currentTimeMillis();
        camera1.renderImage();
        endTime = System.currentTimeMillis();
        camera1.writeToImage("1_CustomScene_NoOpt_NoThreads");
        long timeConfig1 = endTime - startTime;

        // --- Configuration 2 : Sans Optimisation & Avec Threads ---
        Camera camera2 = cameraBuilder
                .setImageWriter(new ImageWriter(RESOLUTION, RESOLUTION))
                .setSuperSampling(64)
                .setAdaptiveSampling(false, 1)
                .setThreadsCount(cores)
                .build();

        System.out.println("Rendu 2/4 : Optimisation [OFF] | Multi-Thread [ON]...");
        startTime = System.currentTimeMillis();
        camera2.renderImage();
        endTime = System.currentTimeMillis();
        camera2.writeToImage("2_CustomScene_NoOpt_WithThreads");
        long timeConfig2 = endTime - startTime;

        // --- Configuration 3 : Adaptive Super-Sampling & Sans Threads ---
        Camera camera3 = cameraBuilder
                .setImageWriter(new ImageWriter(RESOLUTION, RESOLUTION))
                .setAdaptiveSampling(true, 4)
                .setThreadsCount(1)
                .build();

        System.out.println("Rendu 3/4 : Optimisation [ON]  | Multi-Thread [OFF]...");
        startTime = System.currentTimeMillis();
        camera3.renderImage();
        endTime = System.currentTimeMillis();
        camera3.writeToImage("3_CustomScene_Adaptive_NoThreads");
        long timeConfig3 = endTime - startTime;

        // --- Configuration 4 : Adaptive Super-Sampling & Avec Threads ---
        Camera camera4 = cameraBuilder
                .setImageWriter(new ImageWriter(RESOLUTION, RESOLUTION))
                .setAdaptiveSampling(true, 4)
                .setThreadsCount(cores)
                .build();

        System.out.println("Rendu 4/4 : Optimisation [ON]  | Multi-Thread [ON]...");
        startTime = System.currentTimeMillis();
        camera4.renderImage();
        endTime = System.currentTimeMillis();
        camera4.writeToImage("4_CustomScene_Adaptive_WithThreads");
        long timeConfig4 = endTime - startTime;

        // -------------------------------------------------------------------------
        // AFFICHAGE DE LA MATRICE DE TESTS
        // -------------------------------------------------------------------------
        System.out.println("\n=================================================================");
        System.out.println("         MATRICE DE PERFORMANCES : SCÈNE PERSONNALISÉE 3D        ");
        System.out.println("=================================================================");
        System.out.printf("| %-42s | %-12s |\n", "Configuration (Scène Réaliste)", "Temps (ms)");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("| 1. Grille Fixe (64)   & Sans Threads          | %8d ms |\n", timeConfig1);
        System.out.printf("| 2. Grille Fixe (64)   & Avec Threads (%2d)     | %8d ms |\n", cores, timeConfig2);
        System.out.printf("| 3. Adaptive Sampling  & Sans Threads          | %8d ms |\n", timeConfig3);
        System.out.printf("| 4. Adaptive Sampling  & Avec Threads (%2d)     | %8d ms |\n", cores, timeConfig4);
        System.out.println("=================================================================");

        System.out.printf("Gain de l'Adaptive Sampling seul : %.2fx plus rapide%n", (double) timeConfig1 / timeConfig3);
        System.out.printf("Gain du Multi-Threading seul      : %.2fx plus rapide%n", (double) timeConfig1 / timeConfig2);
        System.out.printf("Gain cumulé (Accélération Totale) : %.2fx plus rapide%n", (double) timeConfig1 / timeConfig4);
        System.out.println("=================================================================");
    }

    /**
     * Crée un cube en utilisant uniquement des translations de vecteurs sur la classe Point.
     * Évite d'appeler les méthodes getX(), getY(), getZ().
     */
    private void addCubeToScene(Scene scene, Point p0, double size, Material mat, Color col) {
        // Vecteurs directeurs alignés sur les axes principaux de taille 'size'
        Vector tX = new Vector(size, 0, 0);
        Vector tY = new Vector(0, size, 0);
        Vector tZ = new Vector(0, 0, -size); // Axe Z négatif vers l'arrière de la scène

        // Calcul des 8 points via la méthode .add(Vector) de Point
        Point p1 = p0.add(tX);
        Point p2 = p0.add(tX).add(tY);
        Point p3 = p0.add(tY);

        Point p4 = p0.add(tZ);
        Point p5 = p1.add(tZ);
        Point p6 = p2.add(tZ);
        Point p7 = p3.add(tZ);

        // Ajout des 12 triangles constituant les 6 faces du cube
        scene.geometries.add(
                // Face Avant
                new Triangle(p0, p1, p2).setMaterial(mat).setEmission(col),
                new Triangle(p0, p2, p3).setMaterial(mat).setEmission(col),
                // Face Arrière
                new Triangle(p5, p4, p7).setMaterial(mat).setEmission(col),
                new Triangle(p5, p7, p6).setMaterial(mat).setEmission(col),
                // Face Gauche
                new Triangle(p4, p0, p3).setMaterial(mat).setEmission(col),
                new Triangle(p4, p3, p7).setMaterial(mat).setEmission(col),
                // Face Droite
                new Triangle(p1, p5, p6).setMaterial(mat).setEmission(col),
                new Triangle(p1, p6, p2).setMaterial(mat).setEmission(col),
                // Face Haute
                new Triangle(p3, p2, p6).setMaterial(mat).setEmission(col),
                new Triangle(p3, p6, p7).setMaterial(mat).setEmission(col),
                // Face Basse
                new Triangle(p4, p5, p1).setMaterial(mat).setEmission(col),
                new Triangle(p4, p1, p0).setMaterial(mat).setEmission(col)
        );
    }
}