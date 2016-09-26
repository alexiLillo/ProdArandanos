package cl.lillo.prodarandanos.Otros;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cl.lillo.prodarandanos.Controlador.GestionTrabajador;
import cl.lillo.prodarandanos.MainActivity;
import cl.lillo.prodarandanos.R;

/**
 * Created by Usuario on 06/09/2016.
 */
public class LoginPesador extends Activity {
    String scanContent;
    String scanFormat;
    private GestionTrabajador gestionTrabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestionTrabajador = new GestionTrabajador(this);
        if (conectado(this)) {
            if (fechaCorrecta()) {
                escanear();
                Toast.makeText(this, "Escanear código de Pesador", Toast.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Atención!")
                        .setMessage("La fecha del celular parece ser incorrecta, por favor configure la fecha y hora del dispositivo correctamente.\nFecha del servidor: " + gestionTrabajador.getServerDate() + " " + gestionTrabajador.getServerTime())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 0);
                                System.exit(0);                            }
                        }).show();
            }
        } else {
            escanear();
            Toast.makeText(this, "Escanear código de Pesador", Toast.LENGTH_LONG).show();
        }
    }

    public boolean fechaCorrecta() {
        String[] split1 = gestionTrabajador.getServerTime().split(":");
        String[] split2 = getLocalTime().split(":");
        int horaServer = Integer.parseInt(split1[0]);
        int horaLocal = Integer.parseInt(split2[0]);
        int minServer = Integer.parseInt(split1[1]);
        int minLocal = Integer.parseInt(split2[1]);
        int diferenciaHoras = horaServer - horaLocal;
        int diferenciaMins = minLocal - minServer;
        if (diferenciaHoras == 0) {
            if (gestionTrabajador.getServerDate().compareTo(getLocalDate()) == 0 && (diferenciaMins <= 5 && diferenciaMins >= -5)) {
                return true;
            } else {
                return false;
            }
        } else if (diferenciaHoras == 1 || diferenciaHoras == -1) {
            if (gestionTrabajador.getServerDate().compareTo(getLocalDate()) == 0 && (diferenciaMins >= 55 || diferenciaMins <= -55)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getLocalDate() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dia = "" + day;
        int month = c.get(Calendar.MONTH) + 1;
        String mes = "" + month;
        int year = c.get(Calendar.YEAR);
        if (day < 10)
            dia = "0" + day;
        if (month < 10)
            mes = "0" + mes;
        String fecha = dia + "/" + mes + "/" + year;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String hora = "" + hour;
        int min = c.get(Calendar.MINUTE);
        String minu = "" + min;
        if (hour < 10)
            hora = "0" + hour;
        if (min < 10)
            minu = "0" + min;
        String horario = hora + ":" + minu;

        return fecha;
    }

    public String getLocalTime() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dia = "" + day;
        int month = c.get(Calendar.MONTH) + 1;
        String mes = "" + month;
        int year = c.get(Calendar.YEAR);
        if (day < 10)
            dia = "0" + day;
        if (month < 10)
            mes = "0" + mes;
        String fecha = dia + "/" + mes + "/" + year;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String hora = "" + hour;
        int min = c.get(Calendar.MINUTE);
        String minu = "" + min;
        if (hour < 10)
            hora = "0" + hour;
        if (min < 10)
            minu = "0" + min;
        String horario = hora + ":" + minu;

        return horario;
    }

    public void escanear() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt("Escanear código de Pesador");
        scanIntegrator.setBeepEnabled(false);
        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
                if (validarRut(scanContent)) {
                    pop();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("pesador", scanContent);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Código incorrecto!", Toast.LENGTH_SHORT).show();
                    error();
                    escanear();
                }

            }
        }
    }

    public static boolean validarRut(String rut) {
        boolean validacion = false;
        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));
            char dv = rut.charAt(rut.length() - 1);
            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    private void pop() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pop);
        mp.start();
    }

    private void ok() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.ok);
        mp.setVolume(50, 50);
        mp.start();
    }

    private void error() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.error);
        mp.setVolume(50, 50);
        mp.start();
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
