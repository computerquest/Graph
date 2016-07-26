package sample;

import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;

import java.util.LinkedList;

/**
 * Created by jared_000 on 7/18/2016.
 */
public class Line {
    LinkedList<LineSegment> lineMl = new LinkedList<>();
    LinkedList<DataPoint> dataMl = new LinkedList<>();
    Color color;
    String file;
    boolean used = true;

    public Line(Color colorInput) {
        color = colorInput;
        dataMl.add(new DataPoint(0, 0));
    }

    public LineSegment findSegmentContaining(double xPos) {
        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).begining.x <= xPos && lineMl.get(i).end.x >= xPos) {
                return lineMl.get(i);
            }
        }

        return new LineSegment(0, 0, 0, 0);
    }

    public void newDataPoint(double x, double y) {
        lineMl.add(new LineSegment(dataMl.get(dataMl.size() - 1), new DataPoint(x, y)));
        dataMl.add(new DataPoint(x, y));
    }
}
