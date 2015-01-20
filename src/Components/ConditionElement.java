package Components;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by serch on 19/01/15.
 */
public class ConditionElement implements Serializable{
    /** Variable que almacena el nombre de la variable. */
    private String variable; 	// Variable a verificar

    /** Variable que almacena tipo de caracter relacional. */
    private int carCond; 		// Caracter [0: =, 1: <, 2: >, 3: <=, 4: >=, 5: <>]

    /** Variable que almacena el valor. */
    private String valor; 	// Valor a verificar

    /** Variable que almacena el tipo de dato de la variable. */
    int tipoDato; 	// 0: CHAR(length),
    // 1: VARCHAR(length),
    // 2: INTEGER,
    // 3: FLOAT,
    // 4: NUMERIC(presicion,decimales),
    // 5: DATE
    // 7: TIME
    // 8: TIMESTAMP

    /** Variable que almacena el nombre de la funcion cuando sea el caso. */
    String funcion;

    /** Variable que almacena el parametro o parametros de la funcion. */
    String parametro;


    /** Construye un objeto de tipo Condition. */
    public ConditionElement(String variable, int carCond, String valor, String tipoDatoS) {
        this.variable = variable;
        this.carCond = carCond;
        this.valor = valor;
        this.tipoDato = convierte(tipoDatoS);
        System.out.println("Variable: " + variable + "; condicion: " + carCond + "; Valor: " + valor);
        this.funcion = "";
        this.parametro = "";
    }

    /** Construye un objeto de tipo Condition como una funcion. */
    public ConditionElement(String funcion, String parametro) {
        this.variable = "";
        this.carCond = -1;
        this.valor = "";
        this.tipoDato = 8;
        this.funcion = funcion;
        this.parametro = parametro;
        System.out.println("Variable: " + variable + "; condicion: " + carCond + "; Valor: " + valor);
    }


    /** Convierte la cadena de tipo de dato a su respectivo valor num�rico. */
    public int convierte(String c) {
        if(c.equalsIgnoreCase("CHAR(length)")) return 0;
        else if(c.equalsIgnoreCase("VARCHAR(length)")) return 1;
        else if(c.equalsIgnoreCase("INTEGER")) return 2;
        else if(c.equalsIgnoreCase("FLOAT")) return 3;
        else if(c.equalsIgnoreCase("NUMERIC(presicion,decimal)")) return 4;
        else if(c.equalsIgnoreCase("DATE")) return 5;
        else if(c.equalsIgnoreCase("TIME")) return 6;
        else if(c.equalsIgnoreCase("TIMESTAMP")) return 7;
        return -1;
    }


    /** Regresa la variable. */
    public String getVariable() {
        return variable;
    }

    /** Regresa la condici�n. */
    public int getCondicion() {
        return carCond;
    }

    /** Regresa el valor. */
    public String getValor() {
        return valor;
    }

    /** Regresa el tipo de dato. */
    public int getTipoDato() {
        return tipoDato;
    }

    /** Regresa la funcion a evaluar. */
    public String getFuncion() {
        return funcion;
    }

    /** Regresa el parametro de la funcion. */
    public String getParametro() {
        return parametro;
    }

    /** Clasifica la expresion de acuerdo a su tipo. */
    public boolean evalua(String valo) {
        String val = valo.trim();
        boolean resp = false;

        switch(tipoDato) {
            case 0 :
            case 1 : resp = evaluaValor(val); break;
            case 2 : resp = evaluaValor(new Integer(val)); break;
            case 3 :
            case 4 : resp = evaluaValor(new Float(val)); break;
            case 5 : resp = evaluaValor(Date.valueOf(val)); break; // Checar
            case 6 : resp = evaluaValor(Time.valueOf(val)); break; // Checar
            case 7 : resp = evaluaValor(Timestamp.valueOf(val)); break; // Checar
            case 8 : double i4 = Math.random();
                if(i4 < 0.5) {
                    System.out.println("Aleatoriamente NO Cumple la funcion ");
                    // resp = false;
                } else {
                    resp = true;
                    System.out.println("Aleatoriamente SI Cumple la funcion");
                }
                break;
        }
        return resp;
    }

    /** Evalua la cadena con el parametro recibido. */
    public boolean evaluaValor(String val) {
        boolean resp = false;
        System.out.println("En evalua valor val = " + val);
        System.out.println("En evalua valor valor = " + valor);

        switch(carCond) {
            case 0 : if(valor.compareTo(val) == 0)
                resp = true;
                break;
            case 1 : break;
            case 2 : break;
            case 3 : break;
            case 4 : break;
            case 5 : break;
        }
        return resp;
    }

