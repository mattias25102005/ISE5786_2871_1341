package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Classe représentant une source de lumière ponctuelle (Point Light).
 */
public class PointLight extends Light implements LightSource {

    /** Position de la source ponctuelle */
    private final Point _position;

    /** Coefficient constant d'atténuation (kc) */
    private double _kC = 1.0;
    /** Coefficient linéaire d'atténuation (kl) */
    private double _kL = 0.0;
    /** Coefficient quadratique d'atténuation (kq) */
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

    /**
     * Définit le coefficient kc (constante) pour l'atténuation.
     * @param kC coefficient constant
     * @return this pour chaînage
     */
    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    /**
     * Définit le coefficient kl (linéaire) pour l'atténuation.
     * @param kL coefficient linéaire
     * @return this pour chaînage
     */
    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    /**
     * Définit le coefficient kq (quadratique) pour l'atténuation.
     * @param kQ coefficient quadratique
     * @return this pour chaînage
     */
    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    /**
     * Calcule l'intensité reçue en un point p en tenant compte de l'atténuation.
     * @param p point cible où l'on mesure l'intensité
     * @return couleur atténuée reçue au point p
     */
    @Override
    public Color getIntensity(Point p) {
        double d = _position.distance(p);
        // Formule d'atténuation : I_0 / (kc + kl*d + kq*d^2)
        double factor = _kC + _kL * d + _kQ * d * d;
        return getIntensity().reduce((int) factor);
    }

    /**
     * Retourne le vecteur normalisé allant de la source vers le point p.
     * @param p point cible
     * @return vecteur normalisé L
     */
    @Override
    public Vector getL(Point p) {
        // Le vecteur allant de la source vers le point p, normalisé
        return p.subtract(_position).normalize();
    }
}