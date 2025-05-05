package main.view;

import main.model.GameConfig;

import javax.swing.*;
import java.awt.*;

/**
 * 팀별 “남은 말” 목록을 버튼/아이콘으로 보여주는 패널
 *  ─ GameConfig 를 받아 동적으로 생성
 */
public class PieceSelectPanel extends JPanel {

    /** 팀 색 순서 미리 정의 – 팀 수에 따라 앞에서부터 사용 */
    private static final String[] COLORS = {"red", "blue", "yellow", "green"};

    public PieceSelectPanel(GameConfig cfg) {
        int teams  = cfg.teamCount();
        int pieces = cfg.piecePerTeam();

        // 1) 전체 패널 박스레이아웃 + 패딩
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(250, 245, 230));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // 상,좌,하,우

        // 2) 토큰 그리드
        JPanel grid = new JPanel(new GridLayout(teams, pieces, 10, 10));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // 그리드 아래 여백

        for (int t = 0; t < teams; t++) {
            String color = COLORS[t];                          // red / blue / …
            ImageIcon icon = ResourceLoader.piece(color, 40);  // 40×40으로 축소

            for (int p = 0; p < pieces; p++) {
                JLabel lbl = new JLabel(icon);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                grid.add(lbl);
            }
        }

        add(grid);
        // 버튼 아래 남는 공간 밀어내기
        add(Box.createVerticalGlue());

        // 3) ‘새로운 말 꺼내기’ 버튼
        JButton newPieceBtn = new JButton("새로운 말 꺼내기");
        newPieceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPieceBtn.setMaximumSize(new Dimension(200, 60)); // 최대 크기 지정
        newPieceBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        newPieceBtn.setBackground(new Color(200, 220, 240));
        add(newPieceBtn);

    }
}