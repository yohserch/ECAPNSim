package Components;

/**
 * Created by serch on 19/01/15.
 */
public class ControlAnimacion {
    // Variable that stores the coordinate of point to graph
    private Punto pto;
    // Varible that store the factor progress in the x-axis
    private double progressX;
    private double progressY;
    private double x;
    private double y;

    // Variable that store the index of place and transition
    private int place;
    private int transition;

    private boolean uso;

    public static final double l = 30.0;

    public ControlAnimacion() {
        pto = new Punto(0, 0);
        progressX = 0.0;
        progressY = 0.0;
        x = 0.0;
        y = 0.0;
        place = 0;
        transition = 0;
    }

    public ControlAnimacion(Punto pto, double x, double y, int place, int transition) {
        this.pto = pto;
        this.progressX = x / l;
        this.progressY = y / l;
        x = pto.valX();
        y = pto.valY();
        place = place;
        transition = transition;
        uso = true;
    }


    public Punto getPto() {
        return pto;
    }

    public void setPto(Punto pto) {
        this.pto = pto;
    }

    public double getProgressX() {
        return progressX;
    }

    public void setProgressX(double progressX) {
        this.progressX = progressX;
    }

    public double getProgressY() {
        return progressY;
    }

    public void setProgressY(double progressY) {
        this.progressY = progressY;
    }

    public int getPlace() {
        return place;
    }

    public int getTransition() {
        return transition;
    }

    public boolean getUso() {
        return uso;
    }

    public void setUso(boolean uso) {
        this.uso = uso;
    }

    public void incrementa() {
        x += progressX;
        y += progressY;
        pto = new Punto((int)x, (int)y);
    }
}
