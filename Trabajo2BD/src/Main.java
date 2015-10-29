import com.sun.javafx.geom.Line2D;
import javafx.geometry.Rectangle2D;

import java.awt.geom.Point2D;

/**
 * Created by José David on 28/10/2015.
 */
public class Main {
    public static void main(String[] args) {
        Rectangle2D rect = new Rectangle2D(20,20,20,20);
        //System.out.println(rect.contains(41, 41));

        Line2D line1 = new Line2D(20,10,20,20);
        Line2D line2 = new Line2D(50,0,90,90);

        if(line1.intersectsLine(line2))
        {
            Point2D p = getLineLineIntersection(line1.x1,line1.y1,line1.x2,line1.y2,line2.x1,line2.y1,line2.x2,line2.y2);
            System.out.println(p);
        }


    }

    public static Point2D getLineLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
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
