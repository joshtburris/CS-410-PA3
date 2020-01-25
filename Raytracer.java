import java.io.*;
import java.util.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Raytracer {
    
    public static Camera camera = new Camera();
    public static ArrayList<Material> materials = new ArrayList<>();
    public static ArrayList<Sphere> spheres = new ArrayList<>();
    public static ArrayList<Light> lights = new ArrayList<>();
    public static ArrayList<Model> models = new ArrayList<>();
    public static Vector3D ambient;
    public static int recursionLevel;

    public static void main(String[] args) {
        
        readDriverData(args[0]);
    
        int[][] img = new int[camera.width][camera.height * 3];
        
        for (int i = 0; i < camera.width; ++i) {
            for (int j = 0; j < camera.height; ++j) {
                
                Ray ray = pixelRay(i, j, camera);
                
                Vector3D rgb = new Vector3D(0.0, 0.0, 0.0);
                
                rgb = rayTrace(ray, rgb, new Vector3D(1.0, 1.0, 1.0), recursionLevel);
                img[i][j*3] = (int)(rgb.getX() * 255.0);
                img[i][(j*3)+1] = (int)(rgb.getY() * 255.0);
                img[i][(j*3)+2] = (int)(rgb.getZ() * 255.0);
            }
        }
    
        writeImg(img, args[1]);
    }

    private static void readDriverData(String driverFilename) {
    
        try {
        
            File driverFile = new File(driverFilename);
            Scanner scan = new Scanner(driverFile);
            
            while (scan.hasNextLine()) {
                scanNextLine(scan.nextLine().trim());
            }
            
            camera.setupUVW();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    private static void scanNextLine(String line) {
        
        if (line.isEmpty() || line.charAt(0) == '#')
            return;
        
        String[] data = line.split(" ");
        switch (data[0].toLowerCase()) {
            case "eye":
                Vector3D eye = new Vector3D(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                camera.setEye(eye);
                break;
            case "look":
                Vector3D look = new Vector3D(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                camera.setLook(look);
                break;
            case "up":
                Vector3D up = new Vector3D(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                camera.setUp(up);
                break;
            case "d":
                camera.setNear(Integer.parseInt(data[1]));
                break;
            case "recursionlevel":
                recursionLevel = Integer.parseInt(data[1]);
                break;
            case "bounds":
                Bounds bounds = new Bounds(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]));
                camera.setBounds(bounds);
                break;
            case "res":
                camera.setWidth(Integer.parseInt(data[1]));
                camera.setHeight(Integer.parseInt(data[2]));
                break;
            case "ambient":
                ambient = new Vector3D(Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]),
                        Double.parseDouble(data[3]));
                break;
            case "light":
                Light l = new Light(new Vector3D(Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]), Double.parseDouble(data[3])),
                        new Vector3D(Double.parseDouble(data[5]),
                        Double.parseDouble(data[6]), Double.parseDouble(data[7])));
                lights.add(l);
                break;
            case "sphere":
                Vector3D ka = new Vector3D(Double.parseDouble(data[5]),
                        Double.parseDouble(data[6]), Double.parseDouble(data[7]));
                Vector3D kd = new Vector3D(Double.parseDouble(data[8]),
                        Double.parseDouble(data[9]), Double.parseDouble(data[10]));
                Vector3D ks = new Vector3D(Double.parseDouble(data[11]),
                        Double.parseDouble(data[12]), Double.parseDouble(data[13]));
                Vector3D kr = new Vector3D(Double.parseDouble(data[14]),
                        Double.parseDouble(data[15]), Double.parseDouble(data[16]));
                Material m = new Material(ka, kd, ks, kr, 16.0);
                materials.add(m);
                Sphere s = new Sphere(new Vector3D(Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]), Double.parseDouble(data[3])),
                        Double.parseDouble(data[4]), materials.size()-1);
                spheres.add(s);

                break;
            case "model":
                Model mod = new Model(Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]),
                        Double.parseDouble(data[3]),
                        Double.parseDouble(data[4]),
                        Double.parseDouble(data[5]),
                        Double.parseDouble(data[6]),
                        Double.parseDouble(data[7]),
                        Double.parseDouble(data[8]),
                        data[9]);
                
                models.add(mod);
                break;
        }
    }
    
    private static Ray pixelRay(int i, int j, Camera camera) {
        
        double px = ((double)i / ((double)camera.width - 1.0)) *
                (camera.bounds.right - camera.bounds.left) + camera.bounds.left;
        double py = ((double)j / ((double)camera.height - 1.0)) *
                (camera.bounds.bottom - camera.bounds.top) + camera.bounds.top;
        
        Vector3D L = camera.eye.add( camera.W.scalarMultiply(camera.near) )
                .add( camera.U.scalarMultiply(px) )
                .add( camera.V.scalarMultiply(py) );
        Vector3D D = L.subtract(camera.eye);
        return new Ray(L, D);
    }
    
    private static Vector3D rayTrace(Ray ray, Vector3D rgb, Vector3D refatt, int level) {
        
        Intersection inter = rayFind(ray);
        
        if (rayFind(ray) != null) {
        
            Vector3D N = inter.normal;
            N = N.normalize();
            Material material = inter.material;
            
            Vector3D color = pairwiseProduct(material.ka, ambient);
            
            for (Light light : lights) {
                Vector3D toL = light.P.subtract(inter.point);
                toL = toL.normalize();
                double NdotL = N.dotProduct(toL);
                
                if (rayFind(new Ray(inter.point, toL)) != null) {
                    continue;
                }
                
                if (NdotL > 0.0) {
                    color = color.add( pairwiseProduct(material.kd, light.E).scalarMultiply(NdotL) );
                    
                    Vector3D toC = ray.L.subtract(inter.point);
                    toC = toC.normalize();
                    
                    Vector3D spR = N.scalarMultiply(2 * NdotL).subtract(toL);
                    spR = spR.normalize();
                    
                    double Cdr = toC.dotProduct(spR);
                    if (Cdr > 0.0) {
                        color = color.add( pairwiseProduct(material.ks, light.E).scalarMultiply(
                                Math.pow(Cdr, material.alpha)) );
                    }
                }
            }
            
            rgb = rgb.add(pairwiseProduct(refatt, color));
            
            if (level > 0) {
                Vector3D Uinv = ray.D.scalarMultiply(-1.0);
                
                Vector3D refR = N.scalarMultiply(2 * N.dotProduct(Uinv)).subtract(Uinv);
                refR = refR.normalize();
                
                rgb = rayTrace(new Ray(inter.point, refR), rgb,
                        pairwiseProduct(material.kr, refatt), level - 1);
            }
            
        }
        return rgb;
    }
    
    private static Intersection rayFind(Ray ray) {
        for (Sphere s : spheres) {
            ray.sphereTest(s);
        }
        for (Model model : models) {
            for (Face face : model.obj.faces) {
                ray.triangleTest(model, face);
            }
        }
        return ray.bestIntersection;
    }
    
    private static void writeImg(int[][] img, String filepath) {
        try {
            
            FileWriter writer = new FileWriter(filepath, false);
            writer.write("P3\n");
            writer.write((img[0].length / 3) +" "+ img.length +" 255\n");
            for (int w = 0; w < img[0].length; w += 3) {
                for (int l = 0; l < img.length; ++l) {
                    writer.write(img[l][w] +" "+ img[l][w + 1] +" "+
                            img[l][w + 2] +" ");
                }
            }
            writer.close();
            
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private static Vector3D pairwiseProduct(Vector3D u, Vector3D v) {
        return new Vector3D(u.getX() * v.getX(), u.getY() * v.getY(),
                u.getZ() * v.getZ());
    }
    
    private static double[] pairwiseProduct(double[] u, double[] v) {
        return new double[] { u[0] * v[0], u[1] * v[1], u[2] * v[2] };
    }
    
    public static Point3D vecToPoint(Vector3D v) {
        return new Point3D(v.getX(), v.getY(), v.getZ());
    }
    
    public static Vector3D pointToVec(Point3D p) {
        return new Vector3D(p.x, p.y, p.z);
    }
    
}
