package Components;

import java.io.Serializable;

/**
 * Created by serch on 17/01/15.
 */
public class RowTable implements Serializable {
    private String campo;
    private String tipo;

    public RowTable(String campo, String tipo) {
        this.campo = campo;
        this.tipo = tipo;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return (this.campo + " " + this.tipo);
    }
}
