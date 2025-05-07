package main.view;

import javax.swing.*;
import java.awt.*;

/**
 * 현재 턴과 점수를 표시하는 하단 상태 패널
 */
public class StatusPanel extends JPanel {

    private final JLabel turnLabel = new JLabel("TURN: RED");
    private final JLabel[] scoreLabels;
    private final String[] playerColors;

    public StatusPanel(int playerCount, String[] playerColors) {
        this.playerColors = playerColors;
        this.scoreLabels = new JLabel[playerCount];

        setLayout(new GridLayout(1, playerCount + 1));
        setPreferredSize(new Dimension(650, 40));
        setBackground(new Color(230, 223, 200));

        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(turnLabel);

        for (int i = 0; i < playerCount; i++) {
            scoreLabels[i] = new JLabel(playerColors[i] + ": 0");
            scoreLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabels[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            add(scoreLabels[i]);
        }
    }

    /** 외부에서 턴 정보를 갱신할 수 있도록 제공 */
    public void setTurn(String playerColor) {
        turnLabel.setText("TURN: " + playerColor.toUpperCase());
    }

    /** 외부에서 점수 정보를 갱신할 수 있도록 제공 */
    public void setScores(int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            scoreLabels[i].setText(playerColors[i] + ": " + scores[i]);
        }
    }

}
