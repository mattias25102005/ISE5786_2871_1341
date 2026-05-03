package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;
import renderer.ImageWriter;

/**
 * Test pour la classe ImageWriter basé sur les instructions de la partie 3
 */
public class ImageWriterTests {

    // Constantes selon image_196561.png
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private static final int GRID_SIZE = 50;

    private final Color backgroundColor = new Color(255, 255, 0); // Jaune
    private final Color gridColor = new Color(255, 0, 0);       // Rouge

    @Test
    public void testImageWriter() {
        // 1. Création de l'objet ImageWriter (nx, ny) selon votre code
        ImageWriter imageWriter = new ImageWriter(WIDTH, HEIGHT);

        // 2. Double boucle pour parcourir les pixels (800x500)
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {

                // 3. Logique de la grille : une ligne tous les 50 pixels
                if (i % GRID_SIZE == 0 || j % GRID_SIZE == 0) {
                    imageWriter.writePixel(i, j, gridColor);
                } else {
                    imageWriter.writePixel(i, j, backgroundColor);
                }
            }
        }

        // 4. Sauvegarde de l'image avec le nom du fichier en paramètre
        imageWriter.writeToImage("testYellowGrid");
    }
}