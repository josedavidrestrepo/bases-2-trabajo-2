import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

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
        private ArrayList<Local> locales;

        public CentroComercial()
        {
            setPreferredSize(new Dimension(640, 640));
            setMaximumSize(new Dimension(640, 640));
            setMinimumSize(new Dimension(640, 640));

            try {
                Conexion c = new Conexion("jdbc:oracle:thin:@localhost:1521:xe","Bases_II","bases2015");
                locales = c.getLocales();
            } catch (SQLException e) {
                e.printStackTrace();
            }

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

                Font aux = getFont();
                g.setFont(new Font("default", Font.BOLD, 12));
                for (Local l : locales)
                {
                    int x = (l.getCoordenadas().getX1()*escalaX)+25;
                    int y = (l.getCoordenadas().getY1()*escalaY)+25;
                    int w = (l.getCoordenadas().getX2()-l.getCoordenadas().getX1())*escalaX;
                    int h = (l.getCoordenadas().getY2()-l.getCoordenadas().getY1())*escalaY;
                    g.setColor(l.getColor());
                    g.fillRect(x, y, w, h);
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(l.getNombre() + " " + l.getCategoria()),(x + w/2) - 10,(y + h/2) + 2);
                }
                g.setFont(aux);

                g.setColor(Color.LIGHT_GRAY);
                for(int i = 25; i < height; i+=(height-50)/10)
                {
                    g.drawLine(0, i , width, i);
                }
                for(int j = 25; j < width; j+=(width-50)/10)
                {
                    g.drawLine(j, 0 , j, height);
                }

                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3));

                if (opcion.getOpcion() == Opciones.TrayectoriasPorID)
                {
                    for (HistorialVisitante h : c.getHistorialesVisitante(opcion.getValor(),locales))
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

                                if (i == 1) g.drawString(String.format("%.2f", t.getCategoriaPromedio()), x1, y1);
                            }
                        }
                    }
                }

                if (opcion.getOpcion() == Opciones.MayorCategoriaPromedio)
                {
                    ArrayList<Trayectoria> trayectorias = new ArrayList<>();
                    for (HistorialVisitante h : c.getHistorialesVisitante(null,locales))
                    {
                        for (Trayectoria t : h.getTrayectorias())
                        {
                            trayectorias.add(t);
                        }
                    }
                    Collections.sort(trayectorias,Collections.reverseOrder());

                    int n = Integer.valueOf(opcion.getValor());

                    g2.setColor(Color.black);
                    for(int i = 0; i < n && i < trayectorias.size(); i++)
                    {
                        Trayectoria t = trayectorias.get(i);
                        ArrayList<Punto> puntos = t.getPuntos();
                        for (int j = 1; j < puntos.size(); j++)
                        {
                            int x1 = (puntos.get(j-1).getX() * escalaX) + 25;
                            int y1 = (puntos.get(j-1).getY() * escalaY) + 25;
                            int x2 = (puntos.get(j).getX() * escalaX) + 25;
                            int y2 = (puntos.get(j).getY() * escalaY) + 25;
                            g2.drawLine(x1,y1,x2,y2);

                            if (j == 1) g.drawString(String.format("%.2f", t.getCategoriaPromedio()), x1, y1);
                        }
                    }
                }

                if (opcion.getOpcion() == Opciones.ParejaDeTrayectorias)
                {
                    ArrayList<Trayectoria> trayectorias = new ArrayList<>();
                    for (HistorialVisitante h : c.getHistorialesVisitante(null,locales))
                    {
                        for (Trayectoria t : h.getTrayectorias())
                        {
                            trayectorias.add(t);
                        }
                    }

                    ArrayList<ParejaDeTrayectorias> parejasDeTrayectorias = new ArrayList<>();
                    for (int i = 0; i < trayectorias.size() - 1; i++)
                    {
                        for (int j = i+1; j < trayectorias.size(); j++)
                        {
                            parejasDeTrayectorias.add(new ParejaDeTrayectorias(trayectorias.get(i), trayectorias.get(j)));
                        }
                    }

                    ParejaDeTrayectorias p = Collections.max(parejasDeTrayectorias);

                    ArrayList<Punto> puntos = p.getT1().getPuntos();
                    for (int j = 1; j < puntos.size(); j++)
                    {
                        int x1 = (puntos.get(j-1).getX() * escalaX) + 25;
                        int y1 = (puntos.get(j-1).getY() * escalaY) + 25;
                        int x2 = (puntos.get(j).getX() * escalaX) + 25;
                        int y2 = (puntos.get(j).getY() * escalaY) + 25;
                        g2.drawLine(x1,y1,x2,y2);

                        if (j == 1) g.drawString(String.format("%.2f", p.getT1().getCategoriaPromedio()), x1, y1);
                    }

                    puntos = p.getT2().getPuntos();
                    for (int j = 1; j < puntos.size(); j++)
                    {
                        int x1 = (puntos.get(j-1).getX() * escalaX) + 25;
                        int y1 = (puntos.get(j-1).getY() * escalaY) + 25;
                        int x2 = (puntos.get(j).getX() * escalaX) + 25;
                        int y2 = (puntos.get(j).getY() * escalaY) + 25;
                        g2.drawLine(x1,y1,x2,y2);

                        if (j == 1) g.drawString(String.format("%.2f", p.getT2().getCategoriaPromedio()), x1, y1);
                    }
                }

                if (opcion.getOpcion() == Opciones.Problema)
                {

                }
            }
            catch (Exception e)
            {
                System.err.println(e);
            }

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
        JButton buscarMayorCategoriaPromedio;
        JButton buscarLocalesEnComun;
        JButton cuartoProblema;

        public PanelOpciones() {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            buscarPorIdentificacion = new JButton("Trayectorias por visitante");
            buscarPorIdentificacion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id = null;
                    try {
                        id = JOptionPane.showInputDialog(null,"Ingrese la identificación de un visitante","Trayectorias por visitante", JOptionPane.QUESTION_MESSAGE,null,null,null).toString();
                    } catch (NullPointerException ex) { }

                    setOpcion(new Opcion(Opciones.TrayectoriasPorID,id));
                }
            });
            add(buscarPorIdentificacion);

            buscarMayorCategoriaPromedio = new JButton("Trayectorias con mayor categoría promedio");
            buscarMayorCategoriaPromedio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String n = null;
                    try {
                        n = JOptionPane.showInputDialog(null,"Ingrese un número de trayectorias","Trayectorias con mayor categoría promedio", JOptionPane.QUESTION_MESSAGE,null,null,null).toString();
                    } catch (NullPointerException ex) { }
                    setOpcion(new Opcion(Opciones.MayorCategoriaPromedio,n));
                }
            });
            add(buscarMayorCategoriaPromedio);

            buscarLocalesEnComun = new JButton("Pareja de trayectorias con locales en común");
            buscarLocalesEnComun.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setOpcion(new Opcion(Opciones.ParejaDeTrayectorias,""));
                }
            });
            add(buscarLocalesEnComun);

            cuartoProblema = new JButton("Cuarto problema");
            cuartoProblema.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null,"Cuarto problema");
                    setOpcion(new Opcion(Opciones.Problema,""));
                }
            });
            add(cuartoProblema);

            pack();

        }

    }



}
