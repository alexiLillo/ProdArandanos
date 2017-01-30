package cl.lillo.prodarandanos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cl.lillo.prodarandanos.Controlador.*;
import cl.lillo.prodarandanos.Modelo.*;
import cl.lillo.prodarandanos.Otros.*;

public class MainActivity extends Activity {

    NumberFormat formatter = new DecimalFormat("#0.0000");
    NumberFormat formatter2 = new DecimalFormat("#0");

    private String fundo = "";
    private String potrero = "";
    private String sector = "";
    private String cuartel = "";
    private String variedad = "";
    private String pesador = "";

    private TextView txtKL, txtTrabajador, txtCajas, txtTrabajadorConsulta, txtBandejasDia, txtKilosDia, txtBandejasTotal, txtKilosTotal, txtLastSync, txtLastSyncCompleta, txtLastSyncPesajes, txtCountBandejas;

    private EditText txtRut;

    private Spinner spinFundo, spinPotrero, spinSector, spinVariedad, spinCuartel, spinTara;
    String scanContent;
    String scanFormat;

    private ArrayList<String> lista = new ArrayList<>();
    private Object[] listaFinal = new Object[0];
    private int largo;
    private String bandeja1, bandeja2, bandeja3, bandeja4;
    private int cantidadBandejas = 0;
    private int contadorTemporalSync = 0;

    //Gestiones
    private Sync sync;
    private GestionTablaVista gestionTablaVista;
    private GestionPesaje gestionPesaje;
    private GestionTara gestionTara;
    private GestionTrabajador gestionTrabajador;
    private GestionQRSdia gestionQRSdia;
    private Bluetooth bluetooth;
    /**
     * Called when the activity is first created.
     */
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = this.getIntent().getExtras();
        pesador = bundle.getString("pesador");
        //MOMENTANEO PARA NO LOGEAR
        //pesador = "00000000-0";


        bluetooth = new Bluetooth(this);
        bluetooth.onCreate();

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
        txtBandejasDia = (TextView) findViewById(R.id.txtBandejasDia);
        txtKilosDia = (TextView) findViewById(R.id.txtKilosDia);
        txtBandejasTotal = (TextView) findViewById(R.id.txtBandejasTotal);
        txtKilosTotal = (TextView) findViewById(R.id.txtKilosTotal);
        txtLastSync = (TextView) findViewById(R.id.txtLastSync);
        txtLastSyncCompleta = (TextView) findViewById(R.id.txtLastSyncCompleta);
        txtLastSyncPesajes = (TextView) findViewById(R.id.txtLastSyncPesajes);
        txtCountBandejas = (TextView) findViewById(R.id.txtCountBandejas);

        gestionTablaVista = new GestionTablaVista(this);
        gestionPesaje = new GestionPesaje(this);
        gestionTara = new GestionTara(this);
        gestionTrabajador = new GestionTrabajador(this);
        gestionQRSdia = new GestionQRSdia(this);
        sync = new Sync();

