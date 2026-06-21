package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Traceur de rayons simple utilisant le modèle de réflexion de Phong avec gestion des ombres,
 * et enrichi pour le calcul récursif des effets globaux (réflexion et transparence).
 */
public class SimpleRayTracer extends RayTracerBase {

    /** Profondeur maximale de récursion pour le calcul de la couleur */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /** Seuil minimal d'atténuation pour l'arrêt de la récursion */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /** Coefficient initial d'atténuation lumineuse */
    private static final Double3 INITIAL_K = Double3.ONE;

    /** Générateur de nombres aléatoires pour l'échantillonnage RANDOM et JITTERED */
    private static final Random RANDOM_GENERATOR = new Random();

    /**
     * Constructeur du traceur de rayons.
     * @param scene la scène à tracer
     */
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
     * @param intersection l'intersection à traiter
     * @param ray le rayon incident
     * @param level le niveau de profondeur de récursion
     * @param k le coefficient d'atténuation lumineuse cumulé
     * @return la couleur calculée pour cette intersection
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
     * Modifié pour le Mini-Projet : Intègre le multi-échantillonnage paramétrable pour le fini brillant (Glossy)
     * et le verre dépoli (Diffuse Glass).
     * @param intersection l'intersection à traiter
     * @param ray le rayon incident
     * @param level le niveau de profondeur de récursion
     * @param k le coefficient d'atténuation lumineuse cumulé
     * @return la couleur des effets globaux combinés
     */
    private Color calcGlobalEffects(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = Color.BLACK;
        Material material = intersection.material;

        // --- Effet global 1 : Réflexion (Miroir / Glossy) ---
        Ray idealReflectedRay = constructReflectedRay(intersection.point, ray.getDirection(), intersection.normal);
        List<Ray> reflectedBeam = generateBeam(idealReflectedRay, material.blurReflectionRadius, material.sampleCount, material.samplingType);

        Color reflectionColor = Color.BLACK;
        int validReflectedRays = 0;

        for (Ray beamRay : reflectedBeam) {
            // Contrainte (Slide 3) : s'assurer géométriquement que le rayon perturbé reste du bon côté de la surface
            if (intersection.normal.dotProduct(beamRay.getDirection()) * intersection.normal.dotProduct(idealReflectedRay.getDirection()) > 0) {
                reflectionColor = reflectionColor.add(calcGlobalEffect(beamRay, level, k, material.kR));
                validReflectedRays++;
            }
        }
        if (validReflectedRays > 0) {
            color = color.add(reflectionColor.reduce(validReflectedRays));
        }

        // --- Effet global 2 : Réfraction (Transparence / Verre dépoli) ---
        Ray idealRefractedRay = constructRefractedRay(intersection.point, ray.getDirection(), intersection.normal);
        List<Ray> refractedBeam = generateBeam(idealRefractedRay, material.blurRefractionRadius, material.sampleCount, material.samplingType);

        Color refractionColor = Color.BLACK;
        int validRefractedRays = 0;

        for (Ray beamRay : refractedBeam) {
            // Contrainte (Slide 3) : le rayon réfracté doit traverser et rester du côté opposé à la normale
            if (intersection.normal.dotProduct(beamRay.getDirection()) * intersection.normal.dotProduct(idealRefractedRay.getDirection()) > 0) {
                refractionColor = refractionColor.add(calcGlobalEffect(beamRay, level, k, material.kT));
                validRefractedRays++;
            }
        }
        if (validRefractedRays > 0) {
            color = color.add(refractionColor.reduce(validRefractedRays));
        }

        return color;
    }

