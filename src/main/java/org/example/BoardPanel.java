package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Set;

public class BoardPanel extends JPanel {

    private final int NODE_BIG = 24;
    private final int NODE_SMALL = 14;
    private final int PADDING = 60;
    private final int SIZE = 400;

    private final Set<Point> nodeSet = new HashSet<>();

    public BoardPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        nodeSet.clear();

        int step = SIZE / 5;

        // 외곽 노드 (시계방향)
        for (int i = 0; i < 6; i++) nodeSet.add(new Point(PADDING + i * step, PADDING));                    // 상단
        for (int i = 1; i < 6; i++) nodeSet.add(new Point(PADDING + 5 * step, PADDING + i * step));        // 우측
        for (int i = 4; i >= 0; i--) nodeSet.add(new Point(PADDING + i * step, PADDING + 5 * step));       // 하단
        for (int i = 4; i >= 1; i--) nodeSet.add(new Point(PADDING, PADDING + i * step));                  // 좌측

        // 중앙 큰 노드
        Point center = new Point(PADDING + SIZE / 2, PADDING + SIZE / 2);
        nodeSet.add(center);

        // 대각선 안쪽 작은 노드 (각 방향마다 1/3, 2/3 지점에 노드 추가)
        addDiagonalNodes(PADDING, PADDING, center);                           // ↘ (좌상 → 중앙)
        addDiagonalNodes(PADDING + 5 * step, PADDING, center);               // ↙ (우상 → 중앙)
        addDiagonalNodes(PADDING, PADDING + 5 * step, center);               // ↗ (좌하 → 중앙)
        addDiagonalNodes(PADDING + 5 * step, PADDING + 5 * step, center);    // ↖ (우하 → 중앙)

        // ✅ 중앙과 꼭짓점 큰 노드 연결선
        Point[] bigCorners = {
                new Point(PADDING, PADDING),
                new Point(PADDING + 5 * step, PADDING),
                new Point(PADDING, PADDING + 5 * step),
                new Point(PADDING + 5 * step, PADDING + 5 * step)
        };

        // 2. 모서리끼리 연결 (사각형 테두리)
        g2.drawLine(bigCorners[0].x, bigCorners[0].y, bigCorners[1].x, bigCorners[1].y); // 좌상-우상
        g2.drawLine(bigCorners[1].x, bigCorners[1].y, bigCorners[3].x, bigCorners[3].y); // 우상-우하
        g2.drawLine(bigCorners[3].x, bigCorners[3].y, bigCorners[2].x, bigCorners[2].y); // 우하-좌하
        g2.drawLine(bigCorners[2].x, bigCorners[2].y, bigCorners[0].x, bigCorners[0].y); // 좌하-좌상



        g2.setColor(Color.GRAY);
        for (Point corner : bigCorners) {
            g2.drawLine(center.x, center.y, corner.x, corner.y);
        }

        // 노드 그리기
        for (Point p : nodeSet) {
            int size = isBigNode(p, step) ? NODE_BIG : NODE_SMALL;
            g2.setColor(Color.BLACK);
            g2.fill(new Ellipse2D.Double(p.x - size / 2.0, p.y - size / 2.0, size, size));
        }

        // 출발 텍스트
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        g2.drawString("출발", PADDING + 5 * step - 10, PADDING + 5 * step + 25);
    }

    // 대각선 방향으로 내부 노드 (1/3, 2/3 지점) 추가
    private void addDiagonalNodes(Point start, Point end) {
        int x1 = start.x + (end.x - start.x) / 3;
        int y1 = start.y + (end.y - start.y) / 3;

        int x2 = start.x + 2 * (end.x - start.x) / 3;
        int y2 = start.y + 2 * (end.y - start.y) / 3;

        nodeSet.add(new Point(x1, y1));
        nodeSet.add(new Point(x2, y2));
    }

    private void addDiagonalNodes(int sx, int sy, Point center) {
        addDiagonalNodes(new Point(sx, sy), center);
    }

    private boolean isBigNode(Point p, int step) {
        Point center = new Point(PADDING + SIZE / 2, PADDING + SIZE / 2);

        Point[] bigNodes = {
                new Point(PADDING, PADDING),
                new Point(PADDING + 5 * step, PADDING),
                new Point(PADDING, PADDING + 5 * step),
                new Point(PADDING + 5 * step, PADDING + 5 * step),
                center
        };
        for (Point bp : bigNodes) {
            if (bp.equals(p)) return true;
        }
        return false;
    }
}