package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.LinkedList;

/**
 * Created by jared_000 on 7/17/2016.
 */
public class Graph {
    Canvas canvas = new Canvas(860, 295);
    GraphicsContext graphic = canvas.getGraphicsContext2D();
    LinkedList<Line> lineMl = new LinkedList<Line>();
    double yScale = 10;
    double xScale = 8.6;
    double largetPossibleY = (canvas.getHeight() - 15) / yScale;
    double currentFrame = -1;
    double secondaryFrame = -2;
    double lastX = 0;

    public Graph() {
        setUp();

    }

    public void setUp() {
        graphic.setLineDashes(5);
        graphic.setStroke(Color.LIGHTGREY);
        graphic.setFill(Color.DARKGREY);

        graphic.setFont(new Font("Sans Serif", 10));

        int xMagnitude = 10;
        int dataVolume = (int) (secondaryFrame != -2 ? secondaryFrame - currentFrame : (currentFrame + 100) - lastX);
        for (int i = dataVolume; i / xMagnitude > 10; xMagnitude *= 10) {
        }

        if (dataVolume <= xMagnitude + (.5 * xMagnitude)) {
            xMagnitude /= 10;
        }

        for (int i = xMagnitude; i < (canvas.getWidth() / xScale); i += xMagnitude) {
            graphic.strokeLine(i * xScale, canvas.getHeight(), i * xScale, 0);
            graphic.fillText(Double.toString(i + lastX), i * xScale + 3, canvas.getHeight() - 7);
        }

        int yMagnitude = 10;
        for (double i = largetPossibleY; i / yMagnitude > 50; yMagnitude *= 10) {
        }

        if (largetPossibleY <= yMagnitude + (.5 * yMagnitude)) {
            yMagnitude /= 10;
        }

        for (int i = yMagnitude; i < canvas.getHeight() / yScale; i += yMagnitude) {
            graphic.fillText(Double.toString(i), 3, canvas.getHeight() - i * yScale);
        }

        graphic.setStroke(Color.BLACK);
        graphic.setLineDashes(0);
    }

    public void newLine(Color color) {
        lineMl.add(new Line(color));
    }

    public double makeRegX(double x) {
        return x / xScale;
    }

    public double makeRegY(double y) {
        return (canvas.getHeight() - y) / yScale;
    }

    public void newPoint(double x, double y, int lineNum) {
        lineMl.get(lineNum).newDataPoint(x, y);
    }

    public void newPoint(double x, double y, Color color) {
        newPoint(x, y, findLineI(color));
    }

    public void drawLineSegment(LineSegment line, int lineNum) {
        graphic.setStroke(lineMl.get(lineNum).color);

        drawLineSegment(line);

        graphic.setStroke(Color.BLACK);
    }

    public void drawLineSegment(LineSegment line) {
        double xValueB = line.begining.x < currentFrame ? currentFrame : line.begining.x;
        double xValueE = line.end.x;

        if (line.end.x > secondaryFrame & secondaryFrame != -2) {
            xValueE = secondaryFrame;
        } else if (line.end.x > currentFrame + 100 & secondaryFrame == -20) {

            xValueE = currentFrame + 100;
        }

        double y = canvas.getHeight() - line.yAt(xValueB) * yScale;
        double yOne = canvas.getHeight() - line.yAt(xValueE) * yScale;

        graphic.fillOval((xValueB - lastX) * xScale - 1, y - 2, 2, 2);
        graphic.strokeLine((xValueB - lastX) * xScale, y, (xValueE - lastX) * xScale, yOne);
    }

