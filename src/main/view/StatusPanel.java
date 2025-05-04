package main.view;

import javax.swing.*;
import java.awt.*;

/**
 * 현재 턴과 점수를 표시하는 하단 상태 패널
 */
public class StatusPanel extends JPanel {

    private final JLabel turnLabel  = new JLabel("TURN: RED");
    private final JLabel scoreLabel = new JLabel("Score 0 : 0");

    public StatusPanel() {
        setLayout(new GridLayout(1, 2)); // 좌우로 2등분
        setPreferredSize(new Dimension(650, 40));
        setBackground(new Color(230, 223, 200)); // 밝은 베이지

        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        add(turnLabel);
        add(scoreLabel);
    }

    /** 외부에서 턴 정보를 갱신할 수 있도록 제공 */
    public void setTurn(String playerColor) {
        turnLabel.setText("TURN: " + playerColor.toUpperCase());
    }

    /** 외부에서 점수 정보를 갱신할 수 있도록 제공 */
    public void setScore(int red, int blue) {
        scoreLabel.setText("Score " + red + " : " + blue);
    }
}
