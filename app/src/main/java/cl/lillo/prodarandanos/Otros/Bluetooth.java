package cl.lillo.prodarandanos.Otros;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cl.lillo.prodarandanos.MainActivity;
import cl.lillo.prodarandanos.R;

/**
 * Created by Usuario on 05/09/2016.
 */
public class Bluetooth {
    protected MainActivity context;

    public Bluetooth(Context context) {
        this.context = (MainActivity) context;
    }

    private void updateTV(final String str1) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) context.findViewById(R.id.txtPesoKL);
                textView.setText(str1);
            }
        });
    }

    private static final String TAG = "bluetooth";

    private Handler h;

    private final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private static BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // MAC-address of Bluetooth module (you must edit this line)
    //private static String address = "98:D3:31:30:69:E9";  //pesa 26
    //private static String address = "98:D3:31:20:73:87";  //pesa 17
    private static String address;

    private String neg = "";
    private String txtKL;

    public void onCreate() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();        // if device does not support Bluetooth
        checkBTState();

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String str;
                        String[] cadena;
                        str = Arrays.toString(readBuf);  // unica manera
                        cadena = str.split(",");
                        try {
                            int coma;
                            int peso;
                            int cienLibras;
                            int num = Integer.parseInt(cadena[0].replace(" ", "").replace("[", ""));
                            int num2 = Integer.parseInt(cadena[1].replace(" ", "").replace("[", ""));
                            if (num == 35) {
                                neg = "-";
                            }
                            if (num == 99) {
                                neg = "-";
                            }
                            if (num == 67) {
                                neg = "";
                            }
                            if (num == 3) {
                                neg = "";
                            }
                            if (num == -1 && num2 != 67 && num2 != 3 && num2 != 35 && num2 != 99) {
                                coma = converter(Integer.parseInt(cadena[1].replace(" ", "")));
                                peso = converter(Integer.parseInt(cadena[2].replace(" ", "")));
                                cienLibras = Integer.parseInt(cadena[3].replace(" ", ""));
                                if (cienLibras > 0) {
                                    peso += (cienLibras * 100);
                                }
                                if (coma < 10) {
                                    txtKL = neg + peso + ".0" + coma;
                                } else {
                                    txtKL = neg + peso + "." + coma;
                                }
                            } else if (num == 67 || num == 3 || num == 99 || num == 35) {
                                coma = converter(Integer.parseInt(cadena[1].replace(" ", "")));
                                peso = converter(Integer.parseInt(cadena[2].replace(" ", "")));
                                cienLibras = Integer.parseInt(cadena[3].replace(" ", ""));
                                if (cienLibras > 0) {
                                    peso += (cienLibras * 100);
                                }
                                if (coma < 10) {
                                    txtKL = neg + peso + ".0" + coma;
                                } else {
                                    txtKL = neg + peso + "." + coma;
                                }
                            }
                            //System.out.println("Array: " + str);
                            //System.out.println(txtKL);
                            updateTV(txtKL);
                        } catch (Exception e) {
                            Log.w(TAG, "...Error en Handler Bluetooth: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    public void seleccionarMac(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Seleccionar Pesa");
        alert.setMessage("Seleccione la pesa a utilizar, asegurese de que la pesa esté vinculada.");
        final Spinner listaPesas = new Spinner(context);
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Pesa 17");
        spinnerArray.add("Pesa 26");
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, spinnerArray);
        listaPesas.setAdapter(adapter);
        alert.setView(listaPesas);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pesa = listaPesas.getSelectedItem().toString();
                if (pesa.equals("Pesa 17"))
                    address = "98:D3:31:20:73:87";
                if (pesa.equals("Pesa 26"))
                    address = "98:D3:31:30:69:E9";
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }


    //retorna mac de dispositivo por el nombre.
    private static String getBluetoothMacAddress() {

        if (btAdapter == null) {
            Log.d(TAG, "device does not support bluetooth");
            return null;
        }

        String mac = "";
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            // here you get the mac using device.getAddress()
            if (device.getName().startsWith("HUBCROP")) {
                mac = device.getAddress();
            }
        }
        return mac;
    }

    private int converter(int libra) {
        if (libra >= 16 && libra <= 25) {
            libra -= 6;
        }
        if (libra >= 32 && libra <= 41) {
            libra -= 12;
        }
        if (libra >= 48 && libra <= 63) {
            libra -= 18;
        }
        if (libra >= 64 && libra <= 73) {
            libra -= 24;
        }
        if (libra >= 80 && libra <= 89) {
            libra -= 30;
        }
        if (libra >= 96 && libra <= 105) {
            libra -= 36;
        }
        if (libra >= 112 && libra <= 121) {
            libra -= 42;
        }
        if (libra >= -128 && libra <= -119) {
            libra += 208;
        }
        if (libra >= -112 && libra <= -103) {
            libra += 202;
        }
        return libra;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                //final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void onResume() {
        if (checkBTState()) {
            try {
                Log.d(TAG, "...onResume - try connect...");

                // Set up a pointer to the remote node using it's address.
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                // Two things are needed to make a connection:
                //   A MAC address, which we got above.
                //   A Service ID or UUID.  In this case we are using the
                //     UUID for SPP.

                try {
                    btSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    //errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                }

                // Discovery is resource intensive.  Make sure it isn't going on
                // when you attempt to connect and pass your message.
                btAdapter.cancelDiscovery();

                // Establish the connection.  This will block until it connects.
                Log.d(TAG, "...Connecting...");
                try {
                    btSocket.connect();
                    Log.d(TAG, "....Connection ok...");
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    }
                }

                // Create a data stream so we can talk to server.
                Log.d(TAG, "...Create Socket...");

                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            } catch (RuntimeException ex) {
                System.out.println(".................onResume RUNTIMEEX...............");
            }
        }
    }

    public void onPause() {
        try {
            Log.d(TAG, "...In onPause()...");

            try {
                btSocket.close();
            } catch (IOException e2) {
                //errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
            }
        } catch (NullPointerException ex) {
            System.out.println("...............onPause NULLPOINTEREX.................");
        }
    }

    private boolean checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            //errorExit("Fatal Error", "Bluetooth not support");
            return false;
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                address = getBluetoothMacAddress();
                return true;
            } else {
                //Prompt user to turn on Bluetooth
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, 1);
                btAdapter.enable();
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                address = getBluetoothMacAddress();
                Log.d(TAG, "...Encenciendo Bluetooth...");
                return true;
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.w(TAG, "IOException en connectedthread: " + e.toString());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
