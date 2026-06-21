package renderer;

import org.junit.jupiter.api.Test;
import lighting.*;
import primitives.*;
import scene.Scene;
import geometries.impl.Sphere;


import org.junit.jupiter.api.Test;
import geometries.impl.Sphere;
import geometries.impl.Triangle; // Si tu veux ajouter des miroirs témoins plus tard
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Classe de test pour générer la sphère avec un effet Glossy métallique prononcé.
 */
public class GlossySphereTests {

    @Test
    public void testGlossySphereMetallic() {
        // --- 1. CONFIGURATION DE LA SCÈNE ---
        Scene scene = new Scene("GlossySphereScene");
        scene.setBackground(Color.BLACK); // Fond noir pur pour éliminer les distractions
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 10), Double3.ONE));

        // --- 2. DÉFINITION DU MATÉRIAU MÉTALLIQUE GLOSSY TRÈS PRONONCÉ ---
        Material metallicGlossyMat = new Material()
                .setKD(new Double3(0.05))            // Presque aucune diffusion (caractéristique du métal pur)
                .setKS(new Double3(0.95))            // Spéculaire extrêmement intense pour capturer les sources lumineuses
                .setShininess(300)                   // Très poli, reflet initial très concentré
                .setKR(new Double3(0.9))             // Énergie de réflexion globale à 90%
                .setBlurReflectionRadius(4.5)        // Rayon de flou augmenté pour rendre l'effet glossy flagrant
                .setSampleCount(9)                   // Gardé à 9 pour tester rapidement, monte à 64 ou 81 pour le rendu final
                .setSamplingType(SamplingType.JITTERED);

        // --- 3. AJOUT DE LA SPHÈRE À LA SCÈNE ---
        scene.geometries.add(
                new Sphere(new Point(0, 0, -100), 40)
                        .setMaterial(metallicGlossyMat)
        );

        // --- 4. AJOUT DE LUMIÈRES MULTIPLES ---
        // Des lumières ponctuelles stratégiques pour créer des points de brillance floutés visibles sur la sphère
        scene.lights.add(new PointLight(new Color(255, 255, 255), new Point(60, 60, -40))
                .setKl(0.001).setKq(0.0002));

        scene.lights.add(new PointLight(new Color(255, 150, 50), new Point(-60, -60, -50)) // Lumière orangée contrastante
                .setKl(0.001).setKq(0.0002));

        scene.lights.add(new DirectionalLight(new Color(30, 30, 40), new Vector(-1, -1, -1)));

        // --- 5. CONFIGURATION DE LA CAMÉRA ---
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 100))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(200, 200)
                .setResolution(400, 400) // 400x400 : bon compromis vitesse/netteté pour le test
                .setVpDistance(100)
                .setImageWriter(new ImageWriter(400, 400)) // Version corrigée sans String
                .setRayTracer(new SimpleRayTracer(scene))
                .build();

        // --- 6. GÉNÉRATION DE L'IMAGE ---
        camera.renderImage();
        camera.writeToImage("metallic_glossy_sphere");
    }
}