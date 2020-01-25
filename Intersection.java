import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Intersection {
    
    public Vector3D normal = null;
    public Material material = null;
    public Vector3D point = null;
    double distance = 0.0;
    
    public Intersection(Vector3D normal, Material material, Vector3D point, double distance) {
        this.normal = normal;
        this.material = material;
        this.point = point;
        this.distance = distance;
    }
    
}
