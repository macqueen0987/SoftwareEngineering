package main.view;

import main.model.GameConfig;

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
    private final JButton newPieceBtn;
    private List<List<JLabel>> pieceLabels = new ArrayList<>();
    private final String[] teamColors;

    public PieceSelectPanel(GameConfig cfg, String[] colors) {
        this.teamColors = colors;
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
            String color = colors[t];                          // red / blue / …
            ImageIcon icon = ResourceLoader.piece(color, 40);  // 40×40으로 축소

            List<JLabel> teamPieces = new ArrayList<>();
            for (int p = 0; p < pieces; p++) {
                JLabel lbl = new JLabel(icon);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                grid.add(lbl);
                teamPieces.add(lbl);
            }
            pieceLabels.add(teamPieces);
        }

        add(grid);
        // 버튼 아래 남는 공간 밀어내기
        add(Box.createVerticalGlue());

        // 3) ‘새로운 말 꺼내기’ 버튼
        this.newPieceBtn = new JButton("새로운 말 꺼내기");
        newPieceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPieceBtn.setMaximumSize(new Dimension(200, 60)); // 최대 크기 지정
        newPieceBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        newPieceBtn.setBackground(new Color(200, 220, 240));
        add(newPieceBtn); // <- 이거 버튼이 왜 여기서 생성되나요? 나머지 버튼들을 모두 MainFrame에서 생성하는데? 아무튼 작동 되니 그냥 둡니다.
    }

    public JButton getNewPieceButton() {
        return newPieceBtn;
    }

    public void usePiece(String teamColor) {
        System.out.println("usePiece 호출됨: " + teamColor);
        for (int i = 0; i < pieceLabels.size(); i++) {
            if (teamColors[i].equals(teamColor)) {
                List<JLabel> teamList = pieceLabels.get(i);

                for (JLabel lbl : teamList) {
                    if (lbl.isVisible()) {
                        lbl.setVisible(false);
                        revalidate(); // UI 갱신
                        repaint();
                        return;
                    }
                }

                System.out.println("해당 팀에 표시된 말이 없습니다: " + teamColor);
                return;
            }
        }

        System.out.println("해당하는 teamColor를 찾지 못함: " + teamColor);
    }


    public void returnPiece(String teamColor) {
        System.out.println("returnPiece 호출됨: " + teamColor);
        for (int i = 0; i < teamColors.length; i++) {
            if (teamColors[i].equals(teamColor)) {
                List<JLabel> teamList = pieceLabels.get(i);

                for (JLabel lbl : teamList) {
                    if (!lbl.isVisible()) {
                        lbl.setVisible(true);
                        revalidate();
                        repaint();
                        return;
                    }
                }

                System.out.println("해당 팀에 숨겨진 말이 없습니다: " + teamColor);
                return;
            }
        }

        System.out.println("해당하는 teamColor를 찾지 못함: " + teamColor);
    }



}