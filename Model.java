import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.*;

public class Model {
    
    public double   wx = 0,
                    wy = 0.0,
                    wz = 0.0,
                    theta = 0.0,
                    scale = 0.0,
                    tx = 0.0,
                    ty = 0.0,
                    tz = 0.0;
    public Obj obj;
    
    public Model(double wx, double wy, double wz, double theta, double scale, double tx,
            double ty, double tz, String objFilename) {
        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
        this.theta = theta;
        this.scale = scale;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        obj = new Obj(objFilename);
        transform();
    }
    
    private void transform() {
        RealMatrix  R = getRotation(),
                    S = getScaling(),
                    T = getTranslation();
        RealMatrix transformMatrix = T.multiply(R.multiply(S));
        
        for (int i = 0; i < obj.vertices.size(); ++i) {
            Vector3D vertex = obj.vertices.get(i);
    
            ArrayRealVector vec = new ArrayRealVector(new double[] {vertex.getX(), vertex.getY(),
                    vertex.getZ(), 1.0});
            RealMatrix mat = new Array2DRowRealMatrix(vec.getDataRef());
            mat = transformMatrix.multiply(mat);
            vec = new ArrayRealVector(mat.getColumnVector(0));
            double[] dataRef = vec.getDataRef();
            vertex = new Vector3D(dataRef[0], dataRef[1], dataRef[2]);
            
            obj.vertices.set(i, vertex);
        }
    }
    
    public RealMatrix getRotation() {
        
        Vector3D Wv = new Vector3D(wx, wy, wz);
        Wv = Wv.normalize();
        
        double[] tempMv = { Math.abs(Wv.getX()), Math.abs(Wv.getY()),
                Math.abs(Wv.getZ()) };
        int minIndex = 0;
        for (int i = 0; i < 3; ++i) {
            if (tempMv[i] <= tempMv[minIndex]) {
                minIndex = i;
            }
        }
        tempMv[minIndex] = 1.0;
        Vector3D Mv = new Vector3D(tempMv);
        
        Vector3D Uv = Wv.crossProduct(Mv);
        Uv = Uv.normalize();
        Vector3D Vv = Wv.crossProduct(Uv);
        Vv = Vv.normalize();
        
        RealMatrix RM = new Array2DRowRealMatrix(new double[][] {
                { Uv.getX(), Uv.getY(), Uv.getZ(), 0.0 },
                { Vv.getX(), Vv.getY(), Vv.getZ(), 0.0 },
                { Wv.getX(), Wv.getY(), Wv.getZ(), 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
        RealMatrix RMt = RM.transpose();
        //RealMatrix RMRMt = RMt.multiply(RM);
        
        double rad = theta * (3.1415926535897932384626433833 / 180.0);
        double ca = Math.cos(rad);
        double sa = Math.sin(rad);
        RealMatrix RMz = new Array2DRowRealMatrix(new double[][] {
                { ca, -sa, 0.0, 0.0 },
                { sa, ca, 0.0, 0.0 },
                { 0.0, 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
        RealMatrix RT = RMt.multiply(RMz.multiply(RM));
        return RT;
    }
    
    public RealMatrix getScaling() {
        return new Array2DRowRealMatrix(new double[][] {
                { scale, 0.0, 0.0, 0.0 },
                { 0.0, scale, 0.0, 0.0 },
                { 0.0, 0.0, scale, 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
    }
    
    public RealMatrix getTranslation() {
        return new Array2DRowRealMatrix(new double[][] {
                { 1.0, 0.0, 0.0, tx },
                { 0.0, 1.0, 0.0, ty },
                { 0.0, 0.0, 1.0, tz },
                { 0.0, 0.0, 0.0, 1.0 }
        });
    }

}
