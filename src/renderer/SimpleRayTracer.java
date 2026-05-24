package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

/**
 * Traceur de rayons simple utilisant le modèle de réflexion de Phong avec gestion des ombres,
 * et enrichi pour le calcul récursif des effets globaux (réflexion et transparence).
 */
public class SimpleRayTracer extends RayTracerBase {

    // Constantes pour le contrôle de la récursion (Partie 2 - Point 2.א)
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Point d'entrée (Point ט) : trouve l'intersection visible la plus proche via la méthode
     * centralisée findClosestIntersection, puis lance le calcul récursif de la couleur.
     */
    @Override
    public Color traceRay(Ray ray) {
        var closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null) {
            return _scene.background;
        }

        // Initialisation de la récursion (Partie 2 - Point 2.ד)
        return _scene.ambientLight.getIntensity()
                .add(calcColor(closestIntersection, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Surcharge récursive pour le calcul de la couleur (Point ו & ז).
     * Additionne l'émission, les effets locaux et les effets globaux (réflexion et transparence).
     */
    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
        if (!preprocessIntersection(intersection, ray)) {
            return Color.BLACK;
        }

        // Émission de la géométrie courante + effets locaux modulés (Partie 3 - Point 4)
        Color color = intersection.geometry.getEmission().add(calcLocalEffects(intersection, k));

        // Condition d'arrêt de la récursion pour les effets globaux (Point ו)
        if (level == 1) {
            return color;
        }

        // Ajout des effets globaux récursifs (Point ו et ז)
        return color.add(calcGlobalEffects(intersection, ray, level, k));
    }

    /**
     * Calcule et combine la somme des effets globaux (réflexion + transparence) (Point ז).
     */
    private Color calcGlobalEffects(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = Color.BLACK;
        Material material = intersection.material;

        // Effet global 1 : Réflexion (Miroir) - Utilisation de ray.getDirection()
        Ray reflectedRay = constructReflectedRay(intersection.point, ray.getDirection(), intersection.normal);
        color = color.add(calcGlobalEffect(reflectedRay, level, k, material.kR));

        // Effet global 2 : Réfraction (Transparence) - Utilisation de ray.getDirection()
        Ray refractedRay = constructRefractedRay(intersection.point, ray.getDirection(), intersection.normal);
        color = color.add(calcGlobalEffect(refractedRay, level, k, material.kT));

        return color;
    }

    /**
     * Calcule l'effet global pour un unique rayon secondaire donné (réfléchi ou réfracté) (Point ו).
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        // Utilisation de product(kx) pour multiplier deux Double3 entre eux (k_nouveau = k * kx) (Point ו)
        Double3 kX = k.product(kx);

        // Condition d'arrêt (Point ו) : Arrêt si l'atténuation cumulée descend sous le seuil
        if (kX._d1() < MIN_CALC_COLOR_K && kX._d2() < MIN_CALC_COLOR_K && kX._d3() < MIN_CALC_COLOR_K) {
            return Color.BLACK;
        }

        // Recherche de l'intersection la plus proche pour ce rayon secondaire (Point ו)
        var closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null) {
            return _scene.background; // ÉNONCÉ POINT ו : Retourne directement la couleur de fond
        }

        // ÉNONCÉ POINT ו : La couleur retournée par la méthode récursive est multipliée par le coefficient kx
        return calcColor(closestIntersection, ray, level - 1, kX).scale(kx);
    }

    /**
     * Méthode de refactoring centralisée pour trouver l'intersection la plus proche (Point ח).
     */
    private Intersection findClosestIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /**
     * Accumule les effets diffus et spéculaires en appliquant la transparence (Ombre partielle) (Partie 3 - Point 4).
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = Color.BLACK;
        double vNormal = intersection.vNormal;
        Vector n = intersection.normal;

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                double lNormal = intersection.lNormal;

                // Condition géométrique : caméra et lumière du même côté de la surface
                if (lNormal * vNormal > 0) {
                    Vector l = lightSource.getL(intersection.point);

                    // Récupération de l'atténuation d'ombre partielle à la place de unshaded (Partie 3 - Point 4)
                    Double3 ktr = transparency(lightSource, l, n, intersection);

                    // Condition de contribution suffisante utilisant la nouvelle méthode de Double3 (Partie 3 - Point 6)
                    if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                        Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

                        color = color.add(
                                lightIntensity.scale(calcDiffuse(intersection)),
                                lightIntensity.scale(calcSpecular(intersection))
                        );
                    }
                }
            }
        }
        return color;
    }

    /**
     * Calcule le coefficient cumulé d'atténuation lumineuse causé par les obstacles semi-transparents (Partie 3 - Point 1 & 2).
     */
    private Double3 transparency(LightSource light, Vector l, Vector n, Intersection intersection) {
        Vector shadowDirection = l.scale(-1);

        // Crée le rayon d'ombre (le décalage DELTA est interne à la classe Ray)
        Ray shadowRay = new Ray(intersection.point, shadowDirection, n);
        var intersections = _scene.geometries.calcIntersections(shadowRay);

        if (intersections == null) {
            return Double3.ONE; // Aucun obstacle, lumière totale transparente
        }

        Double3 ktr = Double3.ONE;
        double lightDistance = light.getDistance(intersection.point);

        for (var i : intersections) {
            // Ne prend en compte que les intersections situées entre la surface et la source lumineuse
            if (intersection.point.distance(i.point) < lightDistance) {
                ktr = ktr.product(i.material.kT); // Accumulation par produit des coefficients de transparence

                // Si l'atténuation totale descend sous le seuil minimal, l'ombre devient opaque
                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }
        return ktr;
    }

    /**
     * Calcule la réflexion diffuse (Aspect mat - Loi de Lambert).
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double factor = Math.abs(intersection.lNormal);
        return intersection.material.kD.scale(factor);
    }

    /**
     * Calcule la réflexion spéculaire (Aspect brillant - Modèle de Phong).
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector l = intersection.l;
        Vector n = intersection.normal;
        Vector v = intersection.v;
        double lNormal = intersection.lNormal;

        Vector r = l.subtract(n.scale(2 * lNormal));
        double minusVR = -v.dotProduct(r);

        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        double factor = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }

    // --- Helpers pour la construction des rayons secondaires réfactorisés ---

    /**
     * Construit le rayon réfléchi (miroir) en utilisant le nouveau constructeur de Ray (Point 4).
     */
    private Ray constructReflectedRay(Point point, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        Vector r = v.subtract(n.scale(2 * vn));
        return new Ray(point, r, n);
    }

    /**
     * Construit le rayon réfracté (transparence) en utilisant le nouveau constructeur de Ray (Point 4).
     */
    private Ray constructRefractedRay(Point point, Vector v, Vector n) {
        return new Ray(point, v, n);
    }
}