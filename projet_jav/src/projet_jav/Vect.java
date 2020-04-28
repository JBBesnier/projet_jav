package projet_jav;

import static java.lang.Math.*;

public class Vect {
    double x, y;
    
    public Vect(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double[] toArray() {
    	return new double[] {this.x,this.y};
    }
    
    public String toString() {
    	return x+","+y;
    }
    
    public void add(Vect v) {
        x += v.x;
        y += v.y;
    }
    
    public void sub(Vect v) {
        x -= v.x;
        y -= v.y;
    }
    
    public void div(double val) {
        x /= val;
        y /= val;
    }
    
    public void mult(double val) {
        x *= val;
        y *= val;
    }
    public double norme() {
        return sqrt(pow(x, 2) + pow(y, 2));
    }
    public double produit_sca(Vect v) {
        return x * v.x + y * v.y;
    }
    public void normalisation() {
        double norme = norme();
        if (norme != 0) {
            x /= norme;
            y /= norme;
        }
    }
    public void limit(double lim) {
        double norme = norme();
        if (norme != 0 && norme > lim) {
            x *= lim / norme;
            y *= lim / norme;
        }
    }
    public double heading() {
        return atan2(y, x);
    }
    public static Vect sub(Vect v, Vect v2) {
        return new Vect(v.x - v2.x, v.y - v2.y);
    }
    public static double dist(Vect v, Vect v2) {
        return sqrt(pow(v.x - v2.x, 2) + pow(v.y - v2.y, 2));
    }
    public static double angleBetween(Vect v, Vect v2) {
        return acos(v.produit_sca(v2) / (v.norme() * v2.norme()));
    }
}