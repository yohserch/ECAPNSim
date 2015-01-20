package Components;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by serch on 19/01/15.
 */
public class Tabla {
    public String table;
    public ArrayList fields;

    public Tabla(String name, ArrayList f) {
        table = name;
        fields = f;
    }

    public String getTableName() {
        return table;
    }

    public ArrayList getTableFields() {
        ArrayList a = new ArrayList();
        String f;
        StringTokenizer stringTokenizer;
        for (int i = 0; i < fields.size(); i++) {
            f = (String)fields.get(i);
            stringTokenizer = new StringTokenizer(f);
            a.add(stringTokenizer.nextToken());
        }
        return a;
    }

    public String getFieldType(String field) {
        String c, type = "", t, f;
        StringTokenizer stringTokenizer;
        for (int i = 0; i < fields.size(); i++) {
            f = (String)fields.get(i);
            stringTokenizer = new StringTokenizer(f);
            c = stringTokenizer.nextToken();
            t = stringTokenizer.nextToken();
            if (c.equals(field))
                type = t;
        }
        return type;
    }

}
