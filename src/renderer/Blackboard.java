package renderer;

import primitives.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire générique responsable de la génération de points d'échantillonnage 2D (offsets).
 * Cette entité est réutilisable pour toutes les fonctionnalités de super-sampling.
 */
public class Blackboard {

    /**
     * Génère une liste de points 2D répartis selon une grille régulière (Grid Pattern).
     * Les points sont distribués uniformément dans une zone carrée de côté 1, centrée sur (0,0).
     * * @param sampleCount Le nombre total de points souhaités (doit être un carré parfait pour une grille, ex: 9, 16, 64, 81...)
     * @return Liste de coordonnées 2D (décalages entre -0.5 et 0.5)
     */
    public static List<Point> generateGridSamples(int sampleCount) {
        List<Point> samples = new ArrayList<>();

        // Si on ne veut qu'un seul échantillon, c'est le centre exact (0, 0)
        if (sampleCount <= 1) {
            samples.add(new Point(0, 0, 0)); // On utilise ta classe Point (z restera à 0 en 2D)
            return samples;
        }

        // On calcule le nombre de mailles par côté (la racine carrée du nombre de points)
        int n = (int) Math.sqrt(sampleCount);
        if (n * n != sampleCount) {
            // Si le nombre n'est pas un carré parfait, on prend l'entier le plus proche pour ne pas planter
            n = (int) Math.round(Math.sqrt(sampleCount));
        }

        // Taille d'une petite case de la grille
        double step = 1.0 / n;

        // Parcours de la grille pour calculer les centres de chaque sous-case
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Calcul du centre de la cellule (i, j) par rapport à un repère centré en (0,0)
                double x = (j + 0.5) * step - 0.5;
                double y = (i + 0.5) * step - 0.5;

                samples.add(new Point(x, y, 0));
            }
        }

        return samples;
    }
}