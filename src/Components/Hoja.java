package Components;

/**
 * Created by serch on 19/01/15.
 */
public class Hoja {
    // Variable that stores the index of hoja (P/T)
    private int indicePoT;
    private int tipo;
    private int nivel;
    private int numArbol;

    public Hoja() {
        indicePoT = 0;
        tipo = 0;
        nivel = 0;
        numArbol = 0;
    }

    public Hoja(int indicePoT, int tipo, int nivel, int numArbol) {
        this.indicePoT = indicePoT;
        this.tipo = tipo;
        this.nivel = nivel;
        this.numArbol = numArbol;
    }

    public int getIndicePoT() {
        return indicePoT;
    }

    public void setIndicePoT(int indicePoT) {
        this.indicePoT = indicePoT;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getNumArbol() {
        return numArbol;
    }

    public void setNumArbol(int numArbol) {
        this.numArbol = numArbol;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
}
