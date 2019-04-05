package otm.roguesque.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RoguesqueApp extends Application {

    private static final KeyCode[] CONTROL_TOGGLE_PERF_STATS = new KeyCode[]{
        KeyCode.F3
    };

    // UI
    private final BorderPane mainPanel;
    private final Scene mainScene;
    private Input input;
    private GraphicsContext ctx;
    private Canvas canvas;

    // Performance statistics
    private boolean showPerformanceDetails = false;
    private final float[] deltaSecondsHistory = new float[100];
    private int deltaSecondsHistoryCounter = 0;

    // Game state stuff
    private static final int STATE_COUNT = 4;
    private static final int STATE_INTRO = 0;
    private static final int STATE_MAINMENU = 1;
    private static final int STATE_INGAME = 2;
    private static final int STATE_GAMEOVER = 3;

    private GameState[] gameStates;
    private int currentGameStateIndex;

    public RoguesqueApp() {
        mainPanel = new BorderPane();
        mainScene = new Scene(mainPanel, 640.0, 480.0);
        input = new Input();

        gameStates = new GameState[STATE_COUNT];
        gameStates[STATE_INGAME] = new InGameState();

        currentGameStateIndex = STATE_INGAME;
    }

    private void drawGame(float deltaSeconds) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        float averageDeltaSeconds = updatePerformanceStats(deltaSeconds);
        if (showPerformanceDetails) {
            ctx.fillText("Average frame time: " + (int) (averageDeltaSeconds * 1000.0) + " ms", 10.0, 20.0);
        }

        gameStates[currentGameStateIndex].draw(ctx, deltaSeconds);
    }

    private float updatePerformanceStats(float deltaSeconds) {
        deltaSecondsHistory[deltaSecondsHistoryCounter++] = deltaSeconds;
        if (deltaSecondsHistoryCounter == deltaSecondsHistory.length) {
            deltaSecondsHistoryCounter = 0;
        }
        float averageDeltaSeconds = 0;
        for (float f : deltaSecondsHistory) {
            averageDeltaSeconds += f;
        }
        averageDeltaSeconds /= deltaSecondsHistory.length;
        return averageDeltaSeconds;
    }

    /* This is separated from drawGame just in case we want to switch to a
     * fixed timestemp sometime in the future */
    private void update(float deltaSeconds) {
        if (input.isPressed(CONTROL_TOGGLE_PERF_STATS)) {
            showPerformanceDetails = !showPerformanceDetails;
        }

        gameStates[currentGameStateIndex].update(input, deltaSeconds);
    }
    private final AnimationTimer mainLoop = new AnimationTimer() {
        long lastTime = 0;

        /* The currentTime argument doesn't start at 0, so lastTime is
             * wrong during the first frame, causing a big spike in delta time
             * at the very start. I'd rather just skip a frame to avoid that.
         */
        boolean firstRun = true;

        @Override
        public void handle(long currentTime /* note: timestamp in nanoseconds */) {
            float deltaSeconds = (float) ((currentTime - lastTime) / 1_000_000_000.0);
            lastTime = currentTime;
            if (firstRun) {
                firstRun = false;
                return;
            }

            update(deltaSeconds);
            drawGame(deltaSeconds);
            input.clearPressedKeys();
        }
    };

    @Override
    public void start(Stage stage) {
        canvas = new Canvas();
        ctx = canvas.getGraphicsContext2D();
        mainPanel.setCenter(canvas);
        canvas.widthProperty().bind(mainScene.widthProperty());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(0.0001f);
        });
        canvas.heightProperty().bind(mainScene.heightProperty());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(0.0001f);
        });

        mainScene.setOnKeyPressed((event) -> {
            input.addPressedKey(event.getCode());
        });

        mainLoop.start();

        stage.setScene(mainScene);
        stage.setTitle("Roguesque");
        stage.show();
    }
}
