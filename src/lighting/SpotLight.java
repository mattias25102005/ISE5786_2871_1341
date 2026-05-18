package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class SpotLight extends PointLight {
    private final Vector direction;
    private int narrowBeam = 1;

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    public SpotLight setNarrowBeam(int narrowBeam) {
        this.narrowBeam = narrowBeam;
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
        // Appelle l'intensité de base calculée par PointLight (déjà atténuée par la distance)
        Color pointLightIntensity = super.getIntensity(p);

        // Direction de la source vers le point p
        Vector emissionDir = getL(p).scale(-1);

        double dirDotL = direction.dotProduct(emissionDir);

        if (dirDotL <= 0) {
            return Color.BLACK;
        }

        if (narrowBeam > 1) {
            dirDotL = Math.pow(dirDotL, narrowBeam);
        }

        return pointLightIntensity.scale(dirDotL);
    }
}