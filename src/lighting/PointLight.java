package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Classe représentant une source de lumière ponctuelle (Point Light).
 */
public class PointLight extends Light implements LightSource {

    private final Point _position;

    // Coefficients d'atténuation initiaux (Exigences 7.A & 7.B)
    private double _kC = 1.0;
    private double _kL = 0.0;
    private double _kQ = 0.0;

    /**
     * Constructeur pour la lumière ponctuelle.
     * @param intensity L'intensité d'origine de la lumière
     * @param position La position de la lumière
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this._position = position;
    }

    // Méthodes de chaînage / Fluent API (Exigence 7.D)
    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = _position.distance(p);
        // Formule d'atténuation : I_0 / (kc + kl*d + kq*d^2)
        double factor = _kC + _kL * d + _kQ * d * d;
        return getIntensity().reduce((int) factor);
    }

    @Override
    public Vector getL(Point p) {
        // Le vecteur allant de la source vers le point p, normalisé
        return p.subtract(_position).normalize();
    }
}