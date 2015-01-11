package BdConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author luismario
 */
public class PostgreSQLConnection {

    Connection con = null;
    String url;
    String driver;
    String user;
    String password;
    Vector<String> result;

    public PostgreSQLConnection(){
        iniciarConexion("postgres");
    }

    private void iniciarConexion(String bd){

        url="jdbc:postgresql://127.0.0.1:5432/"+bd;
        driver="org.postgresql.Driver";
        user="postgres";
        password="passwd";

        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
        }catch(SQLException e){
            System.err.println("Error SQL al intentar conectar con la base de datos "+ e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);
        }catch(ClassNotFoundException e){
            System.err.println("No se pudo cargar la clase " + e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cerrarConexion(){
        try{
            con.close();
        }catch(SQLException e){
            System.err.println("No se pudo cerrar la conexión con la base de datos "+e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);
        }
    }

    public Vector<String> showDatabases(){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);
        try{
            s = con.createStatement();
            rs = s.executeQuery("Select datname From pg_database");

            while(rs.next()){
                result.add(rs.getString(1));
            }


        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }

    public Vector<String> showTables(String database){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);
        this.cerrarConexion();
        this.iniciarConexion(database);
        try{
            s = con.createStatement();


            rs=s.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
            while(rs.next()){
                result.add(rs.getString(1));
            }

        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }

    public Vector<String> showColumns(String table){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);

        try{
            s = con.createStatement();

            System.out.println("SELECT column_name FROM information_schema.columns WHERE table_name ='"+table+"';");
            rs=s.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name ='"+table+"';");
            while(rs.next()){
                result.add(rs.getString(1));
            }

        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }
    public void execute(String sql)
    {
        sql=sql.trim();
        StringTokenizer inst=new StringTokenizer(sql,";");
        Statement s;
        String i;
        try{
            s = con.createStatement();

            while(inst.hasMoreElements())
            {
                i=inst.nextToken();
                s.execute(i);
            }

        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"PSQL",JOptionPane.ERROR_MESSAGE);

        }
    }
}

