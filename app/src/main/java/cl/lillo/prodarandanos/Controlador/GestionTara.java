package cl.lillo.prodarandanos.Controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import cl.lillo.prodarandanos.Modelo.ConexionHelperSQLServer;
import cl.lillo.prodarandanos.Modelo.ConexionHelperSQLite;
import cl.lillo.prodarandanos.Modelo.TablaVista;
import cl.lillo.prodarandanos.Modelo.Tara;

/**
 * Created by Usuario on 31/08/2016.
 */
public class GestionTara {

    private static final String TAG = "gestionTara";

    private ConexionHelperSQLite helper;
    private ConexionHelperSQLServer helperSQLServer;

    public GestionTara(Context context) {
        helper = new ConexionHelperSQLite(context);
        helperSQLServer = new ConexionHelperSQLServer();
    }

    public boolean insertLocal(Tara tara) {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ID_Tara", tara.getID_Tara());
            cv.put("Peso", tara.getPeso());
            cv.put("Producto", tara.getProducto());
            cv.put("Formato", tara.getFormato());
            cv.put("Descripcion", tara.getDescripcion());
            data.insertWithOnConflict("Tara", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            data.close();
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "...Error al insertar tabla Tara local: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteLocal() {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            data.delete("Tara", null, null);
            data.close();
        } catch (Exception ex) {
            Log.w(TAG, "...Error al vaciar tabla Tara: " + ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean selectServerInsertLocal() {
        try {
            Connection con = helperSQLServer.CONN();
            if (con == null) {
                return false;
            } else if (deleteLocal()){
                //Consulta SQL
                String query = "select * from Tara";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Tara tara = Tara.getInstance();
                    tara.setID_Tara(rs.getString("ID_Tara"));
                    tara.setPeso(rs.getDouble("Peso"));
                    tara.setProducto(rs.getString("Producto"));
                    tara.setFormato(rs.getString("Formato"));
                    tara.setDescripcion(rs.getString("Descripcion"));

                    insertLocal(tara);
                }
                con.close();
            }
        } catch (Exception ex) {
            Log.w(TAG, "...Error al seleccionar tabla Tara del servidor: " + ex.getMessage());
            return false;
        }
        return true;
    }
}
