package cl.lillo.prodarandanos.Controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import cl.lillo.prodarandanos.Modelo.ConexionHelperSQLServer;
import cl.lillo.prodarandanos.Modelo.ConexionHelperSQLite;
import cl.lillo.prodarandanos.Modelo.Pesaje;

/**
 * Created by Usuario on 31/08/2016.
 */
public class GestionPesaje {

    private static final String TAG = "gestionPesaje";

    private ConexionHelperSQLite helper;
    private ConexionHelperSQLServer helperSQLServer;

    public GestionPesaje(Context context) {
        helper = new ConexionHelperSQLite(context);
        helperSQLServer = new ConexionHelperSQLServer();
    }

    public boolean insertLocal(Pesaje pesaje) {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Producto", pesaje.getProducto());
            cv.put("QRenvase", pesaje.getQRenvase());
            cv.put("RutTrabajador", pesaje.getRutTrabajador());
            cv.put("RutPesador", pesaje.getRutPesador());
            cv.put("Fundo", pesaje.getFundo());
            cv.put("Potrero", pesaje.getPotrero());
            cv.put("Sector", pesaje.getSector());
            cv.put("Variedad", pesaje.getVariedad());
            cv.put("Cuartel", pesaje.getCuartel());
            cv.put("FechaHora", pesaje.getFechaHora());
            cv.put("PesoNeto", pesaje.getPesoNeto());
            cv.put("Tara", pesaje.getTara());
            cv.put("Formato", pesaje.getFormato());
            cv.put("TotalCantidad", pesaje.getTotalCantidad());
            cv.put("Factor", pesaje.getFactor());
            cv.put("Cantidad", pesaje.getCantidad());
            cv.put("Lectura_SVAL", pesaje.getLectura_SVAL());
            cv.put("ID_Map", pesaje.getID_Map());
            data.insertWithOnConflict("Pesaje", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            data.close();
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "...Error al insertar pesaje local: " + ex.getMessage());
            return false;
        }
    }

    public boolean insertLocalSync(Pesaje pesaje) {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Producto", pesaje.getProducto());
            cv.put("QRenvase", pesaje.getQRenvase());
            cv.put("RutTrabajador", pesaje.getRutTrabajador());
            cv.put("RutPesador", pesaje.getRutPesador());
            cv.put("Fundo", pesaje.getFundo());
            cv.put("Potrero", pesaje.getPotrero());
            cv.put("Sector", pesaje.getSector());
            cv.put("Variedad", pesaje.getVariedad());
            cv.put("Cuartel", pesaje.getCuartel());
            cv.put("FechaHora", pesaje.getFechaHora());
            cv.put("PesoNeto", pesaje.getPesoNeto());
            cv.put("Tara", pesaje.getTara());
            cv.put("Formato", pesaje.getFormato());
            cv.put("TotalCantidad", pesaje.getTotalCantidad());
            cv.put("Factor", pesaje.getFactor());
            cv.put("Cantidad", pesaje.getCantidad());
            cv.put("Lectura_SVAL", pesaje.getLectura_SVAL());
            cv.put("ID_Map", pesaje.getID_Map());
            data.insertWithOnConflict("PesajeSync", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            data.close();
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "...Error al insertar pesaje local SYNC: " + ex.getMessage());
            return false;
        }
    }

    public ArrayList<Pesaje> selectLocalSync() {
        ArrayList<Pesaje> listaPesajes = new ArrayList<>();

        try {
            SQLiteDatabase data = helper.getReadableDatabase();
            Cursor cursor = data.rawQuery("select * from PesajeSync", null);
            while (cursor.moveToNext()) {
                Pesaje pesaje = Pesaje.getInstance();
                pesaje.setProducto(cursor.getString(0));
                pesaje.setQRenvase(cursor.getString(1));
                pesaje.setRutTrabajador(cursor.getString(2));
                pesaje.setRutPesador(cursor.getString(3));
                pesaje.setFundo(cursor.getString(4));
                pesaje.setPotrero(cursor.getString(5));
                pesaje.setSector(cursor.getString(6));
                pesaje.setVariedad(cursor.getString(7));
                pesaje.setCuartel(cursor.getString(8));
                pesaje.setFechaHora(cursor.getString(9));
                pesaje.setPesoNeto(cursor.getDouble(10));
                pesaje.setTara(cursor.getDouble(11));
                pesaje.setFormato(cursor.getString(12));
                pesaje.setTotalCantidad(cursor.getDouble(13));
                pesaje.setFactor(cursor.getDouble(14));
                pesaje.setCantidad(cursor.getDouble(15));
                pesaje.setLectura_SVAL(cursor.getString(16));
                pesaje.setID_Map(cursor.getInt(17));
                listaPesajes.add(pesaje);
            }
            data.close();
        } catch (Exception ex) {
            Log.w(TAG, "...Error al leer pesaje local SYNC: " + ex.getMessage());
        }

        return listaPesajes;
    }

    public boolean deleteLocalSync() {
        try {
            SQLiteDatabase data = helper.getWritableDatabase();
            data.delete("PesajeSync", null, null);
            data.close();
        } catch (Exception ex) {
            Log.w(TAG, "...Error al vaciar tabla PesajeSync: " + ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean selectLocalInsertServer() {
        ArrayList<Pesaje> listaPesajes = selectLocalSync();
        for (Pesaje p : listaPesajes) {
            try {
                Connection con = helperSQLServer.CONN();
                if (con == null) {
                    Log.w(TAG, "...Error al conectar con el servidor");
                    return false;
                } else {
                    //Consulta SQL
                    String query = "insert into Pesaje values ('" + p.getProducto() + "', '" + p.getQRenvase() + "', '" + p.getRutTrabajador() + "', '" + p.getRutPesador() + "', '" + p.getFundo() + "', '" + p.getPotrero() + "', '" + p.getSector() + "', '" + p.getVariedad() + "', '" + p.getCuartel() + "', '" + p.getFechaHora() + "', " + p.getPesoNeto() + ", " + p.getTara() + ", '" + p.getFormato() + "', " + p.getTotalCantidad() + ", " + p.getFactor() + ", " + p.getCantidad() + ", '" + p.getLectura_SVAL() + "', " + p.getID_Map() + ")";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    con.close();
                }
            } catch (Exception ex) {
                Log.w(TAG, "...Error al insertar pesaje en el servidor: " + ex.getMessage());
                return false;
            }
        }
        return true;
    }

}
