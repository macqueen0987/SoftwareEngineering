package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

/** 육각형 윷판을 그리는 Swing 패널 */
public class HexBoardPanel extends JPanel {

    public record Piece(int slot, String color, int order) {}

    private Integer highlight = null;

    // 테스트용 말: 외곽 0번, 꼭짓점 25번에 빨강/파랑
    private List<Piece> pieces = List.of(
            new Piece(0 , "RED",   0),
            new Piece(25, "BLUE",  0),
            new Piece(25, "GREEN", 1)
    );

    public HexBoardPanel() {
        setPreferredSize(new Dimension(650, 650));
        setBackground(new Color(245, 238, 220));

        HexBoardGeometry.scaleToPanel(getPreferredSize());

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                HexBoardGeometry.scaleToPanel(getSize());
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int idx = HexBoardGeometry.slotAt(e.getX(), e.getY());
                if (idx != -1) {
                    highlight = idx;
                    repaint();
                }
            }
        });
    }

    public void setPieces(List<Piece> list) {
        pieces = list;
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        drawLines(g2);
        drawSlots(g2);
        drawPieces(g2);
        drawHighlight(g2);
        g2.dispose();
    }

    private void drawLines(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2f));
        for (var e : HexBoardGeometry.EDGE) {
            Point a = HexBoardGeometry.SLOT[e[0]];
            Point b = HexBoardGeometry.SLOT[e[1]];
            g.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    private void drawSlots(Graphics2D g) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        // 강조할 슬롯: 외곽 꼭짓점(0,5,10,15,20,25) + 중심(42)
        Set<Integer> big = Set.of(0, 5, 10, 15, 20, 25, 42);

        for (int i = 0; i < HexBoardGeometry.SLOT.length; i++) {
            Point p = HexBoardGeometry.SLOT[i];
            int R = big.contains(i) ? 25 : 18;

            // 1) 원 채우기
            g.setColor(new Color(163, 115, 33));
            g.fillOval(p.x - R, p.y - R, 2 * R, 2 * R);

            // 2) 테두리
            g.setColor(new Color(100, 70, 20));
            g.setStroke(new BasicStroke(2f));
            g.drawOval(p.x - R, p.y - R, 2 * R, 2 * R);

            // 3) 번호 계산
            int label;
            if (i < 30) {
                // 외곽 슬롯은 반시계방향(아래→위) 증가가 아니라
                // 왼쪽 꼭짓점(0)부터 아래 방향으로 1,2,3... 증가하도록
                label = (30 - i) % 30;
            } else {
                // 내부(30~41)와 중심(42)은 원래 인덱스 그대로
                label = i;
            }

            // 4) 번호 그리기 (슬롯 바로 아래 중앙 정렬)
            String s = String.valueOf(label);
            FontMetrics fm = g.getFontMetrics();
            int tx = p.x - fm.stringWidth(s) / 2;
            int ty = p.y + R + fm.getAscent() + 2;
            g.setColor(Color.DARK_GRAY);
            g.drawString(s, tx, ty);
        }
    }


    private void drawPieces(Graphics2D g) {
        for (Piece pc : pieces) {
            Point p = HexBoardGeometry.offset(pc.slot(), pc.order());
            Image im = ResourceLoader.piece(pc.color()).getImage();
            g.drawImage(im, p.x - 20, p.y - 20, 40, 40, null);
        }
    }

    private void drawHighlight(Graphics2D g) {
        if (highlight == null) return;
        Point p = HexBoardGeometry.SLOT[highlight];
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(p.x - 12, p.y - 12, 24, 24);
    }
}