    /** Evalua el Entero con el parametro recibido. */
    public boolean evaluaValor(Integer val) {
        boolean resp = false;
        switch(carCond) {
            case 0 : if(val.compareTo(new Integer(valor)) == 0 )
                resp = true;
                break;
            case 1 : if(val.compareTo(new Integer(valor)) < 0 )
                resp = true;
                break;
            case 2 : if(val.compareTo(new Integer(valor)) > 0 )
                resp = true;
                break;
            case 3 : if(val.compareTo(new Integer(valor)) <= 0 )
                resp = true;
                break;
            case 4 : if(val.compareTo(new Integer(valor)) >= 0 )
                resp = true;
                break;
            case 5 : if(val.compareTo(new Integer(valor)) != 0 )
                resp = true;
                break;
        }
        return resp;
    }

    /** Evalua el Flotante con el parametro recibido. */
    public boolean evaluaValor(Float val) {
        boolean resp = false;
        switch(carCond) {
            case 0 : if(val.compareTo(new Float(valor)) == 0 )
                resp = true;
                break;
            case 1 : if(val.compareTo(new Float(valor)) < 0 )
                resp = true;
                break;
            case 2 : if(val.compareTo(new Float(valor)) > 0 )
                resp = true;
                break;
            case 3 : if(val.compareTo(new Float(valor)) <= 0 )
                resp = true;
                break;
            case 4 : if(val.compareTo(new Float(valor)) >= 0 )
                resp = true;
                break;
            case 5 : if(val.compareTo(new Float(valor)) != 0 )
                resp = true;
                break;
        }
        return resp;
    }

    /** Evalua el Date con el parametro recibido. */
    public boolean evaluaValor(Date val) {
        boolean resp = false;
        switch(carCond) {
            case 0 : if(val.compareTo(Date.valueOf(valor)) == 0 )
                resp = true;
                break;
            case 1 : if(val.compareTo(Date.valueOf(valor)) < 0 )
                resp = true;
                break;
            case 2 : if(val.compareTo(Date.valueOf(valor)) > 0 )
                resp = true;
                break;
            case 3 : if(val.compareTo(Date.valueOf(valor)) <= 0 )
                resp = true;
                break;
            case 4 : if(val.compareTo(Date.valueOf(valor)) >= 0 )
                resp = true;
                break;
            case 5 : if(val.compareTo(Date.valueOf(valor)) != 0 )
                resp = true;
                break;
        }
        return resp;
    }

    /** Evalua el Time con el parametro recibido. */
    public boolean evaluaValor(Time val) {
        boolean resp = false;
        switch(carCond) {
            case 0 : if(val.compareTo(Time.valueOf(valor)) == 0 )
                resp = true;
                break;
            case 1 : if(val.compareTo(Time.valueOf(valor)) < 0 )
                resp = true;
                break;
            case 2 : if(val.compareTo(Time.valueOf(valor)) > 0 )
                resp = true;
                break;
            case 3 : if(val.compareTo(Time.valueOf(valor)) <= 0 )
                resp = true;
                break;
            case 4 : if(val.compareTo(Time.valueOf(valor)) >= 0 )
                resp = true;
                break;
            case 5 : if(val.compareTo(Time.valueOf(valor)) != 0 )
                resp = true;
                break;
        }
        return resp;
    }

    /** Evalua el Timestamp con el parametro recibido. */
    public boolean evaluaValor(Timestamp val) {
        boolean resp = false;
        switch(carCond) {
            case 0 : if(val.compareTo(Timestamp.valueOf(valor)) == 0 )
                resp = true;
                break;
            case 1 : if(val.compareTo(Timestamp.valueOf(valor)) < 0 )
                resp = true;
                break;
            case 2 : if(val.compareTo(Timestamp.valueOf(valor)) > 0 )
                resp = true;
                break;
            case 3 : if(val.compareTo(Timestamp.valueOf(valor)) <= 0 )
                resp = true;
                break;
            case 4 : if(val.compareTo(Timestamp.valueOf(valor)) >= 0 )
                resp = true;
                break;
            case 5 : if(val.compareTo(Timestamp.valueOf(valor)) != 0 )
                resp = true;
                break;
        }
        return resp;
    }
}
