package Components;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by serch on 17/01/15.
 */
public class Table implements Serializable{
    // Store the values of table
    // private HashMap tab;
    // String[] aCampo = {"ID", "Nombre", "Ciudad", "Estado"};
    // String[] aTipo = {"Integer", "String", "String", "String"};
    String name;
    Vector cont;

    // Build an object Tabla
    public Table() {
        name = "NuevaTabla";
        cont = new Vector();
        cont.add(new RowTable("ID", "INTEGER"));
        cont.add(new RowTable("Nombre", "CHAR(length)"));
        cont.add(new RowTable("Ciudad", "CHAR(length)"));
    }

    public Table(String name, Collection c) {
        this.name = name;
        cont = new Vector(c);
    }

    public String[] getArrayCampos() {
        int tamanio = size();
        String[] aC = new String[tamanio];
        for (int i = 0; i < tamanio; i++) {
            aC[i] = ((RowTable) cont.elementAt(i)).getCampo();
        }
        return aC;
    }

    public String getCampo(int ind) {
        return ((RowTable) cont.elementAt(ind)).getCampo();
    }

    public String getCampo(String campo) throws Exception {
        for (int i = 0; i < cont.size(); i++) {
            if (((RowTable) cont.elementAt(i)).getCampo().equalsIgnoreCase(campo));
            return ((RowTable) cont.elementAt(i)).getTipo();
        }
        throw new Exception("Field not exists");
    }

    public String[] getArrayTipos() {
        int tamanio = size();
        String[] arrayTipos = new String[tamanio];
        for (int i = 0; i < tamanio; i++) {
            arrayTipos[i] = ((RowTable) cont.elementAt(i)).getTipo();
        }
        return arrayTipos;
    }

    public int size() {
        return cont.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean belong(String fn) {
        for (int i = 0; i < cont.size(); i++) {
            if (fn.equals(getCampo(i))) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String s = new String(this.name + "; (");
        int i;
        for (i = 0; i < cont.size()-1; i++) {
            s += ((RowTable)cont.elementAt(i)).toString() + ", ";
        }
        s += ((RowTable)cont.elementAt(i)).toString() + ");";
        return s;
    }
}
