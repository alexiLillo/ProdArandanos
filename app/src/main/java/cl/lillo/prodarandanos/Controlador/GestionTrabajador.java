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
import cl.lillo.prodarandanos.Modelo.Tara;
import cl.lillo.prodarandanos.Modelo.Trabajador;

/**
 * Created by Usuario on 31/08/2016.
 */
public class GestionTrabajador {

    private static final String TAG = "gestionTrabajador";

    private ConexionHelperSQLite helper;
    private ConexionHelperSQLServer helperSQLServer;

    public GestionTrabajador(Context context) {
        helper = new ConexionHelperSQLite(context);
        helperSQLServer = new ConexionHelperSQLServer();
    }

    public boolean insertLocal(Trabajador trabajador) {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Rut", trabajador.getRut());
            cv.put("Nombre", trabajador.getNombre());
            cv.put("Apellido", trabajador.getApellido());
            cv.put("QRrut", trabajador.getQRrut());
            data.insertWithOnConflict("Trabajador", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            data.close();
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "...Error al insertar tabla Trabajador local: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteLocal() {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            data.delete("Trabajador", null, null);
            data.close();
        } catch (Exception ex) {
            Log.w(TAG, "...Error al vaciar tabla Trabajador: " + ex.getMessage());
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
                String query = "select * from Trabajador";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Trabajador trabajador = Trabajador.getInstance();
                    trabajador.setRut(rs.getString("Rut"));
                    trabajador.setNombre(rs.getString("Nombre"));
                    trabajador.setApellido(rs.getString("Apellido"));
                    trabajador.setQRrut(rs.getString("QRrut"));

                    insertLocal(trabajador);
                }
                con.close();
            }
        } catch (Exception ex) {
            Log.w(TAG, "...Error al seleccionar tabla Trabajador del servidor: " + ex.getMessage());
            return false;
        }
        return true;
    }
}
