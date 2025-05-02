package view;

import model.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 팀별 “남은 말” 목록을 버튼/아이콘으로 보여주는 패널
 *  ─ GameConfig 를 받아 동적으로 생성
 */
public class PieceSelectPanel extends JPanel {

    /** 팀 색 순서 미리 정의 – 팀 수에 따라 앞에서부터 사용 */
    private static final String[] COLORS = {"red", "blue", "yellow", "green"};

    /** 생성자: config 로 팀 수·말 개수를 받아 말 아이콘들을 배치 */
    public PieceSelectPanel(GameConfig cfg) {

        int teams  = cfg.teamCount();
        int pieces = cfg.piecePerTeam();

        /* 세로로 팀, 가로로 말 / GridLayout(행=팀, 열= pieces) */
        setLayout(new GridLayout(teams, pieces, 10, 10));
        setPreferredSize(new Dimension(520, 180));
        setBackground(new Color(250, 245, 230));

        /* 팀별 말 아이콘 생성 */
        for (int t = 0; t < teams; t++) {
            String color = COLORS[t];                          // red / blue / ...
            ImageIcon icon = ResourceLoader.piece(color, 40);  // 40×40으로 축소
        
            for (int p = 0; p < pieces; p++) {
                JLabel lbl = new JLabel(icon);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                add(lbl);
            }
        }
    }
}
