package Components;

import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by serch on 19/01/15.
 */
public class Condition implements Serializable{
    String description;
    private String eNomTabla; // Nombre de la tabla donde ocurre el evento
    private int valueSt=1; // "old"=0 or "new"=1
    private Vector vCond;


    /** Construye un objeto Condition. */
    public Condition(String description) throws ConditionWrongException {
        if(!description.trim().toLowerCase().startsWith("if "))
            throw new ConditionWrongException();

        this.description = description.trim().substring(3,description.length()).trim();

        if(!verify())
            throw new ConditionWrongException();
    }

    public boolean verify() {
        System.out.println("Etapa de verificacion de la condicion");
        if(!description.trim().equalsIgnoreCase("TRUE")) {
            this.vCond = new Vector();
            try {
                System.out.println("description..." + description);
                divideCondComp(description);
                System.out.println("description2..." + description);
            } catch(Exception exc) {
                System.out.println("ECA Error: Se cacho la excepcion de divideCond()");
                return false;
            }
        }
        System.out.println("Etapa de verificacion de la condicion.... regresa verdadero");

        return true;
    }

    /** Divide y obtiene los diferentes valores de la parte condicional. */
    public void divideCondComp(String c) throws Exception {
        String sst, sstv;
        StringTokenizer st = new StringTokenizer(c);
        while(st.hasMoreTokens()) {
            sst = st.nextToken("&").trim();
            System.out.println( sst + ">" + sst.length());
            try {
                divideCondPrim(sst);
            } catch(Exception exc) {
                System.out.println("Error: Se cacho la excepcion de divideCondComp(); sst = " + sst);
                throw new MyException("Error en la parte condicional");
            }
        }
    }

    /** Divide y obtiene los diferentes valores cada condicion. */
    public void divideCondPrim(String c) throws Exception {
        String variable; 	// Variable a verificar
        int carCond=-1; 		// Caracter [0: =, 1: <, 2: >, 3: <=, 4: >=, 5: <>]
        String valor; 	// Valor a verificar

        int pos, lc=0;
        if((pos = c.indexOf("<>")) != -1) {
            carCond = 5;
            lc=2;
        } else {
            if((pos = c.indexOf(">=")) != -1) {
                carCond = 4;
                lc=2;
            } else {
                if((pos = c.indexOf("<=")) != -1) {
                    carCond = 3;
                    lc=2;
                } else {
                    if((pos = c.indexOf(">")) != -1) {
                        carCond = 2;
                        lc=1;
                    } else {
                        if((pos = c.indexOf("<")) != -1) {
                            carCond = 1;
                            lc=1;
                        } else {
                            if((pos = c.indexOf("=")) != -1) {
                                carCond = 0;
                                lc=1;
                            } else {
                                lc = -1;
                            } //0
                        } //1
                    }  //2
                } //3
            }  //4
        } //5

        if(pos != -1) {
            System.out.println("AQui Pos");
            variable = c.substring(0,pos).trim();
            StringTokenizer st = new StringTokenizer(variable,".");
            switch(st.countTokens()) {
                case 2: eNomTabla = st.nextToken();
                    variable = st.nextToken();
                    break;
                case 3: String stat = st.nextToken();
                    if(stat.equalsIgnoreCase("old")) valueSt = 0;
                    else if(stat.equalsIgnoreCase("new")) valueSt = 1;
                    else throw new Exception("Error en la variable de la condicion.");
                    eNomTabla = st.nextToken();
                    variable = st.nextToken();
                    break;
                default: throw new Exception("Error en la variable de la condicion.");
            }

            valor = c.substring(pos+lc,c.length()).trim();
            System.out.println("AQui Pos2");
            int posTabla = Automata.posEnTabla(eNomTabla);
            System.out.println("AQui Pos3, posTabla = " + posTabla + ", variable = " + variable + ", carCond = " + carCond + ", valor = " + valor);
            try {
                vCond.add(new ConditionElement(variable, carCond, valor, ((Table) Automata.tables.elementAt(posTabla)).getCampo(variable)));
            } catch(Exception eC) {
                System.out.println("Se trata de una funcion...");
                int posPar = c.indexOf("(");
                String func = c.substring(0,posPar).trim();
                String param = c.substring(posPar, c.length()).trim();
                vCond.add(new ConditionElement(func, param));
                System.out.println("func: " + func + "; param: " + param + "; size vector = " + vCond.size());
            }
        } else throw new Exception("Error en la parte condicional.");
        System.out.println("Variable: " + variable + "; condicion: " + carCond + "; Valor: " + valor);
        System.out.println("divideCondPrim Automata.tables.size() = " + Automata.tables.size());
    }


    public Vector getElements() {
        return this.vCond;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return description;
    }
}