        txtCountBandejas.setText(String.valueOf(gestionPesaje.cantBandejas(pesador)));

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
        //deshabilitar un tab (tab de tarjetas)
        tabs.getTabWidget().getChildTabViewAt(1).setEnabled(false);

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
        System.out.println(gestionTablaVista.selectFundo().toString());
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
                    //sin cuartel
                    //cuartel = "0";
                    //con cuartel
                    cuartel = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapterTara = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gestionTara.selectTaraSpinner());
        spinTara.setAdapter(adapterTara);

        //sincronizacion automatica
        //startSyncAuto();
        gestionPesaje.deleteLocalOld();
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetooth.onResume();
        System.out.println("........................onResume Main............................");
    }

    @Override
    public void onPause() {
        super.onPause();
        //bluetooth.onPause();
        System.out.println("........................onPause Main............................");
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
        if (txtTrabajador.getText().equals("S/D"))
            scanPesaje("Escanear código de Trabajador");
        else
            scanPesaje("Escanear Bandejas");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents();
                scanFormat = scanningResult.getFormatName();
            }

            QR qr = QR.getInstance();

            if (qr.getTipoQR().equals("pesaje")) {

                if (scanContent != null) {
                    lista.add(scanContent);
                    Set<String> lista2 = new HashSet<>(lista);
                    listaFinal = lista2.toArray();

                    if (largo < listaFinal.length) {
                        //ok();
                        largo = listaFinal.length;
                        if (largo == 1) {
                            if (gestionTrabajador.existe(scanContent) && !scanContent.equals(pesador)) {
                                // if (gestionPesaje.puedePesar(scanContent)) {
                                txtTrabajador.setText(scanContent);
                                Toast.makeText(this, "Trabajador: " + scanContent, Toast.LENGTH_SHORT).show();
                                cantidadBandejas = 0;
                                scanPesaje("Escanear primera bandeja");
                                ok();

                                // } else {
                                //   Toast.makeText(this, "Trabajador ya registró pesaje, vuelva a intentarlo mas tarde", Toast.LENGTH_SHORT).show();
                                // lista.clear();
                                //largo = 0;
                                //cantidadBandejas = 0;
                                // scanPesaje();
                                // error();
                                //}
                            } else {
                                Toast.makeText(this, "Código de trabajador incorrecto!", Toast.LENGTH_SHORT).show();
                                lista.clear();
                                largo = 0;
                                cantidadBandejas = 0;
                                scanPesaje("Escanear código de Trabajador");
                                error();
                            }
                        }
                        if (largo == 2) {
                            if (scanContent.startsWith("ENV")) {
                                if (!gestionQRSdia.existeLocal(scanContent)) {
                                    bandeja1 = scanContent;
                                    Toast.makeText(this, "Primera bandeja: " + bandeja1, Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 1;
                                    txtCajas.setText("1");
                                    scanPesaje("Escanear segunda bandeja");
                                    ok();
                                } else {
                                    Toast.makeText(this, "Código de bandeja ya leído!", Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 0;
                                    largo -= 1;
                                    lista.remove(scanContent);
                                    scanPesaje("Escanear primera bandeja");
                                    error();
                                }
                            } else {
                                Toast.makeText(this, "Código de bandeja inválido!", Toast.LENGTH_SHORT).show();
                                cantidadBandejas = 0;
                                largo -= 1;
                                lista.remove(scanContent);
                                scanPesaje("Escanear primera bandeja");
                                error();
                            }
                        }
                        if (largo == 3) {
                            if (scanContent.startsWith("ENV")) {
                                if (!gestionQRSdia.existeLocal(scanContent)) {
                                    bandeja2 = scanContent;
                                    Toast.makeText(this, "Segunda bandeja: " + bandeja2, Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 2;
                                    txtCajas.setText("2");
                                    scanPesaje("Escanear tercera bandeja");
                                    ok();
                                } else {
                                    Toast.makeText(this, "Código de bandeja ya leído!", Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 1;
                                    largo -= 1;
                                    lista.remove(scanContent);
                                    scanPesaje("Escanear segunda bandeja");
                                    error();
                                }
                            } else {
                                Toast.makeText(this, "Código de bandeja inválido!", Toast.LENGTH_SHORT).show();
                                cantidadBandejas = 1;
                                largo -= 1;
                                lista.remove(scanContent);
                                scanPesaje("Escanear segunda bandeja");
                                error();
                            }

                        }
                        if (largo == 4) {
                            if (scanContent.startsWith("ENV")) {
                                if (!gestionQRSdia.existeLocal(scanContent)) {
                                    bandeja3 = scanContent;
                                    Toast.makeText(this, "Tercera bandeja: " + bandeja3, Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 3;
                                    txtCajas.setText("3");
                                    scanPesaje("Escanear cuarta bandeja");
                                    ok();
                                } else {
                                    Toast.makeText(this, "Código de bandeja ya leído!", Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 2;
                                    largo -= 1;
                                    lista.remove(scanContent);
                                    scanPesaje("Escanear tercera bandeja");
                                    error();
                                }
                            } else {
                                Toast.makeText(this, "Código de bandeja inválido!", Toast.LENGTH_SHORT).show();
                                cantidadBandejas = 2;
                                largo -= 1;
                                lista.remove(scanContent);
                                scanPesaje("Escanear tercera bandeja");
                                error();
                            }
                        }
                        if (largo == 5) {
                            if (scanContent.startsWith("ENV")) {
                                if (!gestionQRSdia.existeLocal(scanContent)) {
                                    bandeja4 = scanContent;
                                    Toast.makeText(this, "Cuarta bandeja: " + bandeja4, Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 4;
                                    txtCajas.setText("4");
                                    ok();
                                } else {
                                    Toast.makeText(this, "Código de bandeja ya leído!", Toast.LENGTH_SHORT).show();
                                    cantidadBandejas = 3;
                                    largo -= 1;
                                    lista.remove(scanContent);
                                    scanPesaje("Escanear cuarta bandeja");
                                    error();
                                }
                            } else {
                                Toast.makeText(this, "Código de bandeja inválido!", Toast.LENGTH_SHORT).show();
                                cantidadBandejas = 3;
                                largo -= 1;
                                lista.remove(scanContent);
                                scanPesaje("Escanear cuarta bandeja");
                                error();
                            }
                        }

                    } else {
                        if (largo < 5) {
                            Toast.makeText(this, "Código QR ya leido!", Toast.LENGTH_SHORT).show();
                            scanPesaje("Intente con un código diferente");
                            error();
                        }
                    }
                }
            } else {
                if (qr.getTipoQR().equals("consulta")) {
                    //comprobar si existe (ya no, por si existe en server pero no local)
                    //if (gestionTrabajador.existe(scanContent)) {
                    //consulta al server
                    String[] splitHistorico = gestionTrabajador.resumenHistorico(scanContent, gestionTablaVista.lastMapeo()).split("-");
                    String[] splitDia = gestionTrabajador.resumenDia(scanContent, gestionTablaVista.lastMapeo()).split("-");
                    txtTrabajadorConsulta.setText(splitHistorico[0]);
                    if (!splitDia[0].equals("S/D")) {
                        txtBandejasDia.setText(formatter2.format(Double.parseDouble(splitDia[0])));
                    } else txtBandejasDia.setText(splitDia[0]);
                    if (!splitDia[1].equals("S/D")) {
                        txtKilosDia.setText(formatter.format(Double.parseDouble(splitDia[1])));
                    } else txtKilosDia.setText(splitDia[1]);
                    if (!splitHistorico[1].equals("S/D")) {
                        txtBandejasTotal.setText(formatter2.format(Double.parseDouble(splitHistorico[1])));
                    } else txtBandejasTotal.setText(splitHistorico[1]);
                    if (!splitHistorico[2].equals("S/D")) {
                        txtKilosTotal.setText(formatter.format(Double.parseDouble(splitHistorico[2])));
                    } else txtKilosTotal.setText(splitHistorico[2]);
                    pop();
                } else {
                    Toast.makeText(this, "Trabajador no registrado!", Toast.LENGTH_SHORT).show();
                }
                //}
            }
            scanContent = null;
        } else {
            Toast.makeText(this, "No se escanearon datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertPesaje(final View view) {
        if (!fundo.equals("") && !potrero.equals("") && !sector.equals("") && !variedad.equals("") && !cuartel.equals("") && cantidadBandejas != 0 && !txtKL.getText().toString().equals("S/D") && !txtTrabajador.getText().toString().equals("S/D") && !txtCajas.getText().toString().equals("S/D")) {
            final String id_tara = spinTara.getSelectedItem().toString().substring(0, 2).replace(" ", "");
            Tara tara = gestionTara.selectLocal(id_tara);
            final Pesaje pesaje = new Pesaje();
            pesaje.setProducto(tara.getProducto());
            //pesaje.setQRenvase();
            pesaje.setCuadrilla("-");
            pesaje.setRutTrabajador(txtTrabajador.getText().toString());
            //rut pesador se debe escanear al iniciar
            pesaje.setRutPesador(pesador);
            pesaje.setFundo(fundo);
            pesaje.setPotrero(potrero);
            pesaje.setSector(sector);
            pesaje.setVariedad(variedad);
            pesaje.setClase("-");
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
            String hora = "" + hour;
            int min = c.get(Calendar.MINUTE);
            String minu = "" + min;
            if (hour < 10)
                hora = "0" + hour;
            if (min < 10)
                minu = "0" + min;
            String horario = hora + ":" + minu;

            pesaje.setFechaHora(fecha + " " + horario);
            // PESO SE DEBE RESTAR TARA (PESO NETO DE UNA SOLA BANDEJA)
            pesaje.setPesoNeto(Double.parseDouble(formatter.format((Double.parseDouble(txtKL.getText().toString()) / cantidadBandejas) - tara.getPeso())));
            pesaje.setTara(tara.getPeso());
            pesaje.setFormato(tara.getFormato());
            pesaje.setTotalCantidad(1);
            pesaje.setFactor(1);
            pesaje.setCantidad(1);
            pesaje.setLectura_SVAL("");
            pesaje.setID_Map(gestionTablaVista.lastMapeo());
            pesaje.setTipoRegistro("CELULAR");
            pesaje.setFechaHoraModificacion("-");
            pesaje.setUsuarioModificaion(getImei(getApplicationContext()));

            if (pesaje.getPesoNeto() <= 0 || pesaje.getPesoNeto() > (3.5 - tara.getPeso())) {
                new AlertDialog.Builder(this)
                        .setTitle("Pesaje erróneo!")
                        .setMessage("El peso parece ser incorrecto\nVuelva a verificar bandejas")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("¿Guardar pesaje?")
                        .setCancelable(true)
                        .setMessage("Trabajador: " + txtTrabajador.getText().toString() + "\nBandejas: " + cantidadBandejas + "\nPeso Bruto: " + txtKL.getText().toString() + "\nPeso Neto: " + String.valueOf(formatter.format(Double.parseDouble(txtKL.getText().toString()) - (tara.getPeso() * cantidadBandejas))))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean is = false;
                                if (cantidadBandejas == 0) {
                                    Toast.makeText(MainActivity.this, "No se escanearon bandejas!", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (cantidadBandejas >= 1) {
                                        if (!gestionQRSdia.existeLocal(bandeja1)) {
                                            Pesaje pesaje1 = pesaje;
                                            pesaje1.setQRenvase(bandeja1);
                                            gestionPesaje.insertLocal(pesaje1);
                                            gestionPesaje.insertLocalSync(pesaje1);
                                            gestionQRSdia.insertarLocal(bandeja1);
                                            is = true;
                                        } else {
                                            is = false;
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Error!")
                                                    .setMessage("Bandeja n°1, código: " + bandeja1 + "\nYa se registró anteriormente!\nNo se agregará dicha bandeja al sistema.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                        }
                                    }
                                    if (cantidadBandejas >= 2) {
                                        if (!gestionQRSdia.existeLocal(bandeja2)) {
                                            Pesaje pesaje2 = pesaje;
                                            pesaje2.setQRenvase(bandeja2);
                                            gestionPesaje.insertLocal(pesaje2);
                                            gestionPesaje.insertLocalSync(pesaje2);
                                            gestionQRSdia.insertarLocal(bandeja2);
                                            is = true;
                                        } else {
                                            is = false;
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Error!")
                                                    .setMessage("Bandeja n°2, código: " + bandeja2 + "\nYa se registró anteriormente!\nNo se agregará dicha bandeja al sistema.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                        }
                                    }
                                    if (cantidadBandejas >= 3) {
                                        if (!gestionQRSdia.existeLocal(bandeja3)) {
                                            Pesaje pesaje3 = pesaje;
                                            pesaje3.setQRenvase(bandeja3);
                                            gestionPesaje.insertLocal(pesaje3);
                                            gestionPesaje.insertLocalSync(pesaje3);
                                            gestionQRSdia.insertarLocal(bandeja3);
                                            is = true;
                                        } else {
                                            is = false;
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Error!")
                                                    .setMessage("Bandeja n°3, código: " + bandeja3 + "\nYa se registró anteriormente!\nNo se agregará dicha bandeja al sistema.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                        }
                                    }
                                    if (cantidadBandejas >= 4) {
                                        if (!gestionQRSdia.existeLocal(bandeja4)) {
                                            Pesaje pesaje4 = pesaje;
                                            pesaje4.setQRenvase(bandeja4);
                                            gestionPesaje.insertLocal(pesaje4);
                                            gestionPesaje.insertLocalSync(pesaje4);
                                            gestionQRSdia.insertarLocal(bandeja4);
                                            is = true;
                                        } else {
                                            is = false;
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Error!")
                                                    .setMessage("Bandeja n°4, código: " + bandeja4 + "\nYa se registró anteriormente!\nNo se agregará dicha bandeja al sistema.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                        }
                                    }
                                    if (is)
                                        Toast.makeText(MainActivity.this, "Pesaje registrado", Toast.LENGTH_SHORT).show();
                                    txtCountBandejas.setText(String.valueOf(gestionPesaje.cantBandejas(pesador)));
                                    contadorTemporalSync = contadorTemporalSync + cantidadBandejas;
                                    if (contadorTemporalSync >= 200 && contadorTemporalSync < 250) {
                                        txtCountBandejas.setTextColor(Color.YELLOW);
                                        Toast toast = Toast.makeText(MainActivity.this, "Se sugiere sincronizar pesajes", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 0);
                                        toast.show();
                                    }
                                    if (contadorTemporalSync >= 250) {
                                        txtCountBandejas.setTextColor(Color.RED);
                                        Toast toast = Toast.makeText(MainActivity.this, "SINCRONIZAR PESAJES LO ANTES POSIBLE", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 0);
                                        toast.show();
                                    }
                                    limpiar();
                                    pop();
                                    cantidadBandejas = 0;
                                    //System.out.println(".........LISTA SYNC....." + gestionPesaje.selectLocalSync().toString());
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Atención!")
                    .setMessage("Complete todos los campos antes de ingresar pesaje")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }

    public void scanPesaje(String title) {
        if (largo < 5) {
            qrIntent("pesaje", title.toUpperCase());
        }
    }

    public void scanConsulta(View view) {
        if (sync.conectado(this)) {
            qrIntent("consulta", "Consultar por código QR de Trabajador");
        } else {
            Toast.makeText(context, "No hay conexión a Internet, imposible realizar consulta", Toast.LENGTH_LONG).show();
        }
    }

    //public void scanPesador() {
    //    qrIntent("pesador", "Escanear código de Pesador");
    //}

    public void qrIntent(String tipo, String titulo) {
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
        mp.setVolume(50, 50);
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
        if (!txtTrabajador.getText().equals("S/D") || !txtCajas.getText().equals("S/D")) {
            new AlertDialog.Builder(this)
                    .setTitle("Datos pendientes")
                    .setMessage("¿Está seguro de limpiar datos que aún no se registran en el sistema?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            limpiar();
                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        } else {
            limpiar();
        }
    }

    public void limpiar() {
        txtTrabajador.setText("S/D");
        txtCajas.setText("S/D");
        cantidadBandejas = 0;
        bandeja1 = "";
        bandeja2 = "";
        bandeja3 = "";
        bandeja4 = "";
        largo = 0;
        lista.clear();
    }

    //FECHA ACTUAL
    public String getHoraActual() {
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

        return hora + ":" + minu;
    }

    //SINCRONIZACION
    public void syncCompleta(View view) {
        if (sync.eventoSyncAll(view.getContext(), true)) {
            contadorTemporalSync = 0;
            txtLastSync.setText(getHoraActual());
            txtLastSyncCompleta.setText(getHoraActual());
        }

        ArrayAdapter adapterTara = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gestionTara.selectTaraSpinner());
        spinTara.setAdapter(adapterTara);

        ArrayAdapter adapterF = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gestionTablaVista.selectFundo());
        spinFundo.setAdapter(adapterF);
    }

    public void syncPesaje(View view) {
        if (sync.eventoSyncPesaje(view.getContext(), false)) {
            contadorTemporalSync = 0;
            txtLastSync.setText(getHoraActual());
            txtLastSyncPesajes.setText(getHoraActual());
        }
    }

    private final static int INTERVAL = 1000 * 60 * 10; //  1000 * 60 * 10 = 10 minutes
    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            bluetooth.onResume();
            if (sync.eventoSyncPesaje(context, false))
                txtLastSync.setText(getHoraActual());
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

    //doble back para salir
    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Vuelva a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sync.eventoSyncPesaje(context, false);
    }

    public static String getImei(Context c) {
        TelephonyManager telephonyManager = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

}