package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class BoardGeometryPentagon {

    private static final int BASE = 650;

    // 게임 로직용 재정렬된 슬롯 배열 (0번부터 시작해서 시계 반대방향 외곽 우선)
    public static final Point[] SLOT = new Point[36];


    // 슬롯 좌표들 (중심, 꼭짓점, 외곽, 보조점 총 36개)
    public static final Point[] T_SLOT = new Point[36];
    private static final List<Point> RAW = new ArrayList<>();

    static {
        // 중심점
        Point center = new Point(BASE / 2, BASE / 2);
        RAW.add(center); // index 0

        int sides = 5;
        int radius = 250;
        Point[] corners = new Point[sides];

        // 꼭짓점
        for (int i = 0; i < sides; i++) {
            double angle = Math.toRadians(-90 + i * 360.0 / sides);
            int x = (int) (center.x + radius * Math.cos(angle));
            int y = (int) (center.y + radius * Math.sin(angle));
            corners[i] = new Point(x, y);
        }

        // 꼭짓점 및 각 변마다 4개씩 점 추가 (총 5x5 = 25)
        for (int i = 0; i < sides; i++) {
            Point start = corners[i];
            Point end = corners[(i + 1) % sides];
            RAW.add(start); // 꼭짓점

            for (int j = 1; j <= 4; j++) {
                double t = j / 5.0;
                int x = (int) (start.x + t * (end.x - start.x));
                int y = (int) (start.y + t * (end.y - start.y));
                RAW.add(new Point(x, y));
            }
        }

        // 중심과 꼭짓점 사이에 2개 보조 점씩 (총 5x2 = 10)
        for (Point corner : corners) {
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                int x = (int) (center.x + t * (corner.x - center.x));
                int y = (int) (center.y + t * (corner.y - center.y));
                RAW.add(new Point(x, y));
            }
        }

        // 리스트 → 배열로 변환
        for (int i = 0; i < RAW.size(); i++) {
            T_SLOT[i] = RAW.get(i);
        }

        // 외곽 순서 지정
        int[] OUTER_ORDER = {
                11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1,
                25, 24, 23, 22, 21, 20, 19, 18, 17,
                16, 15, 14, 13, 12
        };

        // 외곽 점들을 원하는 순서로 GAME_SLOT에 배치
        for (int i = 0; i < 25; i++) {
            SLOT[i] = T_SLOT[OUTER_ORDER[i]];
        }

        SLOT[25] = T_SLOT[0]; // 중심
        // 중심에서 꼭짓점으로 향하는 보조점 10개: SLOT[26]~SLOT[35] → GAME_SLOT[26]~[35]
        for (int i = 0; i < 10; i++) {
            SLOT[26 + i] = T_SLOT[26 + i];

        }
    }

    private BoardGeometryPentagon() {}

    public static void scaleToPanel(Dimension panel) {
        double scale = Math.min(panel.width, panel.height) / (double) BASE;
        int offsetX = (panel.width - (int)(BASE * scale)) / 2;
        int offsetY = (panel.height - (int)(BASE * scale)) / 2;

        for (int i = 0; i < T_SLOT.length; i++) {
            T_SLOT[i].x = (int) Math.round(RAW.get(i).x * scale) + offsetX;
            T_SLOT[i].y = (int) Math.round(RAW.get(i).y * scale) + offsetY;
        }
    }

    public static int slotAt(int x, int y) {
        for (int i = 0; i < T_SLOT.length; i++) {
            if (T_SLOT[i].distanceSq(x, y) < 18 * 18) return i;
        }
        return -1;
    }

    public static Point offset(int slot, int order) {
        Point p = T_SLOT[slot];
        int off = 10;

        return switch (order) {
            case 1 -> new Point(p.x + off, p.y);
            case 2 -> new Point(p.x,       p.y + off);
            case 3 -> new Point(p.x - off, p.y);
            case 4 -> new Point(p.x,       p.y - off);
            case 5 -> new Point(p.x + off, p.y + off);
            case 6 -> new Point(p.x - off, p.y + off);
            case 7 -> new Point(p.x + off, p.y - off);
            case 8 -> new Point(p.x - off, p.y - off);
            default -> p;
        };
    }


}
