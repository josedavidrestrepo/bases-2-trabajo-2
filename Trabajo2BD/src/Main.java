import java.util.ArrayList;

/**
 * Created by José David on 10/10/2015.
 */
public class Main {

    public static void main(String[] args) {
        try
        {
            Conexion c = new Conexion("jdbc:oracle:thin:@localhost:1521:xe","Bases_II","bases2015");

            for (HistorialVisitante h : c.getHistorialesVisitante())
            {
                for (Trayectoria t : h.getTrayectorias())
                {
                    ArrayList<Punto> puntos = t.getPuntos();
                    for (int i = 0; i < puntos.size(); i++)
                    {
                        System.out.println(h.getIdentificacion() + " "  + puntos.get(i).getX() + " " + puntos.get(i).getY());
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

}
