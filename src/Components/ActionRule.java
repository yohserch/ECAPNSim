package Components;

import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by serch on 10/01/15.
 */
public class ActionRule implements Serializable{
    /** Action List Description **/
    private String description;
    /** Variable that stores the list of each rule actions **/
    Vector vActions;
    // this.sql = sst;
    // this.action = nombreAccion(sst);
    private int valueSt = 1; //old = 0 or new = 1
    /** Variable that stores the name of the condition to evaluate **/
    private Vector vNameCondition;
    /** Variable that stores the name of action to do **/
    // private String action;
    // private String actionSQL;
    // private String tableName;
    // private String query;

    public ActionRule(String description) throws ActionWrongException{
    	if(!description.trim().toLowerCase().startsWith("then "))
    		throw new ActionWrongException();
    	this.description = description.trim().substring(5, description.length()).trim();
    	if(!verify())
    		throw new ActionWrongException();
    }

    public boolean verify() {
    	System.out.println("Verification stage of action");
    	this.vActions = new Vector();
    	try {
    		divideActComp(description);
    	} catch(Exception e) {
    		System.out.println("ECA Error: exception of divideAct()");
    		return false;
    	}
    	System.out.println("Finish Verification stage.... return true");
    	return true;
    }

    /** Divide and gets different values of the conditional part **/
    public void divideActComp(String condition) throws Exception {
    	String sst, sstv;
    	StringTokenizer stringTokenizer = new StringTokenizer(condition);
    	while(stringTokenizer.hasMoreTokens()) {
    		sst = stringTokenizer.nextToken("&").trim();
    		System.out.println(sst + ">" + sst.length());
    		try {
    			vActions.add(new Action(sst));
    		} catch(ActionWrongException e) {
    			throw new MyException("Error in the part action of rule");
    		}
    	}
    }

    public Vector getElements() {
    	return this.vActions;
    }

    public String toString() {
    	return this.description;
    }
}
