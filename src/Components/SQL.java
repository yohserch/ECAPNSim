package Components;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by serch on 19/01/15.
 */
public class SQL {
    public String fileSql;
    public ArrayList tableNames;
    public ArrayList fields;

    public SQL(String script) {
        try {
            fileSql = "";
            FileReader fileReader = new FileReader(script);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                fileSql = fileSql + linea + "\n";
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList getTableNames() {
        StringTokenizer tokens=new StringTokenizer(fileSql);
        tableNames=new ArrayList();
        while(tokens.hasMoreTokens())
        {
            String str1=tokens.nextToken();
            if((str1.toUpperCase()).equals("CREATE"))
            {
                String str2=tokens.nextToken();
                if((str2.toUpperCase()).equals("TABLE"))
                    tableNames.add(tokens.nextToken());
            }
        }
        return tableNames;
    }

    public ArrayList getFields(String TableName)
    {
        fields = new ArrayList();
        StringTokenizer tokens = new StringTokenizer(fileSql);
        String desc = "";
        while (tokens.hasMoreTokens()) {
            String str1 = tokens.nextToken();
            if ((str1.toUpperCase()).equals("CREATE")) {
                String str2 = tokens.nextToken();
                if ((str2.toUpperCase()).equals("TABLE"))
                    if (tokens.nextToken().equals(TableName)) {
                        tokens.nextToken();
                        while (tokens.hasMoreTokens()) {
                            String s = "";
                            String aux = tokens.nextToken();
                            if (!aux.equals(")")) {
                                for(int i = 0; i < aux.length(); i++) {
                                    if(aux.charAt(i) == ',')
                                        s = "\n";
                                }
                                desc = desc + aux + " " + s;
                            } else
                                break;
                            }
                        break;
                    }
            }
        }
        String f = "", t = "", tc = "", field;
        StringTokenizer tokens2 = new StringTokenizer(desc, "\n");
        while(tokens2.hasMoreTokens()) {
            String ft = tokens2.nextToken();
            StringTokenizer tokens3 = new StringTokenizer(ft);
            if(tokens3.countTokens() > 1) {
                f = tokens3.nextToken();
                t = tokens3.nextToken();
                int i = t.length() - 1;
                if (t.charAt(i) == ',')
                    tc = t.substring(0, i);
                else
                    tc = t;
                field = f + " " + tc;
                fields.add(field);
            }
        }
        return fields;
    }

    public String getSQL() {
        return fileSql;
    }
}
