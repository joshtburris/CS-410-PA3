import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Ray {
	
	public Vector3D L = new Vector3D(0, 0, 0),
					D = new Vector3D(0, 0, 0);
	
	public double bestT = Double.MAX_VALUE;
	public Vector3D bestPoint = null;
	public Intersection bestIntersection = null;
	
	public Ray(Vector3D L, Vector3D D) {
		this.L = L;
		this.D = D;
		this.D = this.D.normalize();
	}
	
	public boolean sphereTest(Sphere sphere) {
		Vector3D Tv = sphere.center.subtract(this.L);
		double v = Tv.dotProduct(this.D);
		double csq = Tv.dotProduct(Tv);
		double disc = Math.pow(sphere.radius, 2) - (csq - Math.pow(v, 2));
		if (disc > 0) {
			double tval = v - Math.sqrt(disc);
			if (tval < this.bestT && tval > 0.00001) {
				this.bestPoint = this.L.add(this.D.scalarMultiply(tval));
				this.bestT = tval;
				Vector3D N = this.bestPoint.subtract(sphere.center);
				this.bestIntersection = new Intersection(N, Raytracer.materials.get(sphere.materialIndex),
						this.bestPoint, this.bestT);
			}
			return true;
		}
		return false;
	}
	
	public boolean triangleTest(Model model, Face face) {
		double EPSILON = 0.0000001;
		
		Point3D outIntersectionPoint = new Point3D();
		
		Point3D vertex0 = new Point3D(model.obj.vertices.get(face.v1.v).getX(),
				model.obj.vertices.get(face.v1.v).getY(),
				model.obj.vertices.get(face.v1.v).getZ());
		Point3D vertex1 = new Point3D(model.obj.vertices.get(face.v2.v).getX(),
				model.obj.vertices.get(face.v2.v).getY(),
				model.obj.vertices.get(face.v2.v).getZ());
		Point3D vertex2 = new Point3D(model.obj.vertices.get(face.v3.v).getX(),
				model.obj.vertices.get(face.v3.v).getY(),
				model.obj.vertices.get(face.v3.v).getZ());
		
		Vector3D edge1 = new Vector3D(vertex1.x - vertex0.x,
				vertex1.y - vertex0.y,
				vertex1.z - vertex0.z);
		Vector3D edge2 = new Vector3D(vertex2.x - vertex0.x,
				vertex2.y - vertex0.y,
				vertex2.z - vertex0.z);
		Vector3D h = this.D.crossProduct(edge2);
		
		double a = edge1.dotProduct(h);
		
		if (a > -EPSILON && a < EPSILON) {
			return false;    // This ray is parallel to this triangle.
		}
		
		double f = 1.0 / a;
		Vector3D s = new Vector3D(this.L.getX() - vertex0.x,
				this.L.getY() - vertex0.y,
				this.L.getZ() - vertex0.z);
		double u = f * (s.dotProduct(h));
		
		if (u < 0.0 || u > 1.0) {
			return false;
		}
		
		Vector3D q = s.crossProduct(edge1);
		double v = f * this.D.dotProduct(q);
		
		if (v < 0.0 || u + v > 1.0) {
			return false;
		}
		
		// At this stage we can compute t to find out where the intersection point is on the line.
		double t = f * edge2.dotProduct(q);
		
		if (t < bestT && t > EPSILON && t < 1 / EPSILON) { // ray intersection
			outIntersectionPoint.set(0.0, 0.0, 0.0);
			outIntersectionPoint.scaleAdd(t, this.D, Raytracer.vecToPoint(this.L));
			bestPoint = Raytracer.pointToVec(outIntersectionPoint);
			
			Vector3D hitNorm = edge1.crossProduct(edge2);
			hitNorm = hitNorm.normalize();
			double hnsqr = hitNorm.dotProduct(hitNorm);
			double hitDot = hitNorm.dotProduct(this.D);
			double rsqr = this.D.dotProduct(this.D);
			double angle = Math.cos(hitDot / Math.sqrt(hnsqr * rsqr));
			if (angle > 0.0)
				hitNorm = hitNorm.scalarMultiply(-1);
			
			bestIntersection = new Intersection(hitNorm, face.material, bestPoint,
					t);
			return true;
		}
		// This means that there is a line intersection but not a ray intersection.
		return false;
	}
	
}
