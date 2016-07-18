package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.LinkedList;

/**
 * Created by jared_000 on 7/17/2016.
 */
public class Graph {
    Canvas canvas = new Canvas(200, 290);
    GraphicsContext graphic = canvas.getGraphicsContext2D();
    LinkedList<Line> lineMl = new LinkedList<Line>();
    double yScale = 10;
    //double maxX = 0;
    LinkedList<DataPoint> dataMl = new LinkedList<>();
    double largetPossibleY = canvas.getHeight()/yScale;

    public Graph() {
        setUp();

        DataPoint tempPoint = new DataPoint(0,0);
        dataMl.add(tempPoint);
    }

    public void setUp() {
        graphic.strokeLine(0,0, 0,canvas.getHeight());
        graphic.strokeLine(0,canvas.getHeight(), canvas.getWidth(),canvas.getHeight());

        graphic.setLineDashes(5);
        graphic.setStroke(Color.LIGHTGREY);
        for(int i = 1; i < canvas.getWidth()/10; i++) {
            if(i%10 == 0) {
                graphic.strokeLine(i * 10, 0, i * 10, canvas.getHeight());
            }
        }
        graphic.setStroke(Color.BLACK);
        graphic.setLineDashes(0);
    }

    public void changeCanvasWidth(double x) {
        canvas.setWidth(x*10+50);

        setUp();
    }

    public void newPoint(double x, double y) {
        if(y > largetPossibleY) {
            refactorScale(y);
        }

        if(x*10 > canvas.getWidth()-50) {
            changeCanvasWidth(x);
        }
        DataPoint newPoint = new DataPoint(x, y);

        Line newLine = new Line(dataMl.get(dataMl.size()-1), newPoint);

        dataMl.add(newPoint);
        lineMl.add(newLine);

        drawLine(newLine);
    }

    public void drawLine(Line line) {
        double y = canvas.getHeight()-line.begining.y*yScale;
        double yOne = canvas.getHeight() -line.end.y*yScale;

        graphic.fillOval(line.begining.x*10-1, y-2, 2,2);
        graphic.strokeLine(line.begining.x*10, y, line.end.x*10, yOne);
    }

    public void refactorScale(double y) {
        double scaleFactor = largetPossibleY/(y);
        yScale *= scaleFactor;

        reDraw();
    }

    public void reDraw() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        for(int i = 0; i < lineMl.size(); i++) {
            drawLine(lineMl.get(i));
        }

        setUp();
    }
}
