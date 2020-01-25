import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Light {
    
    public Vector3D P, E;
    
    public Light(Vector3D P, Vector3D E) {
        this.P = P;
        this.E = E;
    }
    
}
