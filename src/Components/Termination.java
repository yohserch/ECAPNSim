package Components;

import java.util.Vector;

/**
 * Created by serch on 19/01/15.
 */
public class Termination {
    int m, n, cont = 0;
    int[][] a;
    Vector ruta, rutas;

    public Termination(int[][] b, int p, int q) {
        this.a = b;
        ruta = new Vector();
        rutas = new Vector();
    }

    public void siguienteNodo(int valor, int indice) {
        switch (valor) {
            case -1:
                boolean flag = false;
                for (int i = 0; i < n; i++) {
                    if (a[i][indice] < 0) {
                        flag = true;
                        int non;
                        if ((non = noExisteNodo(i, indice)) < 0) {
                            agregaNodo(new Coordenada(i ,indice));
                            siguienteNodo(1, i);
                            eliminaUltimoNodo();
                        } else {
                            agregaNodo(new Coordenada(i, indice));
                            System.out.println("Existe ruta ciclica");
                            Vector cp = new Vector(ruta);
                            for (int k = 0; k < non; k++) {
                                cp.removeElementAt(0);
                            }
                            imprimeRuta();
                            rutas.add(cp);
                            eliminaUltimoNodo();
                        }
                    }
                }
                if (!flag) {
                    imprimeRuta();
                }
                break;
            case 1:
                for (int j = 0; j < m; j++) {
                    if (a[indice][j] > 0) {
                        agregaNodo(new Coordenada(indice, j));
                        siguienteNodo(-1, j);
                        eliminaUltimoNodo();
                    }
                }
                break;
        }
    }

    public void agregaNodo(Coordenada nodo) {
        ruta.add(nodo);
    }

    public void eliminaUltimoNodo() {
        ruta.removeElementAt(ruta.size() - 1);
    }

    public Vector getPaths() {
        return rutas;
    }

    public void imprimeRuta() {
        int l = ruta.size();
        System.out.println("Ruta [" + cont + "]: ");
        for (int i = 0; i < l; i++) {
            System.out.println(((Coordenada) ruta.elementAt(i)).toString());
            cont++;
        }
    }

    public int noExisteNodo(int vi, int ind) {
        int l = ruta.size();
        for (int i = 0; i < l; i++) {
            if (((Coordenada) ruta.elementAt(i)).equals(new Coordenada(vi, ind)))
                return i;
        }
        return -1;
    }
}
