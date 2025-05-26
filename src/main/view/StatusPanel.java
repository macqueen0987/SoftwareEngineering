package main.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * JavaFX 버전의 StatusPanel
 * 현재 턴과 점수를 표시하는 하단 상태 패널
 */
public class StatusPanel extends HBox {
    private final Label turnLabel = new Label("TURN: RED");
    private final Label[] scoreLabels;
    private final String[] playerColors;

    public StatusPanel(int playerCount, String[] playerColors) {
        this.playerColors = playerColors;
        this.scoreLabels = new Label[playerCount];

        // 레이아웃 설정
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(5, 10, 5, 10));
        setPrefHeight(40);
        setBackground(new Background(new BackgroundFill(Color.web("#E6DFC8"), CornerRadii.EMPTY, Insets.EMPTY)));

        // 턴 라벨
        turnLabel.setFont(Font.font("SansSerif", 14));
        getChildren().add(turnLabel);

        // 점수 라벨
        for (int i = 0; i < playerCount; i++) {
            Label lbl = new Label(playerColors[i] + ": 0");
            lbl.setFont(Font.font("SansSerif", 14));
            getChildren().add(lbl);
            scoreLabels[i] = lbl;
        }
    }

    /**
     * 현재 턴 정보를 업데이트
     */
    public void setTurn(String playerColor) {
        turnLabel.setText("TURN: " + playerColor.toUpperCase());
    }

    /**
     * 점수 정보를 업데이트
     */
    public void setScores(int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            scoreLabels[i].setText(playerColors[i] + ": " + scores[i]);
        }
    }
}
