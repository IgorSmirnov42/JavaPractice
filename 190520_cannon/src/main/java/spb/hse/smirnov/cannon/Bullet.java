package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Bullet {
    private static int index = 1;
    @NotNull private static final List<BulletType> types = new ArrayList<>();
    private static final long ZOMBIE_TIME = 500;
    @NotNull private BulletType bullet;
    private static final double GRAVITY = 100;
    private static final int SECOND = 1000;
    private long lastTimeUpdated = System.currentTimeMillis();
    private double x;
    private double y;
    private double xSpeed;
    private double ySpeed;
    private BulletStatus status = BulletStatus.ALIVE;
    @NotNull private Field field;

    static {
        types.add(new BulletType(7, 3.5, 250));
        types.add(new BulletType(9.5, 6, 175));
        types.add(new BulletType(12, 20, 100));
    }

    public Bullet(@NotNull Point2D point, double angle, @NotNull Field field) {
        this.x = point.getX();
        this.y = point.getY();
        this.field = field;
        bullet = types.get(index);
        xSpeed = Math.cos(angle) * bullet.startSpeed;
        ySpeed = Math.sin(angle) * bullet.startSpeed;
    }

    public boolean isAlive() {
        return status != BulletStatus.DEAD;
    }

    public void draw(@NotNull GraphicsContext context) {
        recalculatePosition();
        if (status == BulletStatus.ALIVE) {
            context.setFill(Color.AQUA);
            context.fillOval(x - bullet.size / 2,
                    y - bullet.size / 2,
                    bullet.size,
                    bullet.size);
        } else {
            context.setFill(Color.MEDIUMVIOLETRED);
            context.fillOval(x - bullet.size / 2,
                    y - bullet.size / 2,
                    bullet.hitRadius * 2,
                    bullet.hitRadius * 2);
        }
    }

    private void recalculatePosition() {
        long deltaTime = System.currentTimeMillis() - lastTimeUpdated;
        if (status == BulletStatus.ZOMBIE) {
            if (deltaTime >= ZOMBIE_TIME) {
                status = BulletStatus.DEAD;
            }
            return;
        }
        lastTimeUpdated += deltaTime;
        x += xSpeed * deltaTime / SECOND;
        ySpeed -= GRAVITY * deltaTime / SECOND;
        y -= ySpeed * deltaTime / SECOND;
        if (x < 0 || y > Field.HEIGHT || x > Field.WIDTH) {
            status = BulletStatus.DEAD;
        }
        checkHit();
    }

    private void checkHit() {
        if (field.isHit(new Circle(x, y, bullet.size / 2))) {
            status = BulletStatus.ZOMBIE;
            if (hitAim()) {
                GameCycle.getInstance().onAimHit();
            }
        }
    }

    private boolean hitAim() {
        return false;
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
