package cl.lillo.prodarandanos.Modelo;

/**
 * Created by Usuario on 31/08/2016.
 */
public class Tara {
    private static Tara instance;

    //datos
    private String ID_Tara;
    private double Peso;
    private String Producto;
    private String Formato;
    private String Descripcion;

    //constructor
    public Tara() {
    }

    //getter setter
    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFormato() {
        return Formato;
    }

    public void setFormato(String formato) {
        Formato = formato;
    }

    public String getID_Tara() {
        return ID_Tara;
    }

    public void setID_Tara(String ID_Tara) {
        this.ID_Tara = ID_Tara;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    //auto-instancia (creo)
    public static synchronized Tara getInstance() {
        if (instance == null) {
            instance = new Tara();
        }
        return instance;
    }
}
