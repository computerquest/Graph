package sample;

/**
 * Created by jared_000 on 7/17/2016.
 */
public class LineSegment {
    double slope;
    double intercept;
    DataPoint begining;
    DataPoint end;

    public LineSegment(DataPoint beginingInput, DataPoint endInput) {
        begining = beginingInput;
        end = endInput;

        calcSlope();
        calcIntercept();
    }

    public LineSegment(double x, double y, double xOne, double yOne) {
        begining = new DataPoint(x, y);
        end = new DataPoint(xOne, yOne);

        calcSlope();
        calcIntercept();
    }

    public LineSegment() {
    }

    public void calcSlope() {
        slope = (begining.y - end.y) / (begining.x - end.x);
    }

    public void calcIntercept() {
        intercept = begining.y - (slope * begining.x);
    }

    public boolean segmentContains(double x, double y) {
        if (yAt(x) == y) {
            return true;
        }

        return false;
    }

    public boolean segmentContainsRounded(double x, double y) {
        if (Math.round(yAt(Math.round(x))) == Math.round(y)) {
            return true;
        }

        return false;
    }

    public double yAt(double xPosition) {
        return (xPosition * slope) + intercept;
    }
}
