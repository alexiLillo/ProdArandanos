package cl.lillo.prodarandanos.Controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Usuario on 31/08/2016.
 */
public class Sync {
    private static final String TAG = "Sync";

    private GestionPesaje gestionPesaje;
    private GestionTablaVista gestionTablaVista;
    private GestionTara gestionTara;
    private GestionTrabajador gestionTrabajador;

    public String sync(boolean syncCompleta, Context context) {
        try {


            if (syncCompleta) {
                //instancia
                gestionPesaje = new GestionPesaje(context);
                gestionTablaVista = new GestionTablaVista(context);
                gestionTara = new GestionTara(context);
                gestionTrabajador = new GestionTrabajador(context);
                //sync todas las tablas
                gestionTablaVista.selectServerInsertLocal();
                gestionTara.selectServerInsertLocal();
                gestionTrabajador.selectServerInsertLocal();
                //sync pesajes
                if (gestionPesaje.selectLocalInsertServer()) {
                    gestionPesaje.deleteLocalSync();
                }
                return "Sincronización completa correcta";
            } else {
                //instancia
                gestionPesaje = new GestionPesaje(context);
                //sync pesaje
                if (gestionPesaje.selectLocalInsertServer()) {
                    gestionPesaje.deleteLocalSync();
                }
                return "Sincronización pesajes correcta";
            }
        } catch (Exception ex) {
            Log.w(TAG, "...Error al sincronizar: " + ex.getMessage());
            return "Error al intentar sincronizar";
        }
    }

    public boolean eventoSyncAll(Context context, boolean syncCompleta) {
        //llamar servicio que sincroniza "bajo cuerda"
        if (conectado(context)) {
            ProgressDialog progress = new ProgressDialog(context);
            progress.setMessage("Sincronizando, por favor espere...");
            new ServicioCompleto(progress, syncCompleta, context).execute();
            return true;
        } else {
            Toast.makeText(context, "Atención! No hay conexión a Internet", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public class ServicioCompleto extends AsyncTask<String, Void, String> {

        private String msj;
        private boolean syncCompleta;
        private Context context;
        ProgressDialog progress;

        public ServicioCompleto(ProgressDialog progress, boolean syncCompleta, Context context) {
            this.progress = progress;
            this.syncCompleta = syncCompleta;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            msj = sync(syncCompleta, context);
            return msj;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean eventoSyncPesaje(Context context, boolean syncCompleta) {
        //llamar servicio que sincroniza "bajo cuerda"
        if (conectado(context)) {
            new ServicioPesaje(syncCompleta, context).execute();
            return true;
        } else {
            //Toast.makeText(view.getContext(), "Atención! No hay conexión a Internet", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public class ServicioPesaje extends AsyncTask<String, Void, String> {

        private String msj;
        private boolean syncCompleta;
        private Context context;


        public ServicioPesaje(boolean syncCompleta, Context context) {

            this.syncCompleta = syncCompleta;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            msj = sync(syncCompleta, context);
            return msj;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("------------------->SYNC PESAJE:" + result);
        }
    }

    public static boolean conectado(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
}
