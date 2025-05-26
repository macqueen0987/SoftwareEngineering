package main.view;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX 버전의 HexBoardGeometry
 */
public final class HexBoardGeometry {
    private static final int BASE = 550;
    public static final int SLOT_COUNT = 43;
    private static final List<Point2D> RAW = new ArrayList<>();
    public static final Point2D[] SLOT = new Point2D[SLOT_COUNT];
    public static final int[][] EDGE;

    static {
        // 1) RAW 좌표 계산
        int cx = BASE / 2, cy = BASE / 2;
        int R = 200;
        Point2D[] verts = new Point2D[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(300 + i * 60);
            double x = cx + R * Math.cos(angle);
            double y = cy - R * Math.sin(angle);
            verts[i] = new Point2D(x, y);
        }
        // 2) 외곽 슬롯 30칸: 각 변마다 5칸
        for (int side = 0; side < 6; side++) {
            Point2D a = verts[side];
            Point2D b = verts[(side + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                double x = a.getX() * (1 - t) + b.getX() * t;
                double y = a.getY() * (1 - t) + b.getY() * t;
                RAW.add(new Point2D(x, y));
            }
        }
        // 3) 중심 슬롯
        RAW.add(new Point2D(cx, cy));
        // 4) 내부 슬롯 12칸: 중심→꼭짓점 방향으로 2칸
        for (int i = 0; i < 6; i++) {
            Point2D v = verts[(i + 1) % 6];
            if (i < 3) {
                for (int j = 2; j >= 1; j--) {
                    double t = j / 3.0;
                    double x = cx * (1 - t) + v.getX() * t;
                    double y = cy * (1 - t) + v.getY() * t;
                    RAW.add(new Point2D(x, y));
                }
            } else {
                for (int j = 1; j <= 2; j++) {
                    double t = j / 3.0;
                    double x = cx * (1 - t) + v.getX() * t;
                    double y = cy * (1 - t) + v.getY() * t;
                    RAW.add(new Point2D(x, y));
                }
            }
        }
        // RAW → SLOT 복사
        for (int i = 0; i < SLOT_COUNT; i++) {
            SLOT[i] = RAW.get(i);
        }
        // EDGE 생성
        List<int[]> edges = new ArrayList<>();
        // 외곽 순환 연결
        for (int i = 0; i < 30; i++) {
            edges.add(new int[]{i, (i + 1) % 30});
        }
        // 내부 연결
        for (int i = 1; i <= 6; i++) {
            int internal1 = 30 + i * 2;
            int internal2 = internal1 - 1;
            int vertex = (i * 5) % 30;
            edges.add(new int[]{30, internal1});
            edges.add(new int[]{internal1, internal2});
            edges.add(new int[]{internal2, vertex});
        }
        EDGE = new int[edges.size()][2];
        for (int i = 0; i < edges.size(); i++) {
            EDGE[i] = edges.get(i);
        }
    }

    private HexBoardGeometry() { /* 유틸리티 클래스 */ }

    /**
     * 패널 크기에 맞춰 스케일 및 중앙 정렬
     */
    public static void scaleToPane(double paneWidth, double paneHeight) {
        double scale = Math.min(paneWidth, paneHeight) / BASE;
        double offX = (paneWidth - BASE * scale) / 2;
        double offY = (paneHeight - BASE * scale) / 2;
        for (int i = 0; i < SLOT_COUNT; i++) {
            double x = RAW.get(i).getX() * scale + offX;
            double y = RAW.get(i).getY() * scale + offY;
            SLOT[i] = new Point2D(x, y);
        }
    }

    /**
     * 클릭 좌표로부터 슬롯 인덱스 검색
     */
    public static int slotAt(double x, double y) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (SLOT[i].distance(x, y) < 18) return i;
        }
        return -1;
    }

    /**
     * 같은 슬롯 내 말 겹침 오프셋 계산
     */
    public static Point2D offset(int slot, int order) {
        Point2D p = SLOT[slot];
        double d = 10;
        return switch (order) {
            case 1 -> new Point2D(p.getX() + d, p.getY());
            case 2 -> new Point2D(p.getX(), p.getY() + d);
            case 3 -> new Point2D(p.getX() - d, p.getY());
            case 4 -> new Point2D(p.getX(), p.getY() - d);
            case 5 -> new Point2D(p.getX() + d, p.getY() + d);
            case 6 -> new Point2D(p.getX() - d, p.getY() + d);
            case 7 -> new Point2D(p.getX() + d, p.getY() - d);
            case 8 -> new Point2D(p.getX() - d, p.getY() - d);
            default -> p;
        };
    }
}