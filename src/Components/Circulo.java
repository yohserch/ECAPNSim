package Components;

import java.io.Serializable;

/**
 * Created by serch on 19/01/15.
 */
public class Circulo implements Serializable{
    // The avriable origen stores the coordinate (x,y)
    private Punto origen;
    // This variable stores the value of radio
    private int radio;
    private boolean visible;

    public Circulo(Punto p, int r) {
        origen = p;
        this.radio = r;
    }

    public Punto getOrigen() {
        return origen;
    }

    public int getRadio() {
        return radio;
    }

    public void mover(int x, int y) {
        origen = new Punto(x, y);
    }

    public boolean pertenece(Punto p) {
        if (origen.distancia(p) < (double) this.radio) {
            return true;
        }
        return false;
    }
}
