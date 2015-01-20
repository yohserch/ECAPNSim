package Components;

import java.awt.geom.*;
import java.io.*;
import java.util.Vector;

/** Clase p�blica para declarar objetos de tipo TransitionPura
 * @version 1.0
 * @author jmedina
 * @see Rectangle
 */
public class TransitionPura extends Rectangulo implements Serializable {

    static int cont=0;
    public java.awt.Color color;
    /** Variable para almacenar el nombre de la Transicion. */
    private String nombre;
    private String condition;
    private String variable;
    private int cond;
    private String valor;
    private Vector vCond;

    /** Variable para almacenar el indice de la Transicion. */
    private int indice;

    /** Variable que indica si es un objeto seleccionado. */
    private boolean sel = false;

    //Numero de tokens que tiene la transicion
    private int token;

    //Numero de Place conectados
    private int plac_conec;
    //TIpo de transicion
    private int tipo; //0: transicion pura, 1: transicion compuesta, 2: transicion copia
    /** Construye TransitionPura con coordenadas en el Punto p, con base = 10 y altura = 30. */
    private Punto pf;

    /** Variable que cuenta los tokens que no cumplen con la condicion. */
    int badTokens;

    /** Variable para identificar el tipo de transicion: 0 = Trule, 1 = Tcopy, 2 = Tce. */
    int type;

    /** Variable para identificar el tipo de evento que se representa: 0:insert, ... */
    int typeEvent;

    private ActionRule arule;

    public TransitionPura(Punto p, int indice) {
        super(p);
        pf =p;
        nombre = "T" + cont++;
        this.indice = indice;
        this.condition = "";
        vCond = new Vector();
        tipo=0;
        token =0;
        color=java.awt.Color.RED;
    }

    public TransitionPura(Punto p, int indice, int typeT, int typeE) {
        super(p);
        nombre = "T" + cont++;
        this.indice = indice;
        this.type = typeT;
        this.typeEvent = typeE;
        this.condition = "";
        //tokenT = new Token();
        vCond = new Vector();
        badTokens = 0;
        color=java.awt.Color.RED;
    }

    public TransitionPura(Punto p, int indice, int typeT, Condition condition, ActionRule asql) {
        System.out.println("Dentro de constructor de TransitionPura");
        nombre = "T" + cont++;
        this.indice = indice;
        this.type = typeT;
        this.typeEvent = -1;
        this.condition = condition.getDescription();
        this.arule = asql;
        //tokenT = new Token();
        vCond = condition.getElements();
        badTokens = 0;
        color=java.awt.Color.RED;
    }

    public TransitionPura(Punto p, int indice, int typeT) {
        super(p);
        nombre = "T" + cont++;
        this.indice = indice;
        this.type = typeT;
        this.typeEvent = -1;
        this.condition = "";
        //tokenT = new Token();
        vCond = new Vector();
        badTokens = 0;
        color=java.awt.Color.RED;
    }

    /** Obtiene el nombre de la Transition */
    public String getNombre() {
        return nombre;
    }
    /** Obtiene el valor del indice. */
    public int getIndice() {
        return this.indice;
    }
    /** Establece que el estado de seleccion del Place. */
    public void setSeleccion(boolean b) {
        sel = b;
    }
    public boolean getSeleccion() {
        return sel;
    }
    public void settoken(int tk) {
        token +=tk;
    }
    public int gettoken() {
        return token;
    }
    public int getTipo() {
        return tipo;
    }
    public void setTipo(int tip) {
        tipo=tip;
    }
    public void setPlaceConec(int nu_p) {
        plac_conec += nu_p;

    }
    public int getPlaceConec() {
        return plac_conec;
    }
    public Punto getPunto() {
        return pf;
    }

    public int getTokens() {
        return token;
    }
    public void setIndice(int i) {
        this.indice = i;
    }

    void restarToken() {
        --token ;
    }
    public void setColor(java.awt.Color c) {
        color=c;
    }

    /** Devuelve el tipo de Transici�n. */
    public int getType() {
        return this.type;
    }
}