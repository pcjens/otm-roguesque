package otm.roguesque.ui.states;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Päävalikko-tila. Ruudulla lukee pelin otsikko, ja pelaaja voi joko aloittaa
 * tai sulkea pelin kahdesta napista.
 *
 * @author Jens Pitkänen
 */
public class MainMenuState implements GameState {

    private final Button playButton = new Button(new KeyCode[]{KeyCode.P}, "Play", 180, 280, 80, 45, 0);
    private final Button replayButton = new Button(new KeyCode[]{KeyCode.R}, "Replay", 180, 350, 100, 45, 0);
    private final Button quitButton = new Button(new KeyCode[]{KeyCode.Q}, "Quit", 280, 280, 80, 45, 0);

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        double xOffset = (canvas.getWidth() - 640) / 2;
        double yOffset = (canvas.getHeight() - 480) / 2;

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        ctx.fillText("Roguesque", 180.0 + xOffset, 200.0 + yOffset);

        playButton.draw(ctx, xOffset, yOffset);
        quitButton.draw(ctx, xOffset, yOffset);
        if (showDebugInfo) {
            replayButton.draw(ctx, xOffset, yOffset);
        }
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        playButton.update(input);
        quitButton.update(input);
        if (showDebugInfo) {
            replayButton.update(input);
        }

        if (playButton.isClicked()) {
            return GameState.STATE_INGAME;
        }
        if (quitButton.isClicked()) {
            return GameState.STATE_QUIT;
        }
        if (showDebugInfo && replayButton.isClicked()) {
            return GameState.STATE_REPLAY;
        }

        return -1;
    }
}
