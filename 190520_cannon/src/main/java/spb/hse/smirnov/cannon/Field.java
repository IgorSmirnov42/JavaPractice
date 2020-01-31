package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/** Class with methods to work with game field */
public class Field implements Drawable {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 400;

    /** Edges of mountains sorted by x */
    private static List<Point2D> points = new ArrayList<>();

    static {
        // Creating hardcoded field
        points.add(new Point2D(0, 0));
        points.add(new Point2D(100, 300));
        points.add(new Point2D(200, 100));
        points.add(new Point2D(300, 200));
        points.add(new Point2D(400, 20));
        points.add(new Point2D(500, 300));
        points.add(new Point2D(600, 70));
        points.add(new Point2D(700, 200));
        points.add(new Point2D(800, 100));
        points.add(new Point2D(800, 20));
        points.add(new Point2D(1000, 230));
    }

    /** {@inheritDoc} */
    @Override
    public void draw(@NotNull GraphicsContext context) {
        var stroke = context.getStroke();
        var lineWidth = context.getLineWidth();

        context.setLineWidth(1);
        context.setStroke(Color.BLACK);
        for (int i = 0; i < points.size() - 1; i++) {
            context.strokeLine(points.get(i).getX(), points.get(i).getY(),
                    points.get(i + 1).getX(), points.get(i + 1).getY());
        }

        context.setStroke(stroke);
        context.setLineWidth(lineWidth);
    }

    /**
     * Returns y-coordinate on segment left-right by x coordinate
     * @throws IllegalArgumentException if x was not from given segment
     */
    private double getYBySegment(@NotNull Point2D left, @NotNull Point2D right, double x) {
        if (!(left.getX() <= x && x <= right.getX())) {
            throw new IllegalArgumentException("x is not in given segment");
        }

        double deltaX = right.getX() - left.getX();
        double deltaY = right.getY() - left.getY();
        double proportion = (x - left.getX()) / deltaX;
        return left.getY() + proportion * deltaY;
    }

    /**
     * Returns y-coordinate of point on a field by x-coordinate
     * @throws IllegalArgumentException if x is not presented on field
     */
    public double getYByX(double x) {
        for (int i = 1; i < points.size(); i++) {
            if (x <= points.get(i).getX()) {
                return getYBySegment(points.get(i - 1), points.get(i), x);
            }
        }
        throw new IllegalArgumentException("Segment was not found");
    }

    /** Checks if bullet presented as circle intersects some border */
    public boolean isHit(@NotNull Circle circle) {
        for (int i = 1; i < points.size(); i++) {
            if (Geometry.circleIntersectsSegment(circle,
                    new Line(points.get(i - 1).getX(),
                             points.get(i - 1).getY(),
                             points.get(i).getX(),
                             points.get(i).getY()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns angle of cannon that depends of line angle that cannon stands on.
     * @throws IllegalArgumentException if cannon is somehow out of field
     */
    public double getCannonAngle(double x) {
        for (int i = 1; i < points.size(); i++) {
            if (x <= points.get(i).getX()) {
                return Geometry.getNormalAngle(new Line(points.get(i - 1).getX(),
                                                        points.get(i - 1).getY(),
                                                        points.get(i).getX(),
                                                        points.get(i).getY()));
            }
        }
        throw new IllegalArgumentException("Segment was not found");
    }

}
