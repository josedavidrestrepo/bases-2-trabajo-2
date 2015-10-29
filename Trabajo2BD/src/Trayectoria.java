import com.sun.javafx.geom.Line2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by José David on 09/10/2015.
 */
public class Trayectoria {

    private ArrayList<Punto> puntos;
    private Double categoriaPromedio;

    public Trayectoria(ArrayList<Punto> puntos, ArrayList<Local> locales) {
        this.puntos = puntos;

        double totalCategorias = 0;
        double totalAtravesados = 0;

        for(Local l : locales)
        {
            Coordenadas c = l.getCoordenadas();
            Line2D bordeSuperior = new Line2D(c.getX1(),c.getY1(),c.getX2(),c.getY1());
            Line2D bordeIzquierdo = new Line2D(c.getX1(),c.getY1(),c.getX1(),c.getY2());
            Line2D bordeInferior = new Line2D(c.getX1(),c.getY2(),c.getX2(),c.getY2());
            Line2D bordeDerecho = new Line2D(c.getX2(),c.getY1(),c.getX2(),c.getY2());

            int intersecciones = 0;

            for (int i = 1; i < puntos.size(); i++)
            {
                Line2D segmento = new Line2D(puntos.get(i-1).getX(),puntos.get(i-1).getY(),puntos.get(i).getX(),puntos.get(i).getY());
                double pendiente;
                try {
                    pendiente = (segmento.y2 - segmento.y1) / (segmento.x2 - segmento.x1);
                } catch (ArithmeticException e) {
                    pendiente = Double.POSITIVE_INFINITY;
                }

                if (pendiente == 0)
                {
                    if (segmento.intersectsLine(bordeSuperior) || segmento.intersectsLine(bordeInferior)) continue;
                }
                if (pendiente == Double.POSITIVE_INFINITY)
                {
                    if (segmento.intersectsLine(bordeIzquierdo) || segmento.intersectsLine(bordeDerecho)) continue;
                }

                ArrayList<Point2D> points = new ArrayList<>();

                if (segmento.intersectsLine(bordeSuperior))
                {
                    Point2D p = Utilidades.getLineLineIntersection(segmento, bordeSuperior);
                    if (!points.contains(p))
                    {
                        points.add(p);
                    }
                }
                if (segmento.intersectsLine(bordeIzquierdo))
                {
                    Point2D p = Utilidades.getLineLineIntersection(segmento, bordeIzquierdo);
                    if (!points.contains(p))
                    {
                        points.add(p);
                    }

                }
                if (segmento.intersectsLine(bordeInferior))
                {
                    Point2D p = Utilidades.getLineLineIntersection(segmento, bordeInferior);
                    if (!points.contains(p))
                    {
                        points.add(p);
                    }
                }
                if (segmento.intersectsLine(bordeDerecho))
                {
                    Point2D p = Utilidades.getLineLineIntersection(segmento, bordeDerecho);
                    if (!points.contains(p))
                    {
                        points.add(p);
                    }
                }
                intersecciones += points.size();

                if (intersecciones >= 2)
                {
                    totalAtravesados++;
                    totalCategorias += l.getCategoria();
                    break;
                }
            }
        }

        categoriaPromedio = totalAtravesados != 0 ? totalCategorias / totalAtravesados : 0.0;
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public Double getCategoriaPromedio() {
        return categoriaPromedio;
    }
}
