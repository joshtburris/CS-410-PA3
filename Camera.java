import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Camera {
	
	public Vector3D eye = new Vector3D(0.0, 0.0, 0.0),
					look = new Vector3D(0.0, 0.0, 0.0),
					up = new Vector3D(0.0, 0.0, 0.0),
					U = new Vector3D(0.0, 0.0, 0.0),
					V = new Vector3D(0.0, 0.0, 0.0),
					W = new Vector3D(0.0, 0.0, 0.0);
	
	public Bounds bounds = new Bounds(0, 0, 0, 0);
	
	public int  near = 0,
				far = 0,
				width = 0,
				height = 0;
	
	public Camera() { }
	
	public Camera(Vector3D eye, Vector3D look, Vector3D up, Bounds bounds, int near,
			  int far, int width, int height) {
		this.eye = eye;
		this.look = look;
		this.up = up;
		this.bounds = bounds;
		this.near = near;
		this.far = far;
		this.width = width;
		this.height = height;
		setupUVW();
	}
	
	public void setEye(Vector3D eye) { this.eye = eye; }
	public void setLook(Vector3D look) { this.look = look; }
	public void setUp(Vector3D up) { this.up = up; }
	public void setBounds(Bounds bounds) { this.bounds = bounds; }
	public void setNear(int near) { this.near = near; }
	public void setFar(int far) { this.far = far; }
	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) { this.height = height; }
	
	public void setupUVW() {
		this.W = eye.subtract(look);
		this.W = this.W.normalize();
		
		this.U = up.crossProduct(this.W);
		this.U = this.U.normalize();
		
		this.V = this.W.crossProduct(this.U);
	}
	
}
