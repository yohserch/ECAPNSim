package Components;

/**
 * Public class to declare objects of Point type
 * @version 1.0
 * @author jmedina
 */
import java.io.*;

public class Punto {
    // Store the coordinate to X axis
    private int x;
    // Store the coordinate to Y axis
    private int y;

    // Build an object Point with coordinates in (0.0, 0.0)
    public Punto() {
        x = 100;
        y = 100;
    }

    // Build an object with coordinates in (x, y)
    public Punto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Build an object Point with other Point
    public Punto(Punto pt) {
        this.x = pt.valX();
        this.y = pt.valY();
    }

    public int valX() {
        return this.x;
    }

    public int valY() {
        return this.y;
    }

    public void nvoX(int x) {
        this.x = x;
    }

    public void nvoY(int y) {
        this.y = y;
    }

    public boolean iguales(Punto p) {
        if(p.x == this.x && p.y == this.y) return true;
        else return false;
    }

    // Calculate the distance between two points
    public double distancia(Punto p) {
        double distancia = Math.sqrt(((p.valX() - this.valX()) * (p.valX() - this.valX())) + ((p.valY() - this.valY()) * (p.valY() - this.valY())));
        return distancia;
    }

    public String toString() {
        return "[" + this.valX() + "," + this.valY() + "]";
    }
}
