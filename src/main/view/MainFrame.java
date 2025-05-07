package main.view;

import main.controller.GameController;
import main.controller.UIComponents;
import main.model.GameConfig;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(GameConfig cfg) {
        String[] colors = {"red", "blue", "green", "yellow"}; // 팀 색상 일단 4개로 고정

        setTitle("윷놀이 - 통합 레이아웃");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // [1] 보드 패널 (중앙)
        BoardPanel boardPanel = new BoardPanel(cfg.boardShape());
        add(boardPanel, BorderLayout.CENTER);

        // [2] 오른쪽 패널 구성 (StickPanel + 버튼 + 말 선택 패널)
        StickPanel stickPanel = new StickPanel();
        JButton throwButton = new JButton("던지기");
        throwButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        PieceSelectPanel piecePanel = new PieceSelectPanel(cfg, colors);

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
        StatusPanel statusPanel = new StatusPanel(cfg.teamCount(), colors);
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        JButton newPieceButton = piecePanel.getNewPieceButton();
        // [★ 중요 ★] GameController 연결 (UI와 게임 연결)
        UIComponents ui = new UIComponents(boardPanel, stickPanel, statusPanel, throwButton, newPieceButton, piecePanel);
        GameController controller = new GameController(ui, cfg, colors, this);
    }

    public void declareWinner(String color) {
        int option = JOptionPane.showConfirmDialog(this,
                color.toUpperCase() + " 플레이어가 승리했습니다!\n다시 하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        this.dispose();

        // 새 게임 시작 (SetupDialog 통해서 설정받기)
        SwingUtilities.invokeLater(() -> {
            SetupDialog setup = new SetupDialog(null);
            GameConfig config = setup.showDialog();

            // 사용자가 취소한 경우
            if (config == null) {
                System.exit(0);
                return;
            }

            MainFrame newGame = new MainFrame(config);
            newGame.setVisible(true);
        });
    }
}