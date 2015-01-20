package Components;

import java.io.Serializable;

/**
 * Created by serch on 19/01/15.
 */
public class Regla implements Serializable{
    private String condition;
    private String event;
    private String action;
    private int index;

    public Regla(String condition, String event, String action, int index) {
        this.condition = condition;
        this.event = event;
        this.action = action;
        this.index = index;
    }

    @Override
    public String toString() {
        String regla = "\n Rule " + index;
        regla += "\n in " + event;
        regla += "\n if " + condition;
        regla += "\n then " + action;
        return regla;
    }
}
