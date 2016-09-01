package cl.lillo.prodarandanos.Modelo;

/**
 * Created by Usuario on 31/08/2016.
 */

public class Trabajador {
    private static Trabajador instance;

    //datos
    private String Rut;
    private String Nombre;
    private String Apellido;
    private String QRrut;

    //constructor
    public Trabajador() {
    }

    //getter setter
    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getQRrut() {
        return QRrut;
    }

    public void setQRrut(String QRrut) {
        this.QRrut = QRrut;
    }

    public String getRut() {
        return Rut;
    }

    public void setRut(String rut) {
        Rut = rut;
    }

    //auto-instancia (creo)
    public static synchronized Trabajador getInstance() {
        if (instance == null) {
            instance = new Trabajador();
        }
        return instance;
    }
}