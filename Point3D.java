import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Point3D {

    public double   x = 0,
                    y = 0,
                    z = 0;
    
    public Point3D() { }
    
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point3D(Point3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(Point3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    public void scaleAdd(double s, Vector3D v, Point3D p) {
        this.x = s * v.getX() + p.x;
        this.y = s * v.getY() + p.y;
        this.z = s * v.getZ() + p.z;
    }

}
