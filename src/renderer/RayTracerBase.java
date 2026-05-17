package renderer;
import scene.Scene;
import  primitives.Color;
import  primitives.Ray;

/**
 * Base class for ray tracers.
 */
public abstract class RayTracerBase {
    /** Scene to trace */
    protected Scene _scene ;

    /**
     * Constructs a ray tracer for the given scene.
     * @param scene the scene to render
     */
    public RayTracerBase (Scene scene){ this._scene = scene ; }

    /**
     * Traces a ray and returns the computed color.
     * @param ray the ray to trace
     * @return computed color
     */
    public abstract Color traceRay (Ray ray);

}
