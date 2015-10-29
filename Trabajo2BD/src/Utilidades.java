import com.sun.javafx.geom.Line2D;

import java.awt.geom.Point2D;

/**
 * Created by José David on 29/10/2015.
 */
public class Utilidades {
    public static Point2D getLineLineIntersection(Line2D rectaA, Line2D rectaB) {

        double x1 = rectaA.x1;
        double y1 = rectaA.y1;
        double x2 = rectaA.x2;
        double y2 = rectaA.y2;
        double x3 = rectaB.x1;
        double y3 = rectaB.y1;
        double x4 = rectaB.x2;
        double y4 = rectaB.y2;

        double det1And2 = det(x1, y1, x2, y2);
        double det3And4 = det(x3, y3, x4, y4);
        double x1LessX2 = x1 - x2;
        double y1LessY2 = y1 - y2;
        double x3LessX4 = x3 - x4;
        double y3LessY4 = y3 - y4;
        double det1Less2And3Less4 = det(x1LessX2, y1LessY2, x3LessX4, y3LessY4);
        if (det1Less2And3Less4 == 0){
            // the denominator is zero so the lines are parallel and there's either no solution (or multiple solutions if the lines overlap) so return null.
            return null;
        }
        double x = (det(det1And2, x1LessX2,
                det3And4, x3LessX4) /
                det1Less2And3Less4);
        double y = (det(det1And2, y1LessY2,
                det3And4, y3LessY4) /
                det1Less2And3Less4);

        return new Point2D.Double(x,y);
    }

    protected static double det(double a, double b, double c, double d) {
        return a * d - b * c;
    }
}
