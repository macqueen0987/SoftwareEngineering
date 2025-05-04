package main.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

/** 윷판만 그리는 Swing 패널 (점, 선, 말, 하이라이트까지) */
public class BoardPanel extends JPanel {

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
    public BoardPanel() {
        setPreferredSize(new Dimension(650, 650));
        setBackground(new Color(245, 238, 220));

        /* 패널 크기 확정 → 슬롯 좌표 스케일링 */
        BoardGeometry.scaleToPanel(getPreferredSize());

        /* 리사이즈 시에도 다시 스케일링 */
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                BoardGeometry.scaleToPanel(getSize());
                repaint();
            }
        });

        /* 슬롯 클릭 감지 */
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int idx = BoardGeometry.slotAt(e.getX(), e.getY());
                if (idx != -1) { highlight = idx; repaint(); }
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

        drawLines(g2);         // 외곽 + 대각선
        drawSlots(g2);         // 점 + 번호
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
        Set<Integer> specialSlots = Set.of(0, 5, 10, 15, 20);

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
            Point p  = BoardGeometry.offset(pc.slot(), pc.order());
            Image im = ResourceLoader.piece(pc.color()).getImage();
            int sz   = 40;
            g.drawImage(im, p.x - sz / 2, p.y - sz / 2, sz, sz, null);
        }
    }

    private void drawHighlight(Graphics2D g) {
        if (highlight == null) return;
    
        Point p = BoardGeometry.SLOT[highlight];
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(p.x - 12, p.y - 12, 24, 24);
    }
    
}
