package cl.lillo.prodarandanos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cl.lillo.prodarandanos.Controlador.*;
import cl.lillo.prodarandanos.Modelo.*;
import cl.lillo.prodarandanos.Otros.*;

public class MainActivity extends Activity {

    private String fundo = "";
    private String potrero = "";
    private String sector = "";
    private String cuartel = "";
    private String variedad = "";

    private TextView txtKL, txtTrabajador, txtCajas, txtTrabajadorConsulta;

    private EditText txtRut;

    private Spinner spinFundo, spinPotrero, spinSector, spinVariedad, spinCuartel, spinTara;
    String scanContent;
    String scanFormat;

    private ArrayList<String> lista = new ArrayList<>();
    private Object[] listaFinal = new Object[0];
    private int largo;
    private String bandeja1, bandeja2, bandeja3, bandeja4;
    private int cantidadBandejas;

    //Gestiones
    private Sync sync;
    private GestionTablaVista gestionTablaVista;
    private GestionPesaje gestionPesaje;
    private GestionTara gestionTara;

    private Bluetooth bluetooth;
    /**
     * Called when the activity is first created.
     */
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = new Bluetooth(this);

        context = this;

        txtKL = (TextView) findViewById(R.id.txtPesoKL);
        txtRut = (EditText) findViewById(R.id.txtRut);
        spinFundo = (Spinner) findViewById(R.id.spinFundo);
        spinPotrero = (Spinner) findViewById(R.id.spinPotrero);
        spinSector = (Spinner) findViewById(R.id.spinSector);
        spinVariedad = (Spinner) findViewById(R.id.spinVariedad);
        spinCuartel = (Spinner) findViewById(R.id.spinCuartel);
        spinTara = (Spinner) findViewById(R.id.spinTara);

        txtTrabajador = (TextView) findViewById(R.id.txtTrabajador);
        txtCajas = (TextView) findViewById(R.id.txtCajas);
        txtTrabajadorConsulta = (TextView) findViewById(R.id.txtTrabajadorConsulta);

        gestionTablaVista = new GestionTablaVista(this);
        gestionPesaje = new GestionPesaje(this);
        gestionTara = new GestionTara(this);
        sync = new Sync();

