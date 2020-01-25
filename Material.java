import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Material {
    
    public Vector3D ka, kd, ks, kr;
    public double alpha = 0.0;
    public int illum = 0;
    public String name = "";
    
    public Material() { }
    
    public Material(Vector3D ka, Vector3D kd, Vector3D ks, Vector3D kr, double alpha) {
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.kr = kr;
        this.alpha = alpha;
    }
    
    public Material(Vector3D ka, Vector3D kd, Vector3D ks, Vector3D kr, double alpha,
            String name) {
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.kr = kr;
        this.alpha = alpha;
        this.name = name;
    }
    
    public Material(String name) {
        this.name = name;
    }

}
