import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Triangle {

	public Point3D  v0 = new Point3D(0.0, 0.0, 0.0),
					v1 = new Point3D(0.0, 0.0, 0.0),
					v2 = new Point3D(0.0, 0.0, 0.0);
	
	public Triangle(Point3D v0, Point3D v1, Point3D v2) {
		this.v0.x = v0.x; this.v0.y = v0.y; this.v0.z = v0.z;
		this.v1.x = v1.x; this.v1.y = v1.y; this.v1.z = v1.z;
		this.v2.x = v2.x; this.v2.y = v2.y; this.v2.z = v2.z;
	}
	
	
	
}
