package view;

import controller.GameController;
import model.GameConfig;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {

    public MainFrame(GameConfig cfg) {

        setTitle("윷놀이 - 통합 레이아웃");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // [1] 보드 패널 (중앙)
        BoardPanel boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // [2] 오른쪽 패널 구성 (StickPanel + 버튼 + 말 선택 패널)
        StickPanel stickPanel = new StickPanel();
        JButton throwButton = new JButton("던지기");
        throwButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        PieceSelectPanel piecePanel = new PieceSelectPanel(cfg);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(560, 700));
        rightPanel.setBackground(new Color(210, 180, 140));

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(stickPanel);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(throwButton);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(piecePanel);
        rightPanel.add(Box.createVerticalGlue());

        add(rightPanel, BorderLayout.EAST);

        // [3] 상태 패널 (하단)
        StatusPanel statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // [★ 중요 ★] GameController 연결 (UI와 게임 연결)
        GameController controller = new GameController(boardPanel, stickPanel, statusPanel, throwButton);
    }
}