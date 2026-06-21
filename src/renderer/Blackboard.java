package renderer;

import primitives.Double3;
import primitives.SamplingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe utilitaire générique responsable de la génération de points d'échantillonnage 2D (offsets).
 * Cette entité est réutilisable pour toutes les fonctionnalités de super-sampling.
 */
public class Blackboard {

    private static final Random RANDOM_GENERATOR = new Random();

    /**
     * Génère une liste de décalages 2D (offsets) répartis selon le type d'échantillonnage demandé.
     * Les points sont distribués dans une zone carrée de côté 1, centrée sur (0,0).
     *
     * @param sampleCount Le nombre total de points souhaités
     * @param type        Le type de pattern (GRID, RANDOM, JITTERED)
     * @return Liste de coordonnées 2D stockées dans des Double3 (z restera à 0)
     */
    public static List<Double3> generateSamples(int sampleCount, SamplingType type) {
        List<Double3> samples = new ArrayList<>();

        // Si on ne veut qu'un seul échantillon, c'est le centre exact (0, 0)
        if (sampleCount <= 1) {
            samples.add(Double3.ZERO);
            return samples;
        }

        // --- CAS 1 : SAMPLING RANDOM PUR (Indépendant de la grille) ---
        if (type == SamplingType.RANDOM) {
            for (int k = 0; k < sampleCount; k++) {
                // Éparpille les points de façon totalement désordonnée entre -0.5 et 0.5
                double x = RANDOM_GENERATOR.nextDouble() - 0.5;
                double y = RANDOM_GENERATOR.nextDouble() - 0.5;
                samples.add(new Double3(x, y, 0));
            }
            return samples;
        }

        // --- CAS 2 : SAMPLING STRUCTURÉ (GRID OU JITTERED) ---
        int n = (int) Math.sqrt(sampleCount);
        if (n * n != sampleCount) {
            // Si le nombre n'est pas un carré parfait, on prend l'entier le plus proche
            n = (int) Math.round(Math.sqrt(sampleCount));
        }

        // Taille d'une petite case de la grille
        double step = 1.0 / n;

        // Parcours de la grille pour calculer les centres ou perturbations de chaque sous-case
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double xOffset = 0.5; // Comportement GRID par défaut (milieu de la sous-case)
                double yOffset = 0.5;

                if (type == SamplingType.JITTERED) {
                    // Aléatoire confiné de façon contrôlée dans sa propre case [0, 1[
                    xOffset = RANDOM_GENERATOR.nextDouble();
                    yOffset = RANDOM_GENERATOR.nextDouble();
                }

                // Calcul du décalage final par rapport à un repère centré en (0,0)
                double x = (j + xOffset) * step - 0.5;
                double y = (i + yOffset) * step - 0.5;

                samples.add(new Double3(x, y, 0));
            }
        }

        return samples;
    }
}