import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Jos� David on 07/10/2015.
 */
public class CentroComercial extends JFrame {

    Conexion c;

    public CentroComercial(String title) throws HeadlessException {
        super(title);
        setSize(650, 675);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = getSize();
        int width =  d.width;
        int height = d.height;

        int escalaX = (width - 50) / 100;
        int escalaY = (height - 75) / 100;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        try
        {
            c = new Conexion("jdbc:oracle:thin:@localhost:1521:xe","Bases_II","bases2015");
            for (Local l : c.getLocales())
            {
                int x = (l.getCoordenadas().getX1()*escalaX)+25;
                int y = (l.getCoordenadas().getY1()*escalaY)+50;
                int w = (l.getCoordenadas().getX2()-l.getCoordenadas().getX1())*escalaX;
                int h = (l.getCoordenadas().getY2()-l.getCoordenadas().getY1())*escalaY;
                g.setColor(l.getColor());
                g.fillRect(x, y, w, h);
            }

            g.setColor(Color.LIGHT_GRAY);
            for(int i = 50; i < height; i+=(height-75)/10)
            {
                g.drawLine(0, i , width, i);
            }
            for(int j = 25; j < width; j+=(width-50)/10)
            {
                g.drawLine(j, 0 , j, height);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            for (HistorialVisitante h : c.getHistorialesVisitante())
            {
                for (Trayectoria t : h.getTrayectorias())
                {
                    ArrayList<Punto> puntos = t.getPuntos();
                    for (int i = 1; i < puntos.size(); i++)
                    {
                        int x1 = (puntos.get(i-1).getX() * escalaX) + 25;
                        int y1 = (puntos.get(i-1).getY() * escalaY) + 50;
                        int x2 = (puntos.get(i).getX() * escalaX) + 25;
                        int y2 = (puntos.get(i).getY() * escalaY) + 50;
                        g2.setColor(h.getColor());
                        g2.drawLine(x1,y1,x2,y2);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println(e);
        }

        for (int i = 10; i <= 100; i+=10)
        {
            g.drawString("" + i, i*escalaX, 45);
            g.drawString("" + i, 3, i*escalaY + 45);
        }
    }

    public static void main(String[] args) {
        new CentroComercial("Centro Comercial");
    }

}
