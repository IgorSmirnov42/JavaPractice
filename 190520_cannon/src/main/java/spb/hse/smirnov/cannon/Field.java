package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Field {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 400;

    private static List<Point2D> points = new ArrayList<>();

    static {
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

    public void draw(GraphicsContext context) {
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

    private double getYBySegment(@NotNull Point2D left,
                                 @NotNull Point2D right, double x) {
        assert left.getX() <= x && x <= right.getX();

        double deltaX = right.getX() - left.getX();
        double deltaY = right.getY() - left.getY();
        double proportion = (x - left.getX()) / deltaX;
        return left.getY() + proportion * deltaY;
    }

    public double getYByX(double x) {
        for (int i = 1; i < points.size(); i++) {
            if (x <= points.get(i).getX()) {
                return getYBySegment(points.get(i - 1), points.get(i), x);
            }
        }
        throw new IllegalArgumentException("Segment was not found");
    }

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
