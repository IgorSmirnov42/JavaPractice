package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public class Player {
    private double barrelAngle = Math.PI / 2;
    private Field field;
    private double x = 250;
    private long lastTimeUpdated;
    private KeyCode keyCode = null;
    private static final int SECOND = 1000;
    private static final int MOVE_PIXELS_PER_SECOND = 50;
    private static final double ROTATION_RADIANS_PER_SECOND = Math.PI / 2;
    private static final double PLAYER_WIDTH = 30;
    private static final double PLAYER_HEIGHT = 30;
    private static final int BARREL_LENGTH = 20;
    private static final int BARREL_WIDTH = 8;

    public Player(@NotNull Field field) {
        this.field = field;
    }

    public void draw(@NotNull GraphicsContext context) {
        updatePosition();
        double y = field.getYByX(x);

        context.setLineWidth(BARREL_WIDTH);
        context.setStroke(Color.DARKGREEN);
        Point2D barrelEnd = getBarrelEnd();
        context.strokeLine(x, y, barrelEnd.getX(), barrelEnd.getY());


        context.setFill(Color.FIREBRICK);
        context.fillOval(x - PLAYER_WIDTH / 2,
                y - PLAYER_HEIGHT / 2,
                PLAYER_WIDTH, PLAYER_HEIGHT);
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

    private Point2D getBarrelEnd() {
        return new Point2D(x + Math.cos(barrelAngle) * BARREL_LENGTH,
                field.getYByX(x) - Math.sin(barrelAngle) * BARREL_LENGTH);
    }

    public void onKeyPressed(@NotNull KeyCode code) {
        if (!code.equals(keyCode)) {
            keyCode = code;
            lastTimeUpdated = System.currentTimeMillis();
        }
    }

    public void onKeyReleased(@NotNull KeyCode code) {
        if (code.equals(KeyCode.ENTER)) {
            GameCycle.getInstance().addBullet(
                    new Bullet(getBarrelEnd(), barrelAngle));
        } else if (keyCode.equals(KeyCode.D)) {
            Bullet.plusSize();
        } else if (keyCode.equals(KeyCode.A)) {
            Bullet.minusSize();
        }
        keyCode = null;
    }
}
