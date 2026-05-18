package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {
    protected final Point position;
    protected double kC = 1.0;
    protected double kL = 0.0;
    protected double kQ = 0.0;

    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = position.distance(p);
        double attenuation = kC + kL * d + kQ * d * d;

        // Sécurité pour éviter une division par zéro (cas théorique limite)
        if (attenuation == 0) return getIntensity();

        // CORRECTION : On utilise .scale(1.0 / attenuation) à la place de reduce
        return getIntensity().scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        return position.subtract(p).normalize();
    }
}