package Components;

/**
 * Created by serch on 19/01/15.
 */
public class SelectRec {
    private int px, py, px1, py1;

    public SelectRec() {
        px = 0;
        py = 0;
        px1 = 0;
        py1 = 0;
    }

    public void agregar(int x, int y) {
        px = x;
        py = y;
    }

    public void agregarTamanio(int x, int y) {
        px1 = x;
        py1 = y;
    }

    public int getX() {
        return px;
    }

    public int getY() {
        return py;
    }

    public int getY1() {
        return py1;
    }

    public int getX1() {
        return px1;
    }

    public int getWidth() {
        return px1 - px;
    }

    public int getHeight() {
        return py1 - py;
    }
}
