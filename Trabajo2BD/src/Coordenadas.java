import oracle.xdb.XMLType;
import org.w3c.dom.Document;

import java.sql.SQLException;

/**
 * Created by José David on 06/10/2015.
 */
public class Coordenadas {

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public Coordenadas(int x1, int y1, int y2, int x2) {
        this.x1 = x1;
        this.y1 = y1;
        this.y2 = y2;
        this.x2 = x2;
    }

    public Coordenadas(XMLType coordenadas) throws SQLException
    {
        Document d = coordenadas.getDocument();

        this.x1 = Integer.valueOf(d.getElementsByTagName("x1").item(0).getFirstChild().getNodeValue());
        this.y1 = Integer.valueOf(d.getElementsByTagName("y1").item(0).getFirstChild().getNodeValue());
        this.x2 = Integer.valueOf(d.getElementsByTagName("x2").item(0).getFirstChild().getNodeValue());
        this.y2 = Integer.valueOf(d.getElementsByTagName("y2").item(0).getFirstChild().getNodeValue());

    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}
