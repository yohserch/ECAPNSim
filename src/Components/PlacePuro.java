package Components;

import java.awt.Color;
import java.util.Vector;

/**
 * Created by serch on 19/01/15.
 */
public class PlacePuro extends Circulo{
    static final int radio = 15;
    static int cont = 0;

    private String nombre;
    public Color color;
    private String command;
    private int index, indexEvent, numTokens;
    private boolean selected = false;
    private Vector vTokens;
    private int tipo;

    public PlacePuro(Punto p, int index) {
        super(p, radio);
        nombre = "P" + cont++;
        this.index = index;
        indexEvent = -1;
        numTokens = 0;
        color = Color.BLACK;
    }

    public PlacePuro(Punto p, int index, int indexEvent, String name, String command) {
        super(p, radio);
        nombre = name;
        this.command = command;
        this.index = index;
        this.indexEvent = indexEvent;
        numTokens = 0;
        vTokens = new Vector();
        color = Color.BLACK;
    }

    public int getIndex() {
        return this.index;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public int getNumTokens() {
        return numTokens;
    }

    public void restarToken() {
        numTokens -= 1;
    }

    public void setNumTokens(int num) {
        numTokens += num;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int t) {
        this.tipo = t;
    }

    public void setIndex(int i) {
        this.index = index;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getCommand() {
        return this.command;
    }

    public void asignaTipo(String nombreT) {
        if (nombre.startsWith("CopyOf_"))
            nombreT = nombre.substring(7, nombre.length());
        if (nombreT.substring(0,6).equalsIgnoreCase("INSERT"))
            tipo = 0;
        else if(nombreT.substring(0,6).equalsIgnoreCase("UPDATE"))
            tipo = 1;
        else if(nombreT.substring(0,6).equalsIgnoreCase("DELETE"))
            tipo = 2;
        else if(nombreT.substring(0,6).equalsIgnoreCase("SELECT"))
            tipo = 3;
    }

    public int getIndexEvent() {
        return indexEvent;
    }

    public String toString() {
        return nombre;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
