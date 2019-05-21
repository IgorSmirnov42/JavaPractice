package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

public class Player {
    private double barrelAngle = 0;
    private static final double MIN_BARREL_ANGLE = -Math.PI / 4;
    private static final double MAX_BARREL_ANGLE = Math.PI / 4;
    private Field field;
    private double x = 250;
    private long lastTimeUpdated;
    private KeyCode keyCode = null;
    private static final int SECOND = 1000;
    private static final int MOVE_PIXELS_PER_SECOND = 50;
    private static final double ROTATION_RADIANS_PER_SECOND = Math.PI / 8;
    private static final double CANNON_WIDTH = 30;
    private static final double CANNON_HEIGHT = 15;
    private static final int BARREL_LENGTH = 20;
    private static final int BARREL_WIDTH = 10;

    public Player(@NotNull Field field) {
        this.field = field;
    }

    public void draw(@NotNull GraphicsContext context) {
        updatePosition();
        double y = field.getYByX(x);
        double realAngle = getRealAngle();

        var lineWidth = context.getLineWidth();
        var stroke = context.getStroke();

        context.setLineWidth(BARREL_WIDTH);
        context.setStroke(Color.DARKGREEN);
        Point2D barrelEnd = getBarrelEnd(realAngle);
        context.strokeLine(x, y, barrelEnd.getX(), barrelEnd.getY());

        context.setStroke(Color.FIREBRICK);
        context.setLineWidth(CANNON_HEIGHT);
        var line = new Line();
        line.setStartX(x - CANNON_WIDTH / 2);
        line.setEndX(x + CANNON_WIDTH / 2);
        line.setStartY(y);
        line.setEndY(y);
        line = Geometry.rotateLine(line, realAngle - barrelAngle);
        context.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

        context.setLineWidth(lineWidth);
        context.setStroke(stroke);
    }

    private void updatePosition() {
        if (keyCode == null) {
            return;
        }

        long deltaTime = System.currentTimeMillis() - lastTimeUpdated;
        lastTimeUpdated += deltaTime;

        if (keyCode.equals(KeyCode.LEFT)) {
            updateX(deltaTime);
        } else if (keyCode.equals(KeyCode.RIGHT)) {
            updateX(-deltaTime);
        } else if (keyCode.equals(KeyCode.UP)) {
            updateBarrelAngle(deltaTime);
        } else if (keyCode.equals(KeyCode.DOWN)) {
            updateBarrelAngle(-deltaTime);
        }
    }

    private void updateBarrelAngle(long signedDeltaTime) {
        barrelAngle += (double) signedDeltaTime / SECOND * ROTATION_RADIANS_PER_SECOND;
        if (barrelAngle < MIN_BARREL_ANGLE) {
            barrelAngle = MIN_BARREL_ANGLE;
        }
        if (barrelAngle > MAX_BARREL_ANGLE) {
            barrelAngle = MAX_BARREL_ANGLE;
        }
    }

    private void updateX(long signedDeltaTime) {
        x -= (double) signedDeltaTime / SECOND * MOVE_PIXELS_PER_SECOND;
        if (x < 0) {
            x = 0;
        }
        if (x > Field.WIDTH) {
            x = Field.WIDTH;
        }
    }

    private Point2D getBarrelEnd(double realAngle) {
        return new Point2D(x + Math.cos(realAngle) * BARREL_LENGTH,
                field.getYByX(x) - Math.sin(realAngle) * BARREL_LENGTH);
    }

    public void onKeyPressed(@NotNull KeyCode code) {
        if (!code.equals(keyCode)) {
            keyCode = code;
            lastTimeUpdated = System.currentTimeMillis();
        }
    }

    private double getRealAngle() {
        return barrelAngle + field.getCannonAngle(x);
    }

    public void onKeyReleased(@NotNull KeyCode code) {
        if (code.equals(KeyCode.ENTER)) {
            double realAngle = getRealAngle();
            GameCycle.getInstance().addBullet(
                    new Bullet(getBarrelEnd(realAngle), realAngle, field));
        } else if (code.equals(KeyCode.D)) {
            Bullet.plusSize();
        } else if (code.equals(KeyCode.A)) {
            Bullet.minusSize();
        }
        keyCode = null;
    }
}
