package spb.hse.smirnov.findpair;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Drawer {
    public static void drawFieldBase(Field field, GraphicsContext context) {
        int size = field.getSize();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, Field.WIDTH, Field.WIDTH);
        double length = (double) Field.HEIGHT / size;
        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                context.strokeRect(row * length, column * length, (row + 1) * length,
                        (column + 1) * length);
            }
        }
    }

    public static void drawCongratulations(Field field, GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, Field.WIDTH, Field.WIDTH);
        context.setFont(new Font(42));
        context.setTextAlign(TextAlignment.CENTER);
        context.strokeText("You won!", Field.WIDTH / 2.0, Field.HEIGHT / 2.0);
    }

    public static void drawActiveCells(Field field, GraphicsContext context) {
        int size = field.getSize();
        double length = (double) Field.HEIGHT / size;
        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                if (field.getStatus(row, column) != CellStatus.CLOSED) {
                    double centerX = row * length + length / 2;
                    double centerY = column * length + length / 2;
                    context.setTextAlign(TextAlignment.CENTER);
                    context.strokeText(String.valueOf(field.getNumber(row, column)),
                            centerX, centerY);
                }
            }
        }
    }
}
