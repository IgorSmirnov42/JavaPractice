package spb.hse.smirnov.cannon;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GameCycle {
    @NotNull private GraphicsContext context;
    @NotNull private Aim aim;
    @NotNull private Player player;
    @NotNull private Field field;
    @NotNull private List<Bullet> bullets = new LinkedList<>();
    private AnimationTimer timer;
    private static GameCycle instance = null;
    private boolean gameFinished = false;

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
        cleanScreen();
        var textFont = context.getFont();
        var textAlign = context.getTextAlign();
        context.setFont(new Font(42));
        context.setTextAlign(TextAlignment.CENTER);
        context.strokeText("You won!", Field.WIDTH / 2.0, Field.HEIGHT / 2.0);
        gameFinished = true;
        context.setFont(textFont);
        context.setTextAlign(textAlign);
    }

    private void cleanScreen() {
        context.clearRect(0, 0, Field.WIDTH, Field.HEIGHT);
    }

    private void drawAll() {
        cleanScreen();
        field.draw(context);
        for (var bulletIterator = bullets.iterator(); bulletIterator.hasNext();) {
            var bullet = bulletIterator.next();
            bullet.draw(context);
            if (!bullet.isAlive()) {
                bulletIterator.remove();
            }
            if (gameFinished) {
                break;
            }
        }
        if (!gameFinished) {
            player.draw(context);
            aim.draw(context);
        }
    }
}
