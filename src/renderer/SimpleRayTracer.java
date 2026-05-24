package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

/**
 * Traceur de rayons simple utilisant le modèle de réflexion de Phong.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructeur du traceur simple de rayons.
     * @param scene la scène à rendre
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Point d'entrée : trouve l'intersection visible la plus proche et calcule sa couleur.
     * @param ray le rayon à tracer
     * @return la couleur calculée pour le rayon
     */
    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) {
            return _scene.background;
        }

        var closestIntersection = ray.findClosestIntersection(intersections);
        return calcColor(closestIntersection, ray);
    }

    /**
     * Calcule la couleur globale (Ambiante + Émission + Effets locaux) après validation du cache.
     * @param intersection intersection la plus proche
     * @param ray le rayon incident
     * @return couleur résultante
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return !preprocessIntersection(intersection, ray) ? Color.BLACK
                : _scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection));
    }

    /**
     * Accumule les effets diffus et spéculaires de toutes les sources lumineuses de la scène.
     * @param intersection intersection pour laquelle on calcule les effets locaux
     * @return couleur résultante des effets locaux
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;
        double vNormal = intersection.vNormal;

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                double lNormal = intersection.lNormal;

                // Condition géométrique : caméra et lumière du même côté de la surface
                if (lNormal * vNormal > 0) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point);

                    color = color.add(
                            lightIntensity.scale(calcDiffuse(intersection)),
                            lightIntensity.scale(calcSpecular(intersection))
                    );
                }
            }
        }
        return color;
    }

    /**
     * Calcule la réflexion diffuse (Loi de Lambert) en utilisant les données mises en cache.
     * @param intersection intersection contenant la normale et la donnée lNormal
     * @return composante diffuse (Double3)
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double factor = Math.abs(intersection.lNormal);
        return intersection.material.kD.scale(factor);
    }

    /**
     * Calcule la réflexion spéculaire selon le modèle de Phong en utilisant les données en cache.
     * @param intersection intersection contenant v, n, l, lNormal et matériau
     * @return composante spéculaire (Double3)
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector l = intersection.l;
        Vector n = intersection.normal;
        Vector v = intersection.v;
        double lNormal = intersection.lNormal;

        // Vecteur de réflexion parfaite : r = l - 2 * (l . n) * n
        Vector r = l.subtract(n.scale(2 * lNormal));

        // Calcul de l'intensité du reflet selon l'angle de vue (-v . r)
        double minusVR = -v.dotProduct(r);

        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        // Concentration du reflet avec l'exposant de brillance (nShininess)
        double factor = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }
}