package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Bullet {
    private static int index = 1;
    private static final List<BulletType> types = new ArrayList<>();
    private BulletType bullet;
    private static final double GRAVITY = 100;
    private static final int SECOND = 1000;
    private long lastTimeUpdated = System.currentTimeMillis();
    private double x;
    private double y;
    private double xSpeed;
    private double ySpeed;

    static {
        types.add(new BulletType(10, 7, 250));
        types.add(new BulletType(12.5, 12, 175));
        types.add(new BulletType(15, 20, 100));
    }

    public Bullet(Point2D point, double angle) {
        System.out.println("SIZE IS " + index);
        this.x = point.getX();
        this.y = point.getY();
        bullet = types.get(index);
        xSpeed = Math.cos(angle) * bullet.startSpeed;
        ySpeed = Math.sin(angle) * bullet.startSpeed;
    }

    public void draw(@NotNull GraphicsContext context) {
        recalculatePosition();
        context.fillOval(x - bullet.size / 2,
                           y - bullet.size / 2,
                              bullet.size,
                              bullet.size);
    }

    private void recalculatePosition() {
        long deltaTime = System.currentTimeMillis() - lastTimeUpdated;
        lastTimeUpdated += deltaTime;
        x += xSpeed * deltaTime / SECOND;
        ySpeed -= GRAVITY * deltaTime / SECOND;
        y -= ySpeed * deltaTime / SECOND;
    }

    public static void plusSize() {
        index = Math.min(index + 1, types.size() - 1);
    }

    public static void minusSize() {
        index = Math.max(index - 1, 0);
    }

    private static final class BulletType {
        private double size;
        private double hitRadius;
        private double startSpeed;
        private BulletType(double size, double hitRadius, double startSpeed) {
            this.size = size;
            this.hitRadius = hitRadius;
            this.startSpeed = startSpeed;
        }
    }
}
