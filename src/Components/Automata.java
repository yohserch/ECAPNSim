package Components;

import javax.swing.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by serch on 17/01/15.
 */
public class Automata {
    static Vector tables;
    static Vector ev;
    static Vector rules;

    public Automata() {
        tables = new Vector();
        ev = new Vector();
        rules = new Vector();
    }

    public void abrirObj(String fn) {
        tables = new Vector();
        ev = new Vector();
        rules = new Vector();
        try {
            FileInputStream fileInputStream = new FileInputStream(fn);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                tables = (Vector) objectInputStream.readObject();
            } catch (Exception e) {
                System.out.println("Failed to open object");
            }
            objectInputStream.close();
        } catch(IOException e) {
            System.out.println("Failed to open file");
        }
        System.out.println("open Obj function 2 ...tables.size() = " + tables.size());

        for (int i = 0; i < tables.size(); i++) {
            System.out.println(((Table) tables.elementAt(i)).toString());
        }
    }

    public static int posEnTabla(String nt) {
        int pt = -1;
        System.out.println("posEnTabla tables.size() = " + tables.size() + "; nt = " + nt);

        for (int i = 0; i < tables.size(); i++) {
            System.out.println("Tablas declaradas : " + ((Table) tables.elementAt(i)).getName());
            if (nt.equalsIgnoreCase(((Table) tables.elementAt(i)).getName())) {
                pt = i;
                break;
            }
        }
        System.out.println("pt : " + pt);
        return pt;
    }

    public void guardar(String fn) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fn);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tables);
            objectOutputStream.writeObject(ev);
            objectOutputStream.writeObject(rules);
            System.out.println("Tables.size() = " + tables.size());
            System.out.println("ev.size() = " + ev.size());
            System.out.println("rules.size() = " + rules.size());
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error saving file " + fn + ".obj");
            JOptionPane.showMessageDialog(null, "Error saving the file " + fn + ".obj", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean compile(String s) {
        boolean retVal = true;
        int sind, eind;
        String ss;
        sind = 0;
        eind = s.indexOf(";", sind);
        
        while (eind > -1) {
            ss = s.substring(sind, eind).trim();
            eind++;
            System.out.println(ss + ">" + ss.length());
            switch (whatIs(ss)) {
                case 1:
                    System.out.println("Es tabla ant: " + ss);
                    if (!(new AutTable(ss)).verify()) {
                        System.out.println("Error in table definition");
                        JOptionPane.showMessageDialog(null, "Error in table definition", "Error", JOptionPane.ERROR_MESSAGE);
                        retVal = false;
                    }
                    System.out.println("Es tabla: " + ss);
                    break;
                case 2:
                    System.out.println("Es evento ant: " + ss);
                    if (!(new AutEvent(ss)).verify()) {
                        System.out.println("Error in event definition");
                        JOptionPane.showMessageDialog(null, "Error in event definition", "Error", JOptionPane.ERROR_MESSAGE);
                        retVal = false;
                    }
                    System.out.println("Es evento: " + ss);
                    break;
                case 3:
                    if (!(new AutRule(ss)).verify()) {
                        System.out.println("Error in rule definition");;
                        JOptionPane.showMessageDialog(null, "Error in rule definition", "Error", JOptionPane.ERROR_MESSAGE);
                        retVal = false;
                    }
                    System.out.println("Es regla: " + ss);
                    break;
                default:
                    System.out.println("Error it's not table, event or rule");
                    System.out.println("The system can't identify which is:" + ss);
                    retVal = false;
                    JOptionPane.showMessageDialog(null, "The system can't identify which is", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            sind = eind;
            eind = s.indexOf(";", sind);
        }
        System.out.println("retVal = " + retVal);
        System.out.println("sind = " + sind + "; s.length() = " + s.length());
        System.out.println("s.substring(sind, s.length()) = " + s.substring(sind, s.length()));

        if (sind < s.length()) {
            for (; sind < s.length(); sind++) {
                if (s.charAt(sind) != ' ' && s.charAt((sind)) != '\n' && s.charAt(sind) != '\0') {
                    retVal = false;
                    JOptionPane.showMessageDialog(null, "Missing signs", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        }
        return retVal;
    }

    private int whatIs(String wis) {
        if (wis.trim().substring(0,4).equalsIgnoreCase("rule"))
            return 3;
        if (wis.indexOf(":") > -1) {
            if (wis.indexOf("(") < 0)
                return 2;
            else if (wis.indexOf(":") < wis.indexOf("("))
                return 2;
        }
        if (wis.indexOf("(") > -1)
            return 1;
        return 0;
    }

    public String readFile(String file) {
        String content = new String(), s = new String();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((s = bufferedReader.readLine()) != null) {
                int position;
                if ((position = s.indexOf("//")) > -1)
                    s = s.substring(0, position);
                content = content.concat(s);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
            return null;
        } catch (IOException e) {
            System.out.println("Error while read the content file");
            return null;
        }
        return content;
    }

    public static boolean isATable(String tn) {
        for (int i = 0; i < tables.size(); i++) {
            String ts = ((Table)tables.elementAt(i)).getName();
            System.out.println("tables[" + i + "] = >" + ts + "< tables.size() = " + tables.size());
            if (tn.equals(ts))
                return true;
        }
        return false;
    }

    public static boolean isAFieldOfTable(String fn, String tn) {
        for (int i = 0; i < tables.size(); i++) {
            String ts = ((Table)tables.elementAt(i)).getName();
            if (tn.equals(ts)) {
                if (((Table) tables.elementAt(i)).belong(fn));
                return true;
            }
        }
        return false;
    }

    public static boolean exists(int index, String s) {
        if (index < 0) {
            for (int i = 0; i < ev.size(); i++) {
                if (s.equalsIgnoreCase(((Event)ev.elementAt(i)).getDescription())) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < ev.size(); i++) {
                if (index == ((Event)ev.elementAt(i)).getUserIndex()) {
                    return true;
                }
                if (s.equalsIgnoreCase(((Event)ev.elementAt(i)).getDescription())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int exists(int userIndex) {
        for (int i = 0; i < ev.size(); i++) {
            if (userIndex == ((Event)ev.elementAt(i)).getUserIndex()) {
                return i;
            }
        }
        return -1;
    }

    public static int exists(String s) {
        int position;
        if (s.toLowerCase().startsWith("ec")) {
            try {
                if ((position = exists((new Integer(s.substring(2, s.length()))).intValue())) >= 0) {
                    return position;
                }
            } catch(Exception e) {
                return -1;
            }
        }
        System.out.println("s: " + s);
        if (s.toLowerCase().startsWith("e")) {
            System.out.println("s: <" + s + ">");
            System.out.println("pos: <" + s.substring(1, s.length()) + ">");
            try {
                if ((position = exists((new Integer(s.substring(1, s.length()))).intValue())) >= 0) {
                    System.out.println("pos: <" + position);
                    return position;
                }
            } catch(Exception e) {
                return -1;
            }
        }

        for (int i = 0; i < ev.size(); i++) {
            if (s.equalsIgnoreCase(((Event) ev.elementAt(i)).getDescription())) {
                return i;
            }
        }
        return -1;
    }
}


class AutName {
    String s;

    public AutName(String s) {
        this.s = s;
    }

    public boolean verify() {
        int st = 0;
        char c;
        try {
            c = s.charAt(0);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        if (!(c == '_' || Character.isLetter(c))) {
            return false;
        }

        for (int j = 1; j < s.length(); j++) {
            if (!(s.charAt(j) == '_' || Character.isLetter(s.charAt(j)) ||
                    Character.isDigit(s.charAt(j)))) {
                return false;
            }
        }
        return true;
    }
}


class AutTable {
    String s;

    public AutTable(String s) {
        this.s = s;
    }

    public boolean verify() {
        int parpos = s.indexOf("(");
        String ss = s.substring(0, parpos).trim();
        System.out.println("ss = :" + ss + ">");
        if (!(new AutName(ss)).verify()) {
            System.out.println("Error in table name");
            return false;
        }

        Vector f = new Vector();
        parpos++;
        String strFields;
        int parpos2 = closedParPos(0);
        try {
            strFields = s.substring(parpos, parpos2);
        } catch(StringIndexOutOfBoundsException e) {
            System.out.println("Missed parentheses in table definition");
            return false;
        }
        System.out.println("strFields : <" + strFields + ">");
        StringTokenizer stringTokenizer = new StringTokenizer(strFields);
        String sst;
        while (stringTokenizer.hasMoreTokens()) {
            sst = stringTokenizer.nextToken(",").trim();
            System.out.println(sst + ">" + sst.length());
            StringTokenizer stdt = new StringTokenizer(sst);
            String fieldName, dataType;
            if (stdt.hasMoreTokens()) {
                fieldName = stdt.nextToken();
                System.out.println(fieldName + ">" + fieldName.length());
                if (!(new AutName(fieldName)).verify()) {
                    System.out.println("Error in field name of table " + fieldName);
                    return false;
                }
            } else {
                System.out.println("Error, missing field name");
                return false;
            }
            dataType = "";
            while (stdt.hasMoreTokens()) {
                dataType += stdt.nextToken();
            }
            if (dataType.equals("")) {
                System.out.println("Error, missing field data type");
                return false;
            }

            if (!isDataType(dataType)) {
                System.out.println("Error incorrect data type " + dataType);
                return false;
            }

            f.add(new RowTable(fieldName.toLowerCase(), dataType.toLowerCase()));
            System.out.println("The field **" + sst + "** is correct");
        }
        parpos2++;

        if (parpos2 < s.length()) {
            System.out.println("Missing ';' at instruction end");
            return  false;
        }

        Automata.tables.add(new Table(ss.toLowerCase(), f));
        return true;
    }

    public boolean isDataType(String dt) {
        String dataType[] = {"CHAR(length)", "VARCHAR(length)", "INTEGER", "FLOAT", "NUMERIC(presicion, decimal)", "DATE", "TIME", "TIMESTAMP"};
        for (int i = 0; i < dataType.length; i++) {
            if (dt.equalsIgnoreCase(dataType[i]))
                return true;
        }
        return false;
    }

    public int closedParPos(int pi) {
        int cont = 0;
        boolean flag = false;
        for (int i = pi; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                cont++;
                flag = true;
            } else if(s.charAt(i) == ')') {
                cont--;
            }
            if (flag && cont == 0)
                return i;
        }
        return -1;
    }
}

class AutEvent {
    String s;

    public AutEvent(String s) {
        this.s = s.trim();
    }

    public boolean verify() {
        int i= 0;
        if (!s.toLowerCase().startsWith("e")) {
            System.out.println("Error, the event name must begin with the letter E");
            return false;
        }
        i++;
        if (!Character.isDigit(s.toLowerCase().charAt(i))) {
            if (s.toLowerCase().charAt(i) != 'c') {
                System.out.println("Error, the event name must contain numerical value or start with EC");
                return false;
            } else {
                i++;
                if (!Character.isDigit(s.charAt(i))) {
                    System.out.println("Error the event name must contain a numerical value");
                    return false;
                }
            }
        }
        int iAnt = i;
        while (Character.isDigit(s.charAt(i)))
            i++;
        int index = (new Integer(s.substring(iAnt, i))).intValue();
        while (Character.isWhitespace(s.charAt(i)))
            i++;
        if(s.charAt(i) != ':') {
            System.out.println("Error, the event name must contain ':' ");
            return false;
        }
        i++;
        String ss = s.substring(i, s.length()).toLowerCase().trim();

        try {
            new Event(index, ss);
        } catch(EventWrongException e) {
            return false;
        }
        return true;
    }
}

class AutRule {
    String rule;
    int indexEvent;

    public AutRule(String s) {
        this.rule = s;
    }

    public boolean verify() {
        try {
            new Rule(rule);
        } catch(RuleWrongException e) {
            return false;
        }
        return true;
    }
}