    /**
     * Génère un faisceau de rayons échantillonnés (Beam) autour d'un rayon idéal.
     * Calcule dynamiquement GRID, RANDOM et JITTERED à la volée.
     */
    private List<Ray> generateBeam(Ray idealRay, double blurRadius, int sampleCount, SamplingType type) {
        List<Ray> beam = new ArrayList<>();

        // Si aucun flou ou 1 seul échantillon demandé, on conserve uniquement le rayon idéal
        if (blurRadius <= 0 || sampleCount <= 1) {
            beam.add(idealRay);
            return beam;
        }

        Vector v = idealRay.getDirection();
        Point p0 = idealRay.getOrigin();

        // 1. Centre du repère de projection local à une distance d_T = 1
        Point center = p0.add(v);

        // 2. Construction d'un repère local 2D (u, w) orthogonal à la direction v
        Vector arbitrary = new Vector(1, 0, 0);
        try {
            v.crossProduct(arbitrary);
        } catch (IllegalArgumentException e) {
            arbitrary = new Vector(0, 1, 0); // Cas où v est colinéaire à (1,0,0)
        }

        Vector u = v.crossProduct(arbitrary).normalize();
        Vector w = v.crossProduct(u).normalize();

        // --- CASE 1 : SAMPLING RANDOM PUR (Indépendant de la grille) ---
        if (type == SamplingType.RANDOM) {
            for (int k = 0; k < sampleCount; k++) {
                // Éparpille les rayons de façon totalement désordonnée sur tout le carré de flou
                double deltaX = (RANDOM_GENERATOR.nextDouble() - 0.5) * blurRadius;
                double deltaY = (RANDOM_GENERATOR.nextDouble() - 0.5) * blurRadius;

                Point targetPoint = center;
                if (!Util.isZero(deltaX)) targetPoint = targetPoint.add(u.scale(deltaX));
                if (!Util.isZero(deltaY)) targetPoint = targetPoint.add(w.scale(deltaY));

                Vector beamDir = targetPoint.subtract(p0).normalize();
                beam.add(new Ray(p0, beamDir));
            }
        }
        // --- CASE 2 : SAMPLING STRUCTURÉ (GRID OU JITTERED, dépendent de la grille) ---
        else {
            // Échantillonnage mathématique par sous-cases
            int n = (int) Math.sqrt(sampleCount);
            if (n * n != sampleCount) {
                n = (int) Math.round(Math.sqrt(sampleCount));
            }
            double step = 1.0 / n;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double xOffset = 0.5; // Comportement GRID par défaut (milieu de la sous-case)
                    double yOffset = 0.5;

                    if (type == SamplingType.JITTERED) {
                        // Aléatoire confiné de façon contrôlée dans sa propre case
                        xOffset = RANDOM_GENERATOR.nextDouble();
                        yOffset = RANDOM_GENERATOR.nextDouble();
                    }

                    // Calcul du décalage final par rapport au centre (dépend de la case i, j)
                    double deltaX = ((j + xOffset) * step - 0.5) * blurRadius;
                    double deltaY = ((i + yOffset) * step - 0.5) * blurRadius;

                    Point targetPoint = center;
                    if (!Util.isZero(deltaX)) targetPoint = targetPoint.add(u.scale(deltaX));
                    if (!Util.isZero(deltaY)) targetPoint = targetPoint.add(w.scale(deltaY));

                    Vector beamDir = targetPoint.subtract(p0).normalize();
                    beam.add(new Ray(p0, beamDir));
                }
            }
        }

        return beam;
    }

    /**
     * Calcule l'effet global pour un unique rayon secondaire donné (réfléchi ou réfracté) (Point ו).
     * @param ray le rayon secondaire (réfléchi ou réfracté)
     * @param level le niveau de profondeur de récursion
     * @param k le coefficient d'atténuation lumineuse cumulé
     * @param kx le coefficient d'atténuation spécifique (kR ou kT)
     * @return la couleur de l'effet global calculée
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
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
     * @param ray le rayon de recherche
     * @return l'intersection la plus proche, ou null s'il n'y a pas d'intersection
     */
    private Intersection findClosestIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /**
     * Accumule les effets diffus et spéculaires en appliquant la transparence (Ombre partielle) (Partie 3 - Point 4).
     * @param intersection l'intersection à traiter
     * @param k le coefficient d'atténuation lumineuse cumulé
     * @return la couleur résultant des effets locaux
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
     * @param light la source lumineuse
     * @param l le vecteur vers la lumière
     * @param n la normale à la surface
     * @param intersection l'intersection à traiter
     * @return le coefficient de transparence cumulé
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
     * @param intersection l'intersection à traiter
     * @return le coefficient de réflexion diffuse
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double factor = Math.abs(intersection.lNormal);
        return intersection.material.kD.scale(factor);
    }

    /**
     * Calcule la réflexion spéculaire (Aspect brillant - Modèle de Phong).
     * @param intersection l'intersection à traiter
     * @return le coefficient de réflexion spéculaire
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
     * @param point le point de réflexion
     * @param v le vecteur de direction incident
     * @param n la normale à la surface
     * @return le rayon réfléchi
     */
    private Ray constructReflectedRay(Point point, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        Vector r = v.subtract(n.scale(2 * vn));
        return new Ray(point, r, n);
    }

    /**
     * Construit le rayon réfracté (transparence) en utilisant le nouveau constructeur de Ray (Point 4).
     * @param point le point de réfraction
     * @param v le vecteur de direction incident
     * @param n la normale à la surface
     * @return le rayon réfracté
     */
    private Ray constructRefractedRay(Point point, Vector v, Vector n) {
        return new Ray(point, v, n);
    }
}