import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Face {
    
    public Vertex v1, v2, v3;
    public Material material;
    
    public Face(Vertex v1, Vertex v2, Vertex v3, Material material) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.material = material;
    }
    
}
