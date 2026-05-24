package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

/**
 * Traceur de rayons simple utilisant le modèle de réflexion de Phong avec gestion des ombres.
 */
public class SimpleRayTracer extends RayTracerBase {

    // 1.א : Ajout de la constante DELTA pour éviter le self-shadowing
    private static final double DELTA = 0.1;

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
        Vector n = intersection.normal;

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                double lNormal = intersection.lNormal;

                // Condition géométrique : caméra et lumière du même côté de la surface
                if (lNormal * vNormal > 0) {

                    // 1.ג : On ajoute la vérification des ombres (unshaded)
                    Vector l = lightSource.getL(intersection.point);
                    if (unshaded(lightSource, l, n, intersection)) {
                        Color lightIntensity = lightSource.getIntensity(intersection.point);

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
     * 1.ב : Vérifie si le point d'intersection n'est pas masqué (ombragé) par rapport à la source lumineuse.
     */
    private boolean unshaded(LightSource light, Vector l, Vector n, Intersection intersection) {
        // 1.ד : La direction du rayon d'ombre est inversée par rapport à l'illumination (du point vers la lumière)
        Vector shadowDirection = l.scale(-1);

        // On décale le point d'origine le long de la normale pour éviter le self-shadowing (EPSILON / DELTA)
        double nv = n.dotProduct(shadowDirection);
        Vector deltaVector = n.scale(nv > 0 ? DELTA : -DELTA);
        Point shadowPoint = intersection.point.add(deltaVector);

        // Création du rayon d'ombre
        Ray shadowRay = new Ray(shadowPoint, shadowDirection);

        // Recherche des intersections entre le point et la source de lumière
        var intersections = _scene.geometries.calcIntersections(shadowRay);

        if (intersections == null) {
            return true; // Pas d'obstacles -> exposé à la lumière
        }

        // On vérifie si les intersections trouvées sont plus proches que la source de lumière elle-même
        double lightDistance = light.getDistance(intersection.point);
        for (var i : intersections) {
            // Comparaison basée sur le point géométrique d'origine pour éviter les biais du DELTA
            if (intersection.point.distance(i.point) < lightDistance) {
                return false; // Un objet bloque la lumière -> ombragé !
            }
        }

        return true;
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