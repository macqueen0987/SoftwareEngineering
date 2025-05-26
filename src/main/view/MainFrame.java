package main.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.controller.GameController;
import main.controller.UIComponents;
import main.model.GameConfig;

/**
 * JavaFX 버전의 MainFrame (JFrame → Stage)
 */
public class MainFrame {
    private final Stage stage;
    private final GameConfig cfg;
    private final String[] colors = {"red", "blue", "green", "yellow"};

    public MainFrame(Stage stage, GameConfig cfg) {
        this.stage = stage;
        this.cfg = cfg;
        buildUI();
    }

    private void buildUI() {
        stage.setTitle("윷놀이 - 통합 레이아웃");
        BorderPane root = new BorderPane();

        // [1] 중앙 보드
        BoardPanel boardPanel = new BoardPanel(cfg.boardShape());
        root.setCenter(boardPanel);

        // [2] 우측 패널
        StickPanel stickPanel = new StickPanel();
        Button forceThrowButton = new Button("지정 윷 던지기");
        Button randomThrowButton = new Button("랜덤 윷 던지기");
        forceThrowButton.setFont(Font.font("SansSerif", 18));
        randomThrowButton.setFont(Font.font("SansSerif", 18));

        HBox buttonBox = new HBox(20, forceThrowButton, randomThrowButton);
        buttonBox.setPadding(new Insets(0, 0, 0, 0));
        buttonBox.setBackground(null);

        PieceSelectPanel piecePanel = new PieceSelectPanel(cfg, colors);

        VBox rightPanel = new VBox(30, stickPanel, buttonBox, piecePanel);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #D2B48C;");
        VBox.setVgrow(piecePanel, Priority.ALWAYS);
        root.setRight(rightPanel);

        // [3] 하단 상태 패널
        StatusPanel statusPanel = new StatusPanel(cfg.teamCount(), colors);
        root.setBottom(statusPanel);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.show();

        // 컨트롤러 연결
        UIComponents ui = new UIComponents(
                boardPanel,
                stickPanel,
                statusPanel,
                randomThrowButton,
                forceThrowButton,
                piecePanel.getNewPieceButton(),
                piecePanel
        );
        new GameController(ui, cfg, colors, this);
    }

    public void declareWinner(String color) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                color.toUpperCase() + " 플레이어가 승리했습니다!\n다시 하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("게임 종료");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) restartGame();
            else stage.close();
        });
    }

    private void restartGame() {
        stage.close();
        // JavaFX용 SetupDialog 호출 및 재시작 로직 필요
        SetupDialog dialog = new SetupDialog(stage);
        GameConfig newCfg = dialog.showDialog();
        if (newCfg != null) new MainFrame(stage, newCfg);
    }
}
