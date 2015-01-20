package Components;

import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JTextField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Angel
 */
import BdConnection.*;

class SQListener implements ItemListener {

    private SQL sql;
    private Choice Table1;
    private Choice att1;
    private Choice att2;
    private Choice att3;
    private Choice att4;
    private Choice att5;
    private JTextField table2;
    private Vector<String> tables;
    public int DBM;
    public String databaseSelected;

    public SQListener(SQL sql, Choice Table1, Choice Att1, Choice Att2, Choice Att3, Choice Att4, Choice Att5, JTextField Tables1,Vector<String> tables,int DBM)
    {
        this.sql=sql;
        this.Table1=Table1;
        this.att1=Att1;
        this.att2=Att2;
        this.att3=Att3;
        this.att4=Att4;
        this.att5=Att5;
        this.table2=Tables1;
        this.tables=tables;
        this.DBM=DBM;

    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        System.out.println(DBM);
        ArrayList lista=new ArrayList();
        lista=sql.getFields(Table1.getItem(Table1.getSelectedIndex()));
        table2.setText(Table1.getItem(Table1.getSelectedIndex()));
        att1.removeAll();
        att2.removeAll();
        att3.removeAll();
        att4.removeAll();
        att5.removeAll();

        Vector<String> fields=null;
        String tb=Table1.getSelectedItem();
        if(DBM==0)
        {
            MySQLConnection MSQC=new MySQLConnection();

            fields=MSQC.showColumns(tb);


        }
        else if(DBM==1)
        {
            System.out.println("#Postgres#");
            PostgreSQLConnection PSQC=new PostgreSQLConnection();
            PSQC.showTables(databaseSelected);
            fields=PSQC.showColumns(tb);


        }
        for(int i=0;i<fields.size();i++)
        {
            att1.add(fields.get(i));
            att2.add(fields.get(i));
            att3.add(fields.get(i));
            att4.add(fields.get(i));
            att5.add(fields.get(i));
        }

    }

}

