package cl.lillo.prodarandanos;

/**
 * Created by Alexi on 06/07/2016.
 */
public class QR {
    private static QR instance;

    // Global variable
    private String rut = "S/D";
    private int bandejas = 0;

    // Restrict the constructor from being instantiated
    private QR() {
    }

    public void setRut(String r) {
        this.rut = r;
    }

    public String getRut() {
        return this.rut;
    }

    public int getBandejas() {
        return this.bandejas;
    }

    public void setBandejas(int bandejas) {
        this.bandejas = bandejas;
    }

    public static synchronized QR getInstance() {
        if (instance == null) {
            instance = new QR();
        }
        return instance;
    }
}
