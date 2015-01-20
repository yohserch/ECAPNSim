package Components;

import java.awt.Color;
import java.io.Serializable;

/**
 * Created by serch on 12/01/15.
 */
public class ArcPuro extends Linea implements Serializable{
    // Variable to store the arc name
    private String nombre;
    public Color color;
    // Variable to store the arc index
    private int indice;

    // Variable to store the index of the place to which it is connected
    private int indPlace;

    // Variable to store the index of the transition to which it is connected
    private int indTransition;

    // Variable to identify the direction of the arc (Place to transition or transition to place)
    private boolean PtoT;

    // Variable for indicate the weight of the arc
    private int peso;

    // Variable that indicate it is selected
    private boolean selected = false;

    // Variable to determinate if is inhibitor arc
    private boolean inhibitorArc = false;

    // Variables to store the coordinates of the arrow in the destiny point of the arc
    private int[] xCoord = new int[3];
    private int[] yCoord = new int[3];

    // Variables to store the values of the angles (in radians) used to calculate the coordinates of the point forming bow arrow
    private double alpha1 = 160.0 * Math.PI / 180.0, alpha2 = 200.0 * Math.PI / 180.0,
                   beta;
    final double PI2 = Math.PI / 2.0;

    // Variables to store the value of the four angles (in radians) en que se divide la transicion
    public final double sigma1 = Math.atan((float) (Rectangulo.hs) / (float)(Rectangulo.ws)),
                        sigma2 = PI2 + Math.atan((float)(Rectangulo.ws) / (float)(Rectangulo.hs)),
                        sigma3 = Math.PI + Math.atan((float)(Rectangulo.hs) / (float)(Rectangulo.ws)),
                        sigma4 = Math.PI * 1.5 + Math.atan((float)(Rectangulo.ws) / (float)(Rectangulo.hs));
    // Variable to draw the point of the line
    private Linea ld;

    // Build ArcPuro with coordinates in (0,0) and (1,1)
    public ArcPuro() {
        super();
        nombre = "SIn nombre";
        indice = 0;
        valBeta();
        indPlace = 0;
        indTransition = 0;
        peso = 1;
        color = Color.black;
    }

    public ArcPuro(Punto pt1, Punto pt2, int indice, int indPlace, int indTrans) {
        super(pt1, pt2);
        nombre = "SIn nombre";
        this.indice = indice;
        valBeta();
        this.indPlace = indPlace;
        this.indTransition = indTrans;
        peso = 1;
        color = Color.black;
    }

    public void actNombre(String name) {
        this.nombre = name;
    }

    public String getNombre() {
        return this.nombre;
    }

    // Calculate the values for the points array xCoord[] used to draw an arrow in point destination
    public void /* int[] */ calcX() {
        int [] values = new int[3];
        values[0] = this.ld.getPoint2().valX();
        // beta = valBeta();
        if (getPoint1().valX() < getPoint2().valX()) {
            values[1] = values[0] + (int)(Math.cos(beta + alpha1) * 10.0);
            values[2] = values[0] + (int)(Math.cos(beta + alpha2) * 10.0);
        } else {
            values[1] = values[0] - (int)(Math.cos(beta + alpha1) * 10.0);
            values[2] = values[0] - (int)(Math.cos(beta + alpha2) * 10.0);
        }
        xCoord = values;
    }

    // Calculate values for the array points yCoord used to draw an arrow in point destination
    public void /*int[]*/ calcY() {
        int[] values = new int[3];
        values[0] = this.ld.getPoint2().valY();
        if (getPoint1().valX() <= getPoint2().valX()) {
            values[1] = values[0] + (int)(Math.sin(beta + alpha1) * 10.0);
            values[2] = values[0] + (int)(Math.sin(beta + alpha2) * 10.0);
        } else {
            values[1] = values[0] - (int)(Math.sin(beta + alpha1) * 10.0);
            values[2] = values[0] - (int)(Math.cos(beta + alpha2) * 10.0);
        }
        yCoord = values;
    }

    // Calculate the angle formed between the two points of the arc
    public void valBeta() {
        try {
            beta = Math.atan((double)(getPoint2().valY() - getPoint1().valY()) / (double)(getPoint2().valX() - getPoint1().valX()));
        } catch (ArithmeticException e) {
            beta = (getPoint1().valY() > getPoint2().valY()) ? PI2*(-1) : PI2;
            e.printStackTrace();
        }
    }

    public double getBeta() {
        return beta;
    }

    public int[] getPointsX() {
        return xCoord;
    }

    public int[] getPointsY() {
        return yCoord;
    }

    public int getIndice() {
        return this.indice;
    }

    public int getPlace() {
        return this.indPlace;
    }

    public void setPlace(int place) {
        this.indPlace = place;
    }

    public int getTransition() {
        return this.indTransition;
    }

    public void setTransition(int transition) {
        this.indTransition = transition;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public void calcDrawLine() {
        Punto pp, pt;
        int xp, yp, xt, yt;
        xp = getPoint2().valX();
        yp = getPoint2().valY();
        xt = getPoint1().valX();
        yt = getPoint1().valY();
        pp = new Punto(xp, yp);
        pt = new Punto(xt, yt);
        setLineDraw(pt, pp);
    }

    public void setLineDraw(Punto p1, Punto p2) {
        this.ld = new Linea(p1, p2);
    }

    public Linea getLineDraw() {
        return ld;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getPeso() {
        return peso;
    }

    public void actPuntos(Punto pto1, Punto pto2) {
        setPoint1(pto1);
        setPoint2(pto2);
        valBeta();
        calcDrawLine();
        calcX();
        calcY();
    }

    public void setInhibitorArc() {
        inhibitorArc = true;
    }

    public boolean getInhibitorArc() {
        return inhibitorArc;
    }

    public void setColor(Color c) {
        this.color = c;
    }
}
