package cl.lillo.prodarandanos.Otros;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
        escanear();
        Toast.makeText(this, "Escanear código de Pesador", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Pesador no registrado!", Toast.LENGTH_SHORT).show();
                    error();
                    escanear();
                }

            }
        }
    }

    public static boolean validarRut(String rut) {
        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
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
}
