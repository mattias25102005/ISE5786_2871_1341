package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

/**
 * Traceur de rayons simple utilisant le modèle de réflexion de Phong.
 */
public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Point d'entrée : trouve l'intersection visible la plus proche et calcule sa couleur.
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
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return !preprocessIntersection(intersection, ray) ? Color.BLACK
                : _scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection));
    }

    /**
     * Accumule les effets diffus et spéculaires de toutes les sources lumineuses de la scène.
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
     * Calcule la réflexion diffuse (Aspect mat - Loi de Lambert) via les données en cache.
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