        //TABS
        Resources res = getResources();

        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.linearLayout);
        spec.setIndicator("", res.getDrawable(R.drawable.weight));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.linearLayout2);
        spec.setIndicator("", res.getDrawable(R.drawable.card));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab3");
        spec.setContent(R.id.linearLayout3);
        spec.setIndicator("", res.getDrawable(R.drawable.sync));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab4");
        spec.setContent(R.id.linearLayout4);
        spec.setIndicator("", res.getDrawable(R.drawable.menu));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
        //fin tabs

        ArrayAdapter adapterF = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectFundo());
        spinFundo.setAdapter(adapterF);
        spinFundo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinFundo.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    String[] split = spinFundo.getItemAtPosition(position).toString().split("-");
                    fundo = split[1].replace(" ", "");
                    ArrayAdapter adapterP = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectPotrero(fundo));
                    spinPotrero.setAdapter(adapterP);
                } else if (spinFundo.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    fundo = "";
                    potrero = "";
                    sector = "";
                    variedad = "";
                    cuartel = "";
                    ArrayAdapter adapterP = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectPotrero(""));
                    spinPotrero.setAdapter(adapterP);
                    ArrayAdapter adapterS = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectSector("", ""));
                    spinSector.setAdapter(adapterS);
                    ArrayAdapter adapterV = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectVariedad("", "", ""));
                    spinVariedad.setAdapter(adapterV);
                    ArrayAdapter adapterC = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectCuartel("", "", "", ""));
                    spinCuartel.setAdapter(adapterC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinPotrero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinPotrero.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    String[] split = spinPotrero.getItemAtPosition(position).toString().split("-");
                    potrero = split[0].replace(" ", "");
                    ArrayAdapter adapterS = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectSector(fundo, potrero));
                    spinSector.setAdapter(adapterS);
                } else if (spinPotrero.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    potrero = "";
                    sector = "";
                    variedad = "";
                    cuartel = "";
                    ArrayAdapter adapterS = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectSector("", ""));
                    spinSector.setAdapter(adapterS);
                    ArrayAdapter adapterV = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectVariedad("", "", ""));
                    spinVariedad.setAdapter(adapterV);
                    ArrayAdapter adapterC = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectCuartel("", "", "", ""));
                    spinCuartel.setAdapter(adapterC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinSector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinSector.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    String[] split = spinSector.getItemAtPosition(position).toString().split("-");
                    sector = split[0].replace(" ", "");
                    ArrayAdapter adapterV = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectVariedad(fundo, potrero, sector));
                    spinVariedad.setAdapter(adapterV);
                } else if (spinSector.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    sector = "";
                    variedad = "";
                    cuartel = "";
                    ArrayAdapter adapterV = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectVariedad("", "", ""));
                    spinVariedad.setAdapter(adapterV);
                    ArrayAdapter adapterC = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectCuartel("", "", "", ""));
                    spinCuartel.setAdapter(adapterC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinVariedad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinVariedad.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    String[] split = spinVariedad.getItemAtPosition(position).toString().split("-");
                    variedad = split[1].replace(" ", "");
                    ArrayAdapter adapterC = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectCuartel(fundo, potrero, sector, variedad));
                    spinCuartel.setAdapter(adapterC);
                } else if (spinVariedad.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    variedad = "";
                    cuartel = "";
                    ArrayAdapter adapterC = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectCuartel("", "", "", ""));
                    spinCuartel.setAdapter(adapterC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinCuartel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinCuartel.getItemAtPosition(position).toString().equals("Seleccione...")) {
                    String[] split = spinCuartel.getItemAtPosition(position).toString().split("-");
                    cuartel = split[1].replace(" ", "");
                } else {
                    cuartel = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapterTara = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gestionTara.selectTaraSpinner());
        spinTara.setAdapter(adapterTara);

        bluetooth.onCreate();
        //startSyncAuto();
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetooth.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bluetooth.onPause();
    }

    public void tecla1(View view) {
        txtRut.setText(txtRut.getText().toString() + "1");
    }

    public void tecla2(View view) {
        txtRut.setText(txtRut.getText().toString() + "2");
    }

    public void tecla3(View view) {
        txtRut.setText(txtRut.getText().toString() + "3");
    }

    public void tecla4(View view) {
        txtRut.setText(txtRut.getText().toString() + "4");
    }

    public void tecla5(View view) {
        txtRut.setText(txtRut.getText().toString() + "5");
    }

    public void tecla6(View view) {
        txtRut.setText(txtRut.getText().toString() + "6");
    }

    public void tecla7(View view) {
        txtRut.setText(txtRut.getText().toString() + "7");
    }

    public void tecla8(View view) {
        txtRut.setText(txtRut.getText().toString() + "8");
    }

    public void tecla9(View view) {
        txtRut.setText(txtRut.getText().toString() + "9");
    }

    public void tecla0(View view) {
        txtRut.setText(txtRut.getText().toString() + "0");
    }

    public void teclaguion(View view) {
        txtRut.setText(txtRut.getText().toString() + "-");
    }

    public void teclak(View view) {
        txtRut.setText(txtRut.getText().toString() + "K");
    }

    public void teclaborrar(View view) {
        String cadena = txtRut.getText().toString();
        if (!cadena.equals(""))
            txtRut.setText(cadena.substring(0, cadena.length() - 1));
    }

    public void teclaborrarAll(View view) {
        txtRut.setText("");
    }


    //PROCESO DE ESCANEO DE CÓDIGO
    public void scanButton(View view) {
        scan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
            }

            QR qr = QR.getInstance();

            if (qr.getTipoQR().equals("pesaje")) {

                if (scanContent != null) {
                    lista.add(scanContent);
                    Set<String> lista2 = new HashSet<>(lista);
                    listaFinal = lista2.toArray();

                    if (largo < listaFinal.length) {
                        ok();
                        largo = listaFinal.length;
                        if (largo == 1) {
                            txtTrabajador.setText(scanContent);
                            Toast.makeText(this, "Trabajador: " + scanContent, Toast.LENGTH_SHORT).show();
                            cantidadBandejas = 0;
                            scan();
                        }
                        if (largo == 2) {
                            bandeja1 = scanContent;
                            Toast.makeText(this, "Primera bandeja: " + bandeja1, Toast.LENGTH_SHORT).show();
                            cantidadBandejas = 1;
                            txtCajas.setText("1");
                            scan();
                        }
                        if (largo == 3) {
                            bandeja2 = scanContent;
                            Toast.makeText(this, "Segunda bandeja: " + bandeja2, Toast.LENGTH_SHORT).show();
                            cantidadBandejas = 2;
                            txtCajas.setText("2");
                            scan();
                        }
                        if (largo == 4) {
                            bandeja3 = scanContent;
                            Toast.makeText(this, "Tercera bandeja: " + bandeja3, Toast.LENGTH_SHORT).show();
                            cantidadBandejas = 3;
                            txtCajas.setText("3");
                            scan();
                        }
                        if (largo == 5) {
                            bandeja4 = scanContent;
                            Toast.makeText(this, "Cuarta bandeja: " + bandeja4, Toast.LENGTH_SHORT).show();
                            cantidadBandejas = 4;
                            txtCajas.setText("4");
                        }
                    } else {
                        if (largo < 5) {
                            Toast.makeText(this, "Código ya leido!", Toast.LENGTH_SHORT).show();
                            scan();
                            error();
                        }
                    }
                }
            } else if (qr.getTipoQR().equals("consulta")) {
                //hacer la conzulta
                String qrTrabajador = scanContent;
                txtTrabajadorConsulta.setText(qrTrabajador);
                ok();
            }

            scanContent = null;
            onResume();
        } else {
            Toast.makeText(this, "No se escanearon datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertPesaje(View view) {
        if (!fundo.equals("") && !potrero.equals("") && !sector.equals("") && !variedad.equals("") && !cuartel.equals("")) {
            final String id_tara = spinTara.getSelectedItem().toString().substring(0, 2).replace(" ", "");
            Tara tara = gestionTara.selectLocal(id_tara);
            final Pesaje pesaje = new Pesaje();
            pesaje.setProducto(tara.getProducto());
            //pesaje.setQRenvase();
            pesaje.setRutTrabajador(txtTrabajador.getText().toString());
            //rut pesador se debe escanear al iniciar
            pesaje.setRutPesador("s/d");
            pesaje.setFundo(fundo);
            pesaje.setPotrero(potrero);
            pesaje.setSector(sector);
            pesaje.setVariedad(variedad);
            pesaje.setCuartel(cuartel);

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
            int min = c.get(Calendar.MINUTE);
            String hora = hour + ":" + min;

            pesaje.setFechaHora(fecha + " " + hora);
            // PESO SE DEBE RESTAR TARA
            pesaje.setPesoNeto((Double.parseDouble(txtKL.getText().toString()) / cantidadBandejas) - tara.getPeso());
            pesaje.setTara(tara.getPeso());
            pesaje.setFormato(tara.getFormato());
            pesaje.setTotalCantidad(1);
            pesaje.setFactor(1);
            pesaje.setCantidad(1);
            pesaje.setLectura_SVAL("");
            pesaje.setID_Map(gestionTablaVista.lastMapeo());

            new AlertDialog.Builder(this)
                    .setTitle("Guardar pesaje?")
                    .setCancelable(true)
                    .setMessage("Trabajador: " + txtTrabajador.getText().toString() + "\nBandejas: " + cantidadBandejas + "\nPeso Bruto: " + txtKL.getText().toString() + "\nPeso Neto: " + String.valueOf(Double.parseDouble(txtKL.getText().toString()) - (tara.getPeso() * cantidadBandejas)))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (cantidadBandejas == 0) {
                                Toast.makeText(MainActivity.this, "No se escanearon bandejas!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (cantidadBandejas >= 1) {
                                    Pesaje pesaje1 = pesaje;
                                    pesaje1.setQRenvase(bandeja1);
                                    gestionPesaje.insertLocal(pesaje1);
                                    gestionPesaje.insertLocalSync(pesaje1);
                                }
                                if (cantidadBandejas >= 2) {
                                    Pesaje pesaje2 = pesaje;
                                    pesaje2.setQRenvase(bandeja2);
                                    gestionPesaje.insertLocal(pesaje2);
                                    gestionPesaje.insertLocalSync(pesaje2);
                                }
                                if (cantidadBandejas >= 3) {
                                    Pesaje pesaje3 = pesaje;
                                    pesaje3.setQRenvase(bandeja3);
                                    gestionPesaje.insertLocal(pesaje3);
                                    gestionPesaje.insertLocalSync(pesaje3);
                                }
                                if (cantidadBandejas >= 4) {
                                    Pesaje pesaje4 = pesaje;
                                    pesaje4.setQRenvase(bandeja4);
                                    gestionPesaje.insertLocal(pesaje4);
                                    gestionPesaje.insertLocalSync(pesaje4);
                                }
                                Toast.makeText(MainActivity.this, "Pesaje registrado", Toast.LENGTH_SHORT).show();
                                pop();
                                System.out.println(".........LISTA SYNC....." + gestionPesaje.selectLocalSync().toString());
                            }

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        } else {
            Toast.makeText(MainActivity.this, "Seleccione todos los campos!", Toast.LENGTH_SHORT).show();
        }
    }

    public void scan() {
        if (largo < 5) {
            qrIntent("pesaje", "Escanear códigos QR");
        }
    }

    public void consultarButton(View view) {
        qrIntent("consulta", "Consultar por código QR de Trabajador");
    }

    public void qrIntent(String tipo, String titulo){
        QR qr = QR.getInstance();
        qr.setTipoQR(tipo);
        IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.setPrompt(titulo);
        scanIntegrator.setBeepEnabled(false);
        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
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

    public void clear(View view) {
        txtTrabajador.setText("S/D");
        txtCajas.setText("S/D");
        bandeja1 = "";
        bandeja2 = "";
        bandeja3 = "";
        bandeja4 = "";
        largo = 0;
        lista.clear();
    }

    //SINCRONIZACION
    public void syncCompleta(View view) {
        sync.eventoSyncAll(view.getContext(), true);
    }

    public void syncPesaje(View view) {
        sync.eventoSyncPesaje(view.getContext(), false);
    }

    private final static int INTERVAL = 1000 * 60 * 10; //10 minutes
    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            sync.eventoSyncPesaje(context, false);
            System.out.println(".........SINCRONIZA PESAJE..........");
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startSyncAuto() {
        mHandlerTask.run();
    }

    void stopSyncAuto() {
        mHandler.removeCallbacks(mHandlerTask);
    }
}