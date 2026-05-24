package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Classe représentant un projecteur (Spot Light), héritant de PointLight.
 */
public class SpotLight extends PointLight {

    /** Direction du faisceau (normalisée) */
    private final Vector _direction;
    /** Exposant de concentration du faisceau (narrow beam) */
    private double _narrowBeam = 1.0;

    /**
     * Constructeur pour le projecteur.
     * @param intensity L'intensité d'origine
     * @param position La position du spot
     * @param direction La direction du faisceau (sera normalisée)
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this._direction = direction.normalize();
    }

    /**
     * Définit l'exposant de concentration du faisceau (Bonus/Sharp Spot).
     * @param narrowBeam exposant de concentration
     * @return ce SpotLight pour le chaînage
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this._narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double dirL = _direction.dotProduct(l);

        // Si le point est en dehors ou derrière le cône d'éclairage du spot
        if (dirL <= 0) {
            return Color.BLACK;
        }

        // Modèle de Phong étendu : (dir . l)^narrowBeam
        double factor = Math.pow(dirL, _narrowBeam);

        // Multiplie l'intensité atténuée par la distance (via PointLight) par le facteur du spot
        return super.getIntensity(p).scale(factor);
    }
}