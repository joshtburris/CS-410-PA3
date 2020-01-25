import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Sphere {

    Vector3D center;
    double radius;
    int materialIndex;
    
    public Sphere(Vector3D center, double radius, int materialIndex) {
        this.center = center;
        this.radius = radius;
        this.materialIndex = materialIndex;
    }

}
