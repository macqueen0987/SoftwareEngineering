package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

/** 윷판만 그리는 Swing 패널 (점, 선, 말, 하이라이트까지) */
public class BoardPanel extends JPanel {

    private String boardShape = "사각";

    /* ---------- 내부 DTO (일단 테스트용) ---------- */
    public record Piece(int slot, String color, int order){}   // order = 겹침 순서

    /* ---------- 상태 ---------- */
    private Integer highlight = null;                 // 클릭된 슬롯
    // private List<Piece> pieces = List.of(); // 빈 리스트로 초기화, 임시 데이터 지우고 할 것
    private List<Piece> pieces = List.of(             // 임시 데이터
            new Piece(0 , "RED"  ,0),
            new Piece(22, "BLUE" ,0),
            new Piece(22, "GREEN",1)
    );


    /* ---------- 생성자 ---------- */
    public BoardPanel(String shape) {
        this.boardShape = shape; // "사각" 또는 "오각"

        setPreferredSize(new Dimension(650, 650));
        setBackground(new Color(245, 238, 220));

        // 보드 모양에 따라 슬롯 좌표 스케일링
        if ("오각".equals(boardShape)) {
            BoardGeometryPentagon.scaleToPanel(getPreferredSize());
        } else {
            BoardGeometry.scaleToPanel(getPreferredSize());
        }

        // 리사이즈 시에도 다시 스케일링
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if ("오각".equals(boardShape)) {
                    BoardGeometryPentagon.scaleToPanel(getSize());
                } else {
                    BoardGeometry.scaleToPanel(getSize());
                }
                repaint();
            }
        });

        // 슬롯 클릭 감지
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx;
                if ("오각".equals(boardShape)) {
                    idx = BoardGeometryPentagon.slotAt(e.getX(), e.getY());
                } else {
                    idx = BoardGeometry.slotAt(e.getX(), e.getY());
                }

                if (idx != -1) {
                    highlight = idx;
                    repaint();
                }
            }
        });
    }


    /* ---------- 외부에서 말 목록 갱신 ---------- */
    public void setPieces(List<Piece> list) {
        pieces = list; repaint();
    }

    /* ---------- 그리기 ---------- */
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        switch (boardShape) {
            case "사각" -> {
                drawLines(g2);
                drawSlots(g2);
            }
            case "오각" -> {
                drawPentagonBoard(g2);
            }
            // case "육각" -> drawHexagonBoard(g2);
        }

        drawPieces(g2);        // 말 아이콘
        drawHighlight(g2);

        g2.dispose();
    }

    /* ---------- 개별 그리기 메서드 ---------- */
    private void drawLines(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2f));
        
        for (int[] e : BoardGeometry.EDGE) {
            Point a = BoardGeometry.SLOT[e[0]];
            Point b = BoardGeometry.SLOT[e[1]];
            g.drawLine(a.x, a.y, b.x, b.y);
        }
        
    }

    private void drawSlots(Graphics2D g) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));

        // 강조할 슬롯 번호 목록 (꼭짓점 및 중앙)
        Set<Integer> specialSlots = Set.of(0, 5, 10, 15, 22);

        for (int i = 0; i < BoardGeometry.SLOT.length; i++) {
            Point p = BoardGeometry.SLOT[i];
            int R = specialSlots.contains(i) ? 30 : 20; // 슬롯 크기
        
            // 1. 슬롯 채우기 (연갈색)
            g.setColor(new Color(163, 115, 33));
            g.fillOval(p.x - R, p.y - R, 2 * R, 2 * R);
        
            // 2. 슬롯 테두리 (진한 갈색)
            g.setColor(new Color(100, 70, 20));
            g.setStroke(new BasicStroke(2f));
            g.drawOval(p.x - R, p.y - R, 2 * R, 2 * R);
        
            // 3. 번호 (회색)
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.valueOf(i), p.x - R - 7, p.y - R - 3);
        }
        
    }

    private void drawPieces(Graphics2D g) {
        for (Piece pc : pieces) {
            Point p;

            // 보드 모양에 따라 위치 계산
            if ("오각".equals(boardShape)) {
                p = BoardGeometryPentagon.offset(pc.slot(), pc.order());
            } else {
                p = BoardGeometry.offset(pc.slot(), pc.order()); // 사각형 또는 기본 보드
            }

            Image im = ResourceLoader.piece(pc.color()).getImage();
            int sz = 40;
            g.drawImage(im, p.x - sz / 2, p.y - sz / 2, sz, sz, null);
        }
    }

    private void drawHighlight(Graphics2D g) {
        if (highlight == null) return;

        Point p;

        // 보드 모양에 따라 다른 SLOT 사용
        if ("오각".equals(boardShape)) {
            p = BoardGeometryPentagon.T_SLOT[highlight];
        } else {
            p = BoardGeometry.SLOT[highlight];
        }

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(p.x - 12, p.y - 12, 24, 24);
    }


    private void drawPentagonBoard(Graphics2D g2) {
        Point[] slots = BoardGeometryPentagon.T_SLOT;

        // 1. 외곽선 그리기 (꼭짓점들 간)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        int[] cornerIndices = {1, 6, 11, 16, 21};
        for (int i = 0; i < cornerIndices.length; i++) {
            Point a = slots[cornerIndices[i]];
            Point b = slots[cornerIndices[(i + 1) % cornerIndices.length]];
            g2.drawLine(a.x, a.y, b.x, b.y);
        }

        // 2. 중심선 연결
        g2.setColor(Color.GRAY);
        Point center = slots[0];
        for (int idx : cornerIndices) {
            Point corner = slots[idx];
            g2.drawLine(center.x, center.y, corner.x, corner.y);
        }

        // 3. 슬롯 노드 그리기
        for (int i = 0; i < slots.length; i++) {
            Point p = slots[i];
            int size = 35;
            if (i == 0 || isCornerIndex(i)) size = 45;

            // 슬롯 채우기
            g2.setColor(new Color(163, 115, 33)); // 연갈색
            g2.fillOval(p.x - size / 2, p.y - size / 2, size, size);

            // 테두리
            g2.setColor(new Color(100, 70, 20));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(p.x - size / 2, p.y - size / 2, size, size);
        }
        // 4. 슬롯 번호 표시
        g2.setColor(Color.DARK_GRAY); // 번호는 잘 보이도록 색 다르게
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        for (int i = 0; i < BoardGeometryPentagon.SLOT.length; i++) {
            Point p = BoardGeometryPentagon.SLOT[i];
            String num = String.valueOf(i);  // 0번부터 표시
            g2.drawString(num, p.x + 8, p.y - 8);
        }

    }

    private boolean isCornerIndex(int i) {
        // 중심 제외하고 꼭짓점 인덱스는 1,6,11,16,21
        return i == 1 || i == 6 || i == 11 || i == 16 || i == 21;
    }

}
