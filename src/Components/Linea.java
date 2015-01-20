package Components;

import java.io.Serializable;

/**
 * Created by serch on 12/01/15.
 */
public class Linea implements Serializable {
    // Variables that stores the final Points of line
    private Punto point1, point2;

    // Build a line with pints in (0,0) and (1,1)
    public Linea() {
        point1 = new Punto(0, 0);
        point2 = new Punto(1, 1);
    }

    // Build a line with two object Points
    public Linea(Punto pt1, Punto pt2) {
        this.point1 = pt1;
        this.point2 = pt2;
    }

    public Linea(int x1, int y1, int x2, int y2) {
        this.point1 = new Punto(x1, y1);
        this.point2 = new Punto(x2, y2);
    }

    public Punto getPoint1() {
        return this.point1;
    }

    public Punto getPoint2() {
        return this.point2;
    }

    public void setPoint1(Punto pt1) {
        this.point1 = pt1;
    }

    public void setPoint2(Punto pt2) {
        this.point2 = pt2;
    }

    public void mover(Punto ptoI, Punto ptoF) {
        this.point1 = ptoI;
        this.point2 = ptoF;
    }

    //Calculate the distance between two points
    public double distancia() {
        double distancia;
        distancia = Math.sqrt(((point2.valX() - point1.valX()) * (point2.valX() - point1.valX())) + ((point2.valY() - point1.valY()) * (point2.valY() - point1.valY())));
        return distancia;
    }

    // Find the slope having the line in the Cartesian plane
    public double pendiente() {
        double pendiente = (point2.valY() - point1.valY()) / (point2.valX() - point1.valX());
        return pendiente;
    }

    // Find the distance that exists betwteen the line and the point pt
    public double distPunto(Punto pt) {
        double A, B, C; // Quotients of the line in the form Ax + By + C
        double distancia; //
        A = this.point2.valY() - this.point1.valY();
        B = -1 * (this.point2.valX() - this.point1.valX());
        C = (this.point2.valX() - this.point1.valX()) * this.point1.valY() - (this.point2.valY() - this.point1.valY()) * this.point1.valX();
        distancia = Math.abs(A * pt.valX() + B * pt.valY() + C) / Math.sqrt(A * A + B * B);
        return distancia;
    }

    public boolean pertenece(Punto p) {
        double distancia = distPunto(p);
        System.out.println("Distancia del punto: " + distancia);
        if ((distancia <= 3.0 && distancia >= -3.0) && entreLinea(p)) return true;
        return false;
    }

    public boolean entreLinea(Punto p) {
        if (perteneceX(p) && perteneceY(p)) return true;
        return false;
    }

    public boolean perteneceX(Punto p) {
        if (this.point1.valX() < this.point2.valX()) {
            if (p.valX() + 3 >= this.point1.valX() && p.valX() - 3 <= this.point2.valX())
                return true;
        } else {
            if (p.valX() - 3 <= this.point1.valX() && p.valX() + 3 >= this.point2.valX())
                return true;
        }
        return false;
    }

    public boolean perteneceY(Punto p) {
        if (this.point1.valY() < this.point2.valY()) {
            if (p.valY() + 3 >= this.point1.valY() && p.valY() - 3 <= this.point2.valY())
                return true;
        } else {
            if (p.valY() - 3 <= this.point1.valY() && p.valY() + 3 >= this.point2.valY())
                return true;
        }
        return false;
    }

    public Punto puntoMedio() {
        int xm = point1.valX() + (int) ((point2.valX() - point1.valX()) / 2);
        int ym = point1.valY() + (int) ((point2.valY() - point1.valY()) / 2);
        return (new Punto(xm, ym));
    }
}
