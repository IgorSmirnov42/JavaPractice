package spb.hse.smirnov.cannon;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/** Class controlling the game cycle
 * Single-thread single tone
 */
public class GameCycle {
    @NotNull private GraphicsContext context;
    @NotNull private Aim aim;
    @NotNull private Player player;
    @NotNull private Field field;
    /** List of bullets that should be seen by user */
    @NotNull private List<Bullet> bullets = new LinkedList<>();
    private AnimationTimer timer;
    private static GameCycle instance = null;
    private boolean gameFinished = false;

    /**
     * Creates new instance if there is no created before
     * @throws IllegalStateException if there is an active instance
     */
    public GameCycle(@NotNull GraphicsContext context, @NotNull Field field,
                     @NotNull Player player, @NotNull Aim aim) {
        if (instance != null) {
            throw new IllegalStateException("Cannot create second instance of GameCycle");
        }
        this.context = context;
        this.player = player;
        this.field = field;
        this.aim = aim;
        instance = this;
    }

    /**
     * Returns current instance
     * @throws IllegalStateException if no instance was created
     */
    @NotNull
    public static GameCycle getInstance() {
        if (instance == null) {
            throw new IllegalStateException("getInstance() was called before initialization");
        }
        return instance;
    }

    /** Starts game cycle */
    public void startGame() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawAll();
            }
        };
        timer.start();
    }

    /** Adds bullet to list of active bullets */
    public void addBullet(@NotNull Bullet bullet) {
        bullets.add(bullet);
    }

    /** Reacts on hitting an aim (shows congratulations screen, stops timer) */
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

    /** Clears everything printed on a screen */
    private void cleanScreen() {
        context.clearRect(0, 0, Field.WIDTH, Field.HEIGHT);
    }

    /** Draws all active objects */
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
