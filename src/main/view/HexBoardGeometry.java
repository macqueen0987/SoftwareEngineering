package main.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class HexBoardGeometry {
    private static final int BASE = 550;
    public static final int SLOT_COUNT = 43;
    private static final Point[] RAW   = new Point[SLOT_COUNT];
    public  static final Point[] SLOT  = new Point[SLOT_COUNT];
    public  static final int[][]  EDGE;

    static {
        // 1) RAW 좌표 계산
        int cx = BASE/2, cy = BASE/2;
        int R  = 200;            // 외곽 반지름
        int idx = 0;

        // 1-1) 6개 꼭짓점 구하기 (0=왼쪽, 1=좌하, 2=우하, 3=오른쪽, 4=우상, 5=좌상)
        Point[] verts = new Point[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians((300 + i*60));
            verts[i] = new Point(
                    (int)(cx + R * Math.cos(angle)),
                    (int)(cy - R * Math.sin(angle))
            );
        }
        // 1-2) 외곽 슬롯 30칸: 각 변마다 5칸 (꼭짓점 포함)
        for (int side = 0; side < 6; side++) {
            Point a = verts[side];
            Point b = verts[(side + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                RAW[idx++] = new Point(
                        (int)(a.x * (1 - t) + b.x * t),
                        (int)(a.y * (1 - t) + b.y * t)
                );
            }
        }
        // idx == 30

        // 1-4) 중심 슬롯 (30번)
        RAW[idx++] = new Point(cx, cy);

        // 1-3) 내부 슬롯 12칸: 중심→꼭짓점 방향으로 2칸 (총 6방향×2)
        for (int i = 0; i < 6; i++) {
            Point v = verts[(i + 1) % 6];
            if(i < 3){
                for (int j = 2; j >= 1; j--) {
                    double t = j / 3.0;
                    RAW[idx++] = new Point(
                           (int)(cx * (1 - t) + v.x * t),
                           (int)(cy * (1 - t) + v.y * t)
                    );
                }
            }
            else{
                for (int j = 1; j <= 2; j++) {
                    double t = j / 3.0;
                    RAW[idx++] = new Point(
                            (int)(cx * (1 - t) + v.x * t),
                            (int)(cy * (1 - t) + v.y * t)
                    );
                }
            }
        }
        // idx == 42

        if (idx != SLOT_COUNT)
            throw new IllegalStateException("슬롯 개수 불일치: " + idx);

        // 2) RAW → SLOT 복사
        for (int i = 0; i < SLOT_COUNT; i++){
            SLOT[i] = new Point(RAW[i]);
        }

        // 3) EDGE 생성 (동적 리스트 → 배열)
        List<int[]> edges = new ArrayList<>();

        // 3-1) 외곽 순환 연결 0→1→…→29→0
        for (int i = 0; i < 30; i++)
            edges.add(new int[]{i, (i + 1) % 30});

        // 3-2) 가지 연결 (42→30→31→0, 42→32→33→5, …)
        // internal1 = 30 + i*2, internal2 = internal1+1, vertex = i*5
        for (int i = 1; i <= 6; i++) {
            int internal1 = 30 + i * 2;
            int internal2 = internal1 - 1;
            int vertex    = (i * 5) % 30;
            edges.add(new int[]{30, internal1});
            edges.add(new int[]{internal1, internal2});
            edges.add(new int[]{internal2, vertex});
        }

        EDGE = new int[edges.size()][2];
        for (int i = 0; i < edges.size(); i++){
            EDGE[i] = edges.get(i);
        }
    }

    /** 패널 크기에 맞춰 SLOT 좌표 스케일 & 중앙 정렬 */
    public static void scaleToPanel(Dimension panel) {
        double scale = Math.min(panel.width, panel.height) / (double) BASE;
        int offX = (panel.width  - (int)(BASE * scale)) / 2;
        int offY = (panel.height - (int)(BASE * scale)) / 2;
        for (int i = 0; i < SLOT_COUNT; i++) {
            SLOT[i].x = (int)Math.round(RAW[i].x * scale) + offX;
            SLOT[i].y = (int)Math.round(RAW[i].y * scale) + offY;
        }
    }

    /** (x,y) 클릭 → 슬롯 인덱스 반환, 없으면 -1 */
    public static int slotAt(int x, int y) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (SLOT[i].distanceSq(x, y) < 18 * 18) return i;
        }
        return -1;
    }

    /** 말 겹침 오프셋 계산 */
    public static Point offset(int slot, int order) {
        Point p = SLOT[slot];
        int d = 10;
        return switch (order) {
            case 1 -> new Point(p.x + d, p.y);
            case 2 -> new Point(p.x, p.y + d);
            case 3 -> new Point(p.x - d, p.y);
            case 4 -> new Point(p.x, p.y - d);
            case 5 -> new Point(p.x + d, p.y + d);
            case 6 -> new Point(p.x - d, p.y + d);
            case 7 -> new Point(p.x + d, p.y - d);
            case 8 -> new Point(p.x - d, p.y - d);
            default -> p;
        };
    }
}
