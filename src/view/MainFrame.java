package view;

import javax.swing.*;
import javax.swing.border.LineBorder;

import model.GameConfig;

import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(GameConfig cfg) {
        setTitle("윷놀이 - 통합 레이아웃");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        System.out.println("선택한 보드: " + cfg.boardShape());
        System.out.println("참가 팀 수: " + cfg.teamCount());
        System.out.println("팀당 말 개수: " + cfg.piecePerTeam());

        /* 1) 왼쪽 : 보드 */
        JPanel board;
        if (cfg.boardShape().equals("육각")) {
            board = new HexBoardPanel();
        } else {
            board = new BoardPanel();
        }
        add(board, BorderLayout.CENTER);

        // StickPanel 생성
        StickPanel stick = new StickPanel();                       // (540 × 170)
        stick.setFaces(new boolean[]{true,false,true,true}, false);

        // 윷 던지기 버튼
        JButton throwBtn = new JButton("던지기");

        // 크기 설정
        throwBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        throwBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwBtn.setPreferredSize(new Dimension(200, 50));
        throwBtn.setMaximumSize(new Dimension(200, 50));  // ➤ 가로 고정!
        

        // 색상 설정 (배경 + 글자)
        throwBtn.setBackground(new Color(163, 115, 33)); 
        throwBtn.setForeground(Color.BLACK);              // 글자색
        

        // 버튼 배경을 확실히 보이게 설정
        throwBtn.setOpaque(true);
        throwBtn.setContentAreaFilled(true);
        throwBtn.setBorderPainted(false); // (선택) 테두리 없애기
        
        // 테두리 설정 (안 보이게 또는 둥글게)
        throwBtn.setBorder(new LineBorder(new Color(100, 70, 20), 2));
        throwBtn.setFocusPainted(false);

        // 말 선택 영역
        PieceSelectPanel piecePanel = new PieceSelectPanel(cfg);
        piecePanel.setPreferredSize(new Dimension(520, 180));
        piecePanel.setBackground(new Color(163, 131, 88));

        // 오른쪽 패널 (폭 560px으로 넉넉히)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(560, 700));      // ★ 폭 확장
        rightPanel.setBackground(new Color(210, 180, 140));

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(stick);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(throwBtn);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(piecePanel);
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
}
