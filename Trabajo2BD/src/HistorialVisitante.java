import java.awt.*;
import java.util.ArrayList;

/**
 * Created by José David on 09/10/2015.
 */
public class HistorialVisitante {

    private int identificacion;
    private ArrayList<Trayectoria> trayectorias;

    public HistorialVisitante(int identificacion, ArrayList<Trayectoria> trayectorias) {
        this.identificacion = identificacion;
        this.trayectorias = trayectorias;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public ArrayList<Trayectoria> getTrayectorias() {
        return trayectorias;
    }

    public Color getColor()
    {
        switch (identificacion)
        {
            case 1: return new Color(245,0,87);
            case 2: return new Color(0,0,0);
            case 3: return new Color(103,58,183);
            default: return new Color(66,66,66);
        }
    }
}
