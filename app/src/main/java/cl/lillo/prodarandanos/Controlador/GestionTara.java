package cl.lillo.prodarandanos.Controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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

    public ArrayList<String> selectTaraSpinner(){
        ArrayList<String> lista = new ArrayList<>();
        try {
            SQLiteDatabase data = helper.getReadableDatabase();
            Cursor cursor = data.rawQuery("select ID_Tara, Peso, Formato, Descripcion nombreCuartel from Tara", null);
            if (cursor.getCount() > 1) {
                lista.add("Seleccione...");
            }
            while (cursor.moveToNext()) {
                lista.add(cursor.getString(0) + " - " + cursor.getDouble(1) + " - " + cursor.getString(2) + " : " + cursor.getString(3));
            }
            data.close();
        } catch (Exception ex) {
            Log.w(TAG, "...Error al seleccionar * from Tara" + ex.getMessage());
        }
        return lista;
    }

    public Tara selectLocal(String id_tara){
        Tara tara = new Tara();
        try {
            SQLiteDatabase data = helper.getReadableDatabase();
            Cursor cursor = data.rawQuery("select * from Tara where ID_Tara = '" + id_tara + "'", null);
            while (cursor.moveToNext()) {
                tara.setID_Tara(cursor.getString(0));
                tara.setPeso(cursor.getDouble(1));
                tara.setProducto(cursor.getString(2));
                tara.setFormato(cursor.getString(3));
                tara.setDescripcion(cursor.getString(4));
            }
        }catch (Exception ex){
            Log.w(TAG, "...Error al seleccionar desde tabla Tara: " + ex.getMessage());
        }
        return tara;
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
                    Tara tara = new Tara();
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
