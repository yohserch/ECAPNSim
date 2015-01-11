package BdConnection;

/**
 *
 * @author luismario
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

public class MySQLConnection{

    Connection con = null;
    String url;
    String driver;
    String user;
    String password;
    Vector<String> result;

    String DB;

    public MySQLConnection(){
        iniciarConexion();
    }

    private void iniciarConexion(){

        url="jdbc:mysql://localhost/test";
        driver="com.mysql.jdbc.Driver";
        user="root";
        password="lmansonrd";

        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);

        }catch(SQLException e){
            System.err.println("Error SQL al intentar conectar con la base de datos "+ e);
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);
        }catch(ClassNotFoundException e){
            System.err.println("No se pudo cargar la clase " + e);
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cerrarConexion(){
        try{
            con.close();
        }catch(SQLException e){
            System.err.println("No se pudo cerrar la conexión con la base de datos "+e);
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);
        }
    }

    public Vector<String> showDatabases(){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);
        try{
            s = con.createStatement();
            rs = s.executeQuery("show databases");

            while(rs.next()){
                result.add(rs.getString(1));
            }


        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }

    public Vector<String> showTables(String database){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);

        try{
            s = con.createStatement();
            rs = s.executeQuery("use "+database);
            DB=database;

            rs=s.executeQuery("show tables");
            while(rs.next()){
                result.add(rs.getString(1));
            }

        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }
    public Vector<String> showColumns(String table){

        Statement s;
        ResultSet rs;
        result=new Vector<String>(1,1);

        try{
            s = con.createStatement();

            rs=s.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name ='"+table+"';");
            while(rs.next()){
                result.add(rs.getString(1));
            }

        }catch(SQLException e){
            System.out.println("Problemas SQL "+e);
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);

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
            JOptionPane.showMessageDialog(null,e,"MySQL",JOptionPane.ERROR_MESSAGE);

        }
    }
}
