import java.awt.*;

/**
 * Created by José David on 06/10/2015.
 */
public class Local {

    private int id;
    private String nombre;
    private int categoria;
    private String tipo;

    private Coordenadas coordenadas;

    public Local(int id, String nombre, int categoria, String tipo, Coordenadas coordenadas) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tipo = tipo;
        this.coordenadas = coordenadas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCategoria() {
        return categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public Color getColor()
    {
        switch (tipo)
        {
            case "T01" : return Color.YELLOW;
            case "B01" : return Color.GREEN;
            case "L01" : return Color.BLUE;
            case "R01" : return Color.ORANGE;
            case "T02" : return Color.RED;
            default: return null;
        }
    }

}
