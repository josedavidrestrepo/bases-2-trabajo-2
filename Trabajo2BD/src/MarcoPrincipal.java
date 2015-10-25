import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Jos� David on 07/10/2015.
 */
public class MarcoPrincipal extends JFrame {

    Opcion opcion;

    public MarcoPrincipal(String title) throws HeadlessException {
        super(title);

        getContentPane().setLayout(new BorderLayout());

        add(new CentroComercial(), BorderLayout.CENTER);
        add(new PanelOpciones(), BorderLayout.EAST);

        pack();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new MarcoPrincipal("Centro Comercial");
    }

    public void setOpcion(Opcion opcion) {
        this.opcion = opcion;
        repaint();
    }

    private class CentroComercial extends JPanel
    {

        public CentroComercial()
        {
            setPreferredSize(new Dimension(640, 640));
            setMaximumSize(new Dimension(640, 640));
            setMinimumSize(new Dimension(640, 640));
        }

        @Override
        public void paint(Graphics g) {
            Conexion c;

            Dimension d = getSize();
            int width =  d.width;
            int height = d.height;

            int escalaX = (width - 50) / 100;
            int escalaY = (height - 50) / 100;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            try
            {
                c = new Conexion("jdbc:oracle:thin:@localhost:1521:xe","Bases_II","bases2015");
                for (Local l : c.getLocales())
                {
                    int x = (l.getCoordenadas().getX1()*escalaX)+25;
                    int y = (l.getCoordenadas().getY1()*escalaY)+25;
                    int w = (l.getCoordenadas().getX2()-l.getCoordenadas().getX1())*escalaX;
                    int h = (l.getCoordenadas().getY2()-l.getCoordenadas().getY1())*escalaY;
                    g.setColor(l.getColor());
                    g.fillRect(x, y, w, h);
                }

                g.setColor(Color.LIGHT_GRAY);
                for(int i = 25; i < height; i+=(height-50)/10)
                {
                    g.drawLine(0, i , width, i);
                }
                for(int j = 25; j < width; j+=(width-50)/10)
                {
                    g.drawLine(j, 0 , j, height);
                }

                String sql = null;

                if (opcion.getOpcion() == Opciones.TrayectoriasPorID)
                {
                    sql = "SELECT h.identificacion, t.column_value AS trayectorias " +
                            "FROM historial_visitante h, TABLE(h.trayectorias) t " +
                            "WHERE h.identificacion = " + opcion.getValor() +
                            " ORDER BY h.identificacion";
                }

                if (sql != null)
                {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3));
                    for (HistorialVisitante h : c.getHistorialesVisitante(sql))
                    {
                        for (Trayectoria t : h.getTrayectorias())
                        {
                            ArrayList<Punto> puntos = t.getPuntos();
                            for (int i = 1; i < puntos.size(); i++)
                            {
                                int x1 = (puntos.get(i-1).getX() * escalaX) + 25;
                                int y1 = (puntos.get(i-1).getY() * escalaY) + 25;
                                int x2 = (puntos.get(i).getX() * escalaX) + 25;
                                int y2 = (puntos.get(i).getY() * escalaY) + 25;
                                g2.setColor(h.getColor());
                                g2.drawLine(x1,y1,x2,y2);
                            }
                        }
                    }
                }
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(null,e.getMessage());
            }
            catch (NullPointerException e){ }

            g.setColor(Color.BLACK);
            for (int i = 10; i <= 100; i+=10)
            {
                g.drawString("" + i, i*escalaX - 5, 14);
                g.drawString("" + i, 3, i*escalaY);
            }
        }
    }

    private class PanelOpciones extends JPanel
    {
        JButton buscarPorIdentificacion;

        public PanelOpciones() {

            buscarPorIdentificacion = new JButton("Trayectorias por ID");

            buscarPorIdentificacion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String id = null;
                    try {
                        id = JOptionPane.showInputDialog(null,"Ingrese una identificación","Buscar por ID", JOptionPane.QUESTION_MESSAGE,null,null,null).toString();
                    } catch (NullPointerException ex) { }

                    setOpcion(new Opcion(Opciones.TrayectoriasPorID,id));

                }
            });

            add(buscarPorIdentificacion, BorderLayout.CENTER);

        }


    }



}
