package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("윷놀이 - 통합 레이아웃");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* 1) 왼쪽 : 보드 */
        BoardPanel board = new BoardPanel();
        add(board, BorderLayout.CENTER);

        // StickPanel 생성
        StickPanel stick = new StickPanel();                       // (540 × 170)
        stick.setFaces(new boolean[]{true,false,true,true}, false);

        // 던지기 버튼
        JButton throwBtn = new JButton("던지기");
        throwBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 말 선택 영역 (폭 520px에 맞춰 살짝 확장)
        JPanel pieceSelectPanel = new JPanel();
        pieceSelectPanel.setPreferredSize(new Dimension(520, 180));
        pieceSelectPanel.setBackground(new Color(250, 245, 230));

        // 오른쪽 패널 (폭 560px으로 넉넉히)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(560, 700));      // ★ 폭 확장
        rightPanel.setBackground(new Color(240, 240, 245));

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(stick);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(throwBtn);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(pieceSelectPanel);
        rightPanel.add(Box.createVerticalGlue());

        add(rightPanel, BorderLayout.EAST);

        /* 3) 하단 : 상태 텍스트 */
        StatusPanel status = new StatusPanel();
        status.setTurn("RED");
        status.setScore(1, 0);
        add(status, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
