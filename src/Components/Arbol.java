package Components;
/**
 * Created by serch on 12/01/15.
 */
import java.util.Vector;
/** Clase publica para declarar objetos de tipo Arbol
 * version 1.0
 * @autor jmedina
 * @see Punto
 */
public class Arbol {
    // Variables that store the coordinates where the tree is located
    private Punto ptoI, ptoF;
    // Variable for store the total of elements by level
    private Vector elemByLevel;

    // Specifies an area (0,0) and (1,1)
    public Arbol() {
        ptoI = new Punto(0,0);
        ptoF = new Punto(0,0);
        elemByLevel = new Vector();
    }

    public Arbol(Punto ptoI, Punto ptoF) {
        this.ptoI = ptoI;
        this.ptoF = ptoF;
        elemByLevel = new Vector();
    }

    // Specifies an area between the points (x1, y1) and (x2, y2)
    public Arbol(int x1, int y1, int x2, int y2) {
        ptoI = new Punto(x1, y1);
        ptoF = new Punto(x2, y2);
        elemByLevel = new Vector();
    }

    public Arbol(Punto ptoI, Punto ptoF, Vector V) {
        this.ptoI = ptoI;
        this.ptoF = ptoF;
        elemByLevel = V;
    }

    // Returns the initial point of area
    public Punto getPtoI() {
        return ptoI;
    }

    // Return the final point of area
    public Punto getPtoF() {
        return ptoF;
    }

    // Returns the vector of elements by level
    public Vector getVector() {
        return elemByLevel;
    }

    // Set the initial point
    public void setPtoI(Punto ptoI) {
        this.ptoI = ptoI;
    }

    // Set hte final point
    public void setPtoF(Punto ptoF) {
        this.ptoF = ptoF;
    }

    // Set the vector of elements by level
    public void setVector(Vector elemByLevel) {
        this.elemByLevel = elemByLevel;
    }

    // Returns the max value of vector
    public int maximo() {
        int max = 0;
        if (elemByLevel.size() > 0) {
            max = ((Integer) elemByLevel.elementAt(0)).intValue();
            for (int j = 0; j < elemByLevel.size(); j++) {
                if( ((Integer) elemByLevel.elementAt(j)).intValue() > max) {
                    max = ((Integer) elemByLevel.elementAt(j)).intValue();
                }
            }
        }
        return max;
    }

}
