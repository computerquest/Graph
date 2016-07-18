package sample;

/**
 * Created by jared_000 on 7/17/2016.
 */
public class Line {
    double slope;
    double intercept;
    DataPoint begining;
    DataPoint end;

    public Line(DataPoint beginingInput, DataPoint endInput) {
        begining = beginingInput;
        end = endInput;

        calcSlope();
        calcIntercept();
    }

    public Line(double x, double y, double xOne, double yOne) {
        begining = new DataPoint(x, y);
        end = new DataPoint(xOne, yOne);

        calcSlope();
        calcIntercept();
    }

    public void calcSlope() {
        slope = (begining.x - end.x)/(begining.y-end.y);
    }

    public void calcIntercept() {
        intercept = (slope*begining.x)+begining.y;
    }
    public double yAt(double xPosition) {
        return (xPosition*slope)+intercept;
    }
}
