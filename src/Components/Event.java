package Components;

import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by serch on 19/01/15.
 */
public class Event implements Serializable{
    static int indTmp = 0;
    // m store the number of events considered to the complex event
    int index, userIndex, type, m = 1;
    String description;
    // Integers that represents the index of event
    Vector events;
    // Store the time interval in which must occur the event
    Interval interval;

    public Event(String description) throws EventWrongException {
        events = new Vector();
        this.index = Automata.ev.size();
        this.userIndex = -1;
        this.description = description;
        if (!verify())
            throw new EventWrongException();
    }

    public Event(int userIndex, String description) throws EventWrongException{
        events = new Vector();
        this.userIndex = userIndex;
        this.index = Automata.ev.size();
        indTmp = (index >= indTmp) ? index + 1 : indTmp;
        this.description = description;
        if (!verify())
            throw new EventWrongException();
    }

    public Event(int userIndex, int type, String description) throws EventWrongException {
        events = new Vector();
        this.userIndex = userIndex;
        this.index = Automata.ev.size();
        indTmp = (index > indTmp) ? index +1 : indTmp;
        this.type = type;
        this.description = description;
        if (!verify())
            throw new EventWrongException();
    }

    public boolean verify() {
        int posColon; // pos of character ','
        String en;    // event name
        String tfn;   // table-field name
        String tn;    // table name
        String fn;    // field name
        int space;
        int i = 0;
        int st = 3;
        i=0;
        type = 0;   // it is a Primitive Event
        Event ev;
        while(i < description.length()) {
            System.out.println("Estado : " + st + " i = " + i);
            switch(st) {
                case 3: //System.out.println("Estado 3:");
                    switch(description.charAt(i)) {
                        case 'i' :
                            if(!description.substring(0,7).equals("insert_")) {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            i+=7;
                            st = 4;
                            type = 0;
                            break;
                        case 'd' :
                            if(!description.substring(0,7).equals("delete_")) {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            i+=7;
                            st = 6;
                            type = 1;
                            break;
                        case 'u' :
                            if(!description.substring(0,7).equals("update_")) {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            i+=7;
                            st = 5;
                            type = 2;
                            break;
                        case 'a' :
                            if(description.substring(0,3).equals("and")) {
                                type = 4;	// AND
                                i+=3;
                                st = 7;
                            } else if(description.substring(0,3).equals("any")) {
                                type = 12;	// ANY
                                i+=3;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 'o' :
                            if(description.substring(0,2).equals("or")) {
                                type = 5;	// OR
                                i+=2;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 'n' :
                            if(description.substring(0,3).equals("not")) {
                                type = 6;	// NEGATION
                                i+=3;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 's' :
                            if(description.substring(0,7).equals("select_")) {
                                i+=7;
                                st = 5; /* ****************CHECAR */
                                type = 3;
                            } else if(description.substring(0,3).equals("seq")) {
                                type = 7;	// SEQUENCE
                                i+=3;
                                st = 7;
                            } else if(description.substring(0,3).equals("sim")) {
                                type = 11;	// SIMULTANEOUS
                                i+=3;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 'c' :
                            if(description.substring(0,4).equals("clos")) {
                                type = 8;	// CLOSURE
                                i+=4;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 'l' :
                            if(description.substring(0,4).equals("last")) {
                                type = 9;	// LAST
                                i+=4;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        case 't' :
                            if(description.substring(0,5).equals("times")) {
                                type = 10;	// TIMES
                                i+=5;
                                st = 7;
                            } else {
                                System.out.println("Error. nombre de evento invalido.");
                                return false;
                            }
                            break;
                        default :
                            System.out.println("no es un nombre de evento valido...");
                            return false;
                    }
                    break;
                case 4:
                    if((space = description.indexOf(" ")) < 0) {
                        space = description.length();
                        st = 24;
                    } else
                        st = 23;
                        tn = description.substring(7,space);
                    if(!(new AutName(tn)).verify()) {
                        System.out.println("Error. Nombre de tabla invalido...");
                        return false;
                    }
                    if(!Automata.isATable(tn)) {
                        System.out.println("Error. Nombre de tabla no existe en la BD...");
                        return false;
                    }
                    i = space;
                    break;
                case 5:
                    if((space = description.indexOf(" ")) < 0) {
                        space = description.length();
                        st = 24;
                    } else
                        st = 23;
                    tfn = description.substring(7,space);
                    int _pos = tfn.indexOf("_");
                    try {
                        tn = tfn.substring(0,_pos);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Error. En la especificaci�n de la tabla y campo de UPDATE");
                        return false;
                    }

                    if(!(new AutName(tn)).verify()) {
                        System.out.println("Error. Nombre de tabla invalido...");
                        return false;
                    }
                    fn = tfn.substring(_pos+1, tfn.length());
                    if(!Automata.isAFieldOfTable(fn,tn)) {
                        System.out.println("Error. Nombre del campo no pertenece a la tabla...");
                        return false;
                    }
                    i = space;
                    break;
                case 6:
                    if((space = description.indexOf(" ")) < 0) {
                        space = description.length();
                        st = 24;
                    } else
                        st = 23;
                    tn = description.substring(7,space);
                    if(!(new AutName(tn)).verify()) {
                        System.out.println("Error. Nombre de tabla invalido...");
                        return false;
                    }
                    if(!Automata.isATable(tn)) {
                        System.out.println("Error. Nombre de tabla no existe en la BD...");
                        return false;
                    }
                    i = space;
                    break;
                case 7:
                    String eventStr;
                    int exs=-1;
                    String rest = description.substring(i, description.length()).trim();
                    int posClose;
                    if((posClose = rest.indexOf(")")) < 0)
                        return false;
                    String eventList = rest.substring(0, posClose+1).trim();
                    if(eventList.charAt(0) != '(' || eventList.charAt(eventList.length()-1) != ')')
                        return false;
                    eventList = eventList.substring(1,eventList.length()-1);
                    StringTokenizer ste = new StringTokenizer(eventList,":");
                    if((type == 12 || type == 10) && ste.hasMoreTokens())
                        m = (new Integer(ste.nextToken())).intValue();

                    while(ste.hasMoreTokens()) {
                        eventStr = ste.nextToken();
                        if((exs = Automata.exists(eventStr)) < 0) {
                            try {
                                exs = (new Event(eventStr)).getIndex();
                            } catch(EventWrongException iwe) {
                                return false;
                            }
                        }
                        events.addElement(new Integer(exs));
                    }

                    if((space = rest.substring(posClose+1,rest.length()).trim().toLowerCase().indexOf(" ")) < 0) {
                        space = description.length();
                        st = 24;
                    } else
                        st = 23;
                    i += posClose+1;
                    break;
                case 16:
                    st = 24;
                    i++;
                    break;
                case 23: String intDesc = description.substring(i,description.length()).trim();
                    if(!intDesc.toLowerCase().startsWith("between")) {
                        System.out.println("Error. en el intervalo, no comienza con BETWEEN");
                        return false;
                    }
                    intDesc = intDesc.substring(7,intDesc.length()).trim();
                    if(intDesc.charAt(0) != '(' || intDesc.charAt(intDesc.length()-1) != ')')
                        return false;
                    intDesc = intDesc.substring(1,intDesc.length()-1).trim();
                    int posDiag;
                    if((posDiag = intDesc.indexOf("/")) < 0)
                        return false;
                    String interval1 = intDesc.substring(0,posDiag).trim();
                    String interval2 = intDesc.substring(posDiag+1,intDesc.length()).trim();
                    try {
                        IntervalDate id1 = new IntervalDate(interval1);
                        IntervalDate id2 = new IntervalDate(interval2);
                        interval = new Interval(id1, id2);
                    } catch (Exception iwe) {
                        System.out.println("Error. En la definici�n del intervalo.");
                        return false;
                    }
                    st = 16;
                    i = description.length()-1;
                    break;
            }
        }

        System.out.println("st = " + st + ", Automata.exists(userIndex,description) = " + Automata.exists(userIndex,description));
        System.out.println("userIndex = " + userIndex + ", description = " + description);

        if(st == 24 && !Automata.exists(userIndex,description)) {
            System.out.println("Nombre de evento : " + this.getEventName());
            Automata.ev.add(this);
            return true;
        }
        else return false;
    }

    public int getIndex() {
        return this.index;
    }

    public Vector getEvents() {
        return this.events;
    }

    public int getUserIndex() {
        return this.getUserIndex();
    }

    public int getType() {
        return this.type;
    }

    public int getMValue() {
        return this.m;
    }

    public String getDescription() {
        return description;
    }

    public String getEventName() {
        String name = (type > 3) ? new String("E") : new String("E");
        name = name.concat((new Integer(index)).toString());
        return name;
    }

    @Override
    public String toString() {
        String typ = new String();
        switch(this.type) {
            case 0: typ = "INSERT"; break;
            case 1: typ = "DELETE"; break;
            case 2: typ = "UPDATE"; break;
            case 3: typ = "SELECT"; break;
            case 4: typ = "AND"; break;
            case 5: typ = "OR"; break;
            case 6: typ = "NOT"; break;
            case 7: typ = "SEQ"; break;
            case 8: typ = "CLOS"; break;
            case 9: typ = "LAST"; break;
            case 10: typ = "TIMES"; break;
            case 11: typ = "SIM"; break;
            case 12: typ = "ANY"; break;
        }
        String s = new String("Event[" + this.index + "] : " + typ);
        if(this.events.size()>0) {
            int i;
            s = s.concat("(");
            for(i=0; i<this.events.size()-1; i++) {
                s = s.concat(((Integer) events.elementAt(i)).intValue() + ", ");
            }
            return (s.concat(((Integer) events.elementAt(i)).intValue() + ");"));
        }
        return s;
    }

    public String getCommand() {
        int position = description.indexOf(" ");
        if (position > 0)
            return description.substring(0, position).trim();
        else
            return description.trim();
    }
}
