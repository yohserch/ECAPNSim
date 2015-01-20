package Components;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Created by serch on 19/01/15.
 */
public class Rule implements Serializable{
    int index;
    int indexEvent;
    Condition condition;
    ActionRule action;

    public Rule(String description) throws RuleWrongException {
        this.index = -1;
        indexEvent = -1;

        if(!verify(description))
            throw new RuleWrongException();

        System.out.println("Is going to add to the rule....\n" + this.toString());
        Automata.rules.add(this);
    }

    public boolean verify(String rul) {

        StringTokenizer st = new StringTokenizer(rul,",");
        String sst;
        try {					// rule xxx,
            sst = st.nextToken().trim();
            System.out.println("sst = " + sst);
            try {                		// "rule"
                if(!sst.substring(0,5).trim().equalsIgnoreCase("rule")) {
                    System.out.println("Error. Falta palabra reservada RULE... ");
                    return false;
                }
            }catch(Exception e) {
                System.out.println("Error. Definici�n de regla incompleta");
                return false;
            }
            try {                   //   (new Integer(ruleN))   xxx
                try {
                    this.index = (new Integer(sst.substring(5,sst.length()))).intValue();
                } catch (NumberFormatException nfe) {
                    System.out.println("Error en el n�mero de la regla...");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Error. Definicion incompleta en notaci�n de regla 1");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error. Definicion incompleta de regla 1");
            return false;
        }

        try {						// on event
            sst = st.nextToken().trim();
            System.out.println("sst = " + sst);
            if(!sst.substring(0,3).trim().equalsIgnoreCase("on")) {
                System.out.println("Error. Falta palabra reservada ON... ");
                return false;
            }
// Verifica si el evento ya existe en el vector de eventos
            System.out.println("Verifica si el evento ya existe en el vector de eventos");
            System.out.println("sst.substring(3,sst.length()).trim() : " + sst.substring(3,sst.length()).trim());
            if((indexEvent = Automata.exists(sst.substring(3,sst.length()).trim())) < 0) {
                System.out.println("indexEvent1 = " + indexEvent);
                try {
            /*Event ev =*/ indexEvent = (new Event(sst.substring(3,sst.length()).trim())).getIndex();
                    System.out.println("indexEvent2 = " + indexEvent);
                } catch (EventWrongException ewe) {
                    return false;
                }
            }
            System.out.println("indexEvent3 = " + indexEvent);

        } catch(Exception e) {
            System.out.println("Error. Definicion incompleta de regla 2");
            return false;
        }
        try {						// if condition
            sst = st.nextToken().trim();
            System.out.println("sst = " + sst);
            condition = new Condition(sst);
        } catch(ConditionWrongException e) {
            System.out.println("Error. Error en la definici�n de la Condici�n.");
            return false;
        } catch(Exception e) {
            System.out.println("Error. Definicion incompleta de regla 3");
            return false;
        }
        try {						// then action
            sst = st.nextToken(";").trim();		// Agregar clase Action
            sst = sst.substring(1,sst.length()).trim();
            System.out.println("sst = " + sst);
            try {
                action = new ActionRule(sst);
            } catch(ActionWrongException ae) {
                System.out.println("Error. En definicion de acci�n");
                return false;
            }
/*      this.asql = sst;
      this.action = nombreAccion(sst); */

//      action = sst;
        } catch(Exception e) {
            System.out.println("Error. Definicion incompleta de regla 4");
            return false;
        }

        if(st.hasMoreTokens()) return false;
        return true;
    }

    /** Assign the index event */
    public void setIndex(int index) {
        this.index = index;
    }

    /** Return the Condition object */
    public Condition getCondition() {
        return this.condition;
    }

    /** Returns the action string */
    public ActionRule getActionRule() {
        System.out.println("getActionRule() = " + this.action);
        return this.action;
    }

    /** Returns the index event */
    public int getIndexEvent() {
        return this.indexEvent;
    }

    public String toString() {
        String s = new String("Rule[" + this.index + "] : " + "\nOn " + this.indexEvent);
        s = s.concat("\nIf " + condition.toString());
        s = s.concat("\nThen " + action.toString());
        return s;
    }
}