    public void refactorScaleFrame(double frame) {
        double y = 0;

        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }
            for (int a = (int) frame; a < frame + 100 & a < lineMl.get(i).lineMl.size(); a++) {
                if (lineMl.get(i).lineMl.get(a).begining.y > y) {
                    y = lineMl.get(i).lineMl.get(a).begining.y;
                }
            }
        }

        if (y > 0) {
            double scaleFactor = largetPossibleY / (y);
            yScale *= scaleFactor;
            largetPossibleY = (canvas.getHeight() - 15) / yScale;
        }
    }

    public void refactorScaleJoin(double frame, double otherFrame) {
        double y = 0;

        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }

            for (int a = (int) frame; a <= otherFrame & a < lineMl.get(i).lineMl.size(); a++) {
                if (lineMl.get(i).lineMl.get(a).begining.y > y) {
                    y = lineMl.get(i).lineMl.get(a).begining.y;
                }
            }
        }

        if (y > 0) {
            double scaleFactor = largetPossibleY / (y);
            yScale *= scaleFactor;
            largetPossibleY = (canvas.getHeight() - 15) / yScale;
        }
    }

    public void drawAll(double frame) {

        this.currentFrame = frame;

        clear();

        refactorScaleFrame(frame);

        findLastX(frame);

        setUp();

        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }
            for (int a = (int) frame; a <= frame + 100 && a < lineMl.get(i).lineMl.size(); a++) {
                drawLineSegment(lineMl.get(i).lineMl.get(a), i);
            }
        }
    }

    public void findLastX(double frame) {
        double subtractX = 0;

        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }
            if (frame > lineMl.get(i).lineMl.size() & lineMl.get(i).lineMl.get(lineMl.get(i).lineMl.size() - 1).end.x > subtractX) {
                subtractX = lineMl.get(i).lineMl.get(lineMl.get(i).lineMl.size() - 1).end.x;
            } else if (lineMl.get(i).lineMl.size() > frame) {
                subtractX = lineMl.get(i).lineMl.get((int) frame).end.x;
            }
        }

        lastX = frame;
    }

    public Line findLine(Color color) {
        for (int i = 0; i < lineMl.size(); i++) {
            if (color == lineMl.get(i).color) {
                return lineMl.get(i);
            }
        }

        return new Line(Color.AQUAMARINE);
    }

    public int findLineI(Color color) {
        for (int i = 0; i < lineMl.size(); i++) {
            if (color == lineMl.get(i).color) {
                return i;
            }
        }

        return -1;
    }

    public void clear() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

    public void clearAll() {
        clear();
        setToDefault();
        lineMl.clear();
    }

    public void setToDefault() {
        xScale = 8.6;
    }

    public void joinFrame(double frame, double otherFrame) {
        clear();

        refactorScaleJoin(frame, otherFrame);

        xScale = canvas.getWidth() / (otherFrame - frame);

        secondaryFrame = otherFrame;

        findLastX(frame);

        setUp();

        for(int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }

            for (int a = (int) frame; a <= otherFrame && a < lineMl.get(i).lineMl.size(); a++) {
                drawLineSegment(lineMl.get(i).lineMl.get(a), i);
            }
        }
    }

    public LinkedList<Color> linesContaining(double x, double y) {
        LinkedList<Color> answer = new LinkedList<>();

        for (int i = 0; i < lineMl.size(); i++) {
            if (lineMl.get(i).used == false) {
                continue;
            }

            LineSegment segment = lineMl.get(i).findSegmentContaining(x);

            System.out.println(segment.begining.x + " " + segment.end.x);
            if (segment.begining.x == 0 & segment.begining.y == 0 & segment.end.x == 0 & segment.end.y == 0) {
                continue;
            }

            if (segment.segmentContainsRounded(x, y) == true) {
                answer.add(lineMl.get(i).color);
            }
        }

        return answer;
    }

    public void linearRegression(LinkedList<DataPoint> data) {
        double allX = 0;
        double allY = 0;
        double allXSq = 0;
        double all = 0;

        for (int i = 0; i < data.size(); i++) {
            allX += data.get(i).x;
            allY += data.get(i).y;
            allXSq += Math.pow(data.get(i).x, 2);
            all += (data.get(i).x * data.get(i).y);
        }

        LineSegment lineSegment = new LineSegment();
        lineSegment.slope = ((data.size() * all) - (allX * allY)) / ((data.size() * allXSq) - Math.pow(allX, 2));

        lineSegment.intercept = ((allY * allXSq) - (allX * all)) / ((data.size() * allXSq) - Math.pow(allX, 2));

        graphic.setLineDashes(5);
        graphic.setStroke(Color.DARKGREY);

        lineSegment.begining = new DataPoint(currentFrame, lineSegment.yAt(currentFrame));
        lineSegment.end = new DataPoint(secondaryFrame != -2 ? secondaryFrame : currentFrame + 100, lineSegment.yAt(secondaryFrame != -2 ? secondaryFrame : currentFrame + 100));
        drawLineSegment(lineSegment);

        graphic.setLineDashes(0);
        graphic.setStroke(Color.BLACK);
    }
}
