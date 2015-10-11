import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.xdb.XMLType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by José David on 06/10/2015.
 */
public class Conexion
{
    private Connection con;

    public Conexion(String url, String user, String pass) throws SQLException
    {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        con = DriverManager.getConnection(url,user,pass);
    }

    public ArrayList<Local> getLocales() throws SQLException
    {
        // Instancia el arraylist donde se almacenarán todos los locales
        ArrayList<Local> locales = new ArrayList<>();

        // Crea el objeto para ejecutar sentencias
        OraclePreparedStatement stmt = (OraclePreparedStatement) con.prepareStatement("SELECT id, nombre, categoria, tipo, coordenadas FROM local");

        // Obtiene todos los locales de la base de datos
        OracleResultSet rs = (OracleResultSet) stmt.executeQuery();

        // Recorre todo el resultSet de locales y cada local de la BD lo encuaderna en un objeto de tipo Local
        while (rs.next())
        {
            Coordenadas c = new Coordenadas((XMLType)(rs.getObject("coordenadas")));
            Local l = new Local(rs.getInt("id"),rs.getString("nombre"), rs.getInt("categoria"), rs.getString("tipo"), c);
            // Cada local lo va agregando al listado de locales
            locales.add(l);
        }

        // retorna la lista de locales de la base de datos mapeados.
        return locales;
    }

    public ArrayList<HistorialVisitante> getHistorialesVisitante() throws SQLException
    {
        ArrayList<HistorialVisitante> historiales = new ArrayList<>();

        // Crea el objeto para ejecutar sentencias
        OraclePreparedStatement stmt = (OraclePreparedStatement) con.prepareStatement("SELECT h.identificacion, t.column_value AS trayectorias FROM historial_visitante h, TABLE(h.trayectorias) t ORDER BY h.identificacion",
                ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

        OracleResultSet rs = (OracleResultSet) stmt.executeQuery();

        int contador = 0;
        int idActual = 0;
        ArrayList<Trayectoria> trayectorias = new ArrayList<>();
        while (rs.next())
        {
            if (contador == 0) idActual = rs.getInt("identificacion");

            XMLType XmlTrayectorias = (XMLType) rs.getObject("trayectorias");

            NodeList nodeListPuntos = XmlTrayectorias.getDocument().getElementsByTagName("punto");

            ArrayList<Punto> puntos = new ArrayList<>();
            for (int i = 0; i < nodeListPuntos.getLength(); i++)
            {
                int x = Integer.valueOf(nodeListPuntos.item(i).getChildNodes().item(0).getFirstChild().getNodeValue());
                int y = Integer.valueOf(nodeListPuntos.item(i).getChildNodes().item(1).getFirstChild().getNodeValue());
                Punto p = new Punto(x,y);
                puntos.add(p);
            }

            Trayectoria t = new Trayectoria(puntos);

            if (rs.getInt("identificacion") != idActual)
            {
                historiales.add(new HistorialVisitante(idActual,trayectorias));
                idActual = rs.getInt("identificacion");
                trayectorias = new ArrayList<>();
            }

            trayectorias.add(t);

            contador++;

            if (rs.isLast()) historiales.add(new HistorialVisitante(idActual,trayectorias));

        }

        return historiales;
    }
}
