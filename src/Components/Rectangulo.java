package Components;

import java.io.Serializable;

/**
 * Created by serch on 14/01/15.
 */
public class Rectangulo implements Serializable{
    // static final int hs = 30;
    // static final int ws = 30;
    static final int hs = 10;
    static final int ws = 30;
    // Variable that stores the location of the rectangle origin vertex
    private Punto point1;
    // Variable that stores the rectangle height
    private int height;
    // Variable that stores the rectangle width
    private int width;
    // Variable that determine if the figure its visible (true)
    private boolean visible;
    // Variable to store the coordinate of the center location
    private Punto centro;

    // Genera an rectangle with width and height 1 in (0,0)
    public Rectangulo() {
        point1 = new Punto(0,0);
        height = hs;
        width = ws;
        centro = new Punto(point1.valX() + (int)(getW() / 2), point1.valY() + (int)(getH() / 2));
    }

    public Rectangulo(Punto pt1) {
        centro = pt1;
        height = hs;
        width = ws;
        this.point1 = new Punto(pt1.valX() - (int)(getW() / 2), point1.valY() - (int)(getH() / 2));
    }

    public Rectangulo(Punto pt1, int base, int altura) {
        centro = pt1;
        height = altura;
        width = base;
        this.point1 = new Punto(point1.valX() - (int)(getW() / 2), point1.valY() - (int)(getH() / 2));
    }

    public Punto getOrigen() {
        return point1;
    }

    public Punto getCentro() {
        return centro;
    }

    public int getH() {
        return height;
    }

    public int getW() {
        return width;
    }

    public void setH(int h) {
        this.height = h;
    }

    public void mostrar() {
        this.visible = true;
    }

    public void ocultar() {
        this.visible = true;
    }

    public double perimetro() {
        double perimetro;
        perimetro = 2 * (height + width);
        return perimetro;
    }

    public double area() {
        double area;
        area = width * height;
        return area;
    }

    public boolean pertenece(Punto p) {
        boolean rx = false, ry = false;
        if (p.valX() >= point1.valX() && p.valX() <= (point1.valX() + getW()))
            rx = true;
        if (p.valY() >= point1.valY() && p.valY() <= (point1.valY() + getH()))
            ry = true;
        return (rx && ry);
    }

    public void mover(int x1, int y1) {
        centro = new Punto(x1, y1);
        this.point1 = new Punto(x1 - (int)(getW() / 2), y1 - (int)(getH() / 2));
    }
}
