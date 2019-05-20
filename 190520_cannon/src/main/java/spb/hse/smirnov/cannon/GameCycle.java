package spb.hse.smirnov.cannon;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class GameCycle {
    @NotNull private GraphicsContext context;
    @NotNull private Aim aim;
    @NotNull private Player player;
    @NotNull private Field field;
    @NotNull List<Bullet> bullets = new ArrayList<>();
    AnimationTimer timer;
    private static GameCycle instance = null;

    public GameCycle(@NotNull GraphicsContext context,
                     @NotNull Field field,
                     @NotNull Player player,
                     @NotNull Aim aim) {
        if (instance != null) {
            throw new IllegalStateException("Cannot create second instance of GameCycle");
        }
        this.context = context;
        this.player = player;
        this.field = field;
        this.aim = aim;
        instance = this;
    }

    @NotNull
    public static GameCycle getInstance() {
        if (instance == null) {
            throw new IllegalStateException("getInstance() was called before initialization");
        }
        return instance;
    }

    public void startGame() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawAll();
            }
        };
        timer.start();
    }

    public void addBullet(@NotNull Bullet bullet) {
        bullets.add(bullet);
    }

    public void onAimHit() {
        timer.stop();
    }

    private void cleanScreen() {
        context.clearRect(0, 0, Field.WIDTH, Field.HEIGHT);
    }

    private void drawAll() {
        cleanScreen();
        field.draw(context);
        player.draw(context);
        aim.draw(context);
        for (var bullet : bullets) {
            bullet.draw(context);
        }
    }
}
