package main.view;

import java.awt.*;

public final class BoardGeometry {

    /* 기준 보드 크기(그림 원본) */
    private static final int BASE = 550;

    /* 슬롯별 절대 좌표 (X, Y) – 그림과 동일 */
    private static final Point[] RAW = {
        new Point(500,500), new Point(500,420), new Point(500,340), new Point(500,260), new Point(500,180), new Point(500,100), //0-5
        new Point(420,100), new Point(340,100), new Point(260,100), new Point(180,100), new Point(100,100), //6-10
        new Point(100,180), new Point(100,260), new Point(100,340), new Point(100,420), new Point(100,500), //11 - 15
        new Point(180,500), new Point(260,500), new Point(340,500), new Point(420,500), //16-19
        /* 대각 (우상→중앙) */ new Point(300,300), new Point(420,180), new Point(360,240), //20-22
        /* 대각 (좌상→중앙) */ new Point(180,180), new Point(240,240), //23-24
        /* 대각 (중앙→좌하) */ new Point(240,360), new Point(180,420), //25-26
        /* 대각 (중앙→우하) */ new Point(360,360), new Point(420,420)   //27-28
    };
    public static int[][] EDGE = {
        // 외곽 0 ~ 19
        {0,1},{1,2},{2,3},{3,4},{4,5},{5,6},{6,7},{7,8},{8,9},{9,10},
        {10,11},{11,12},{12,13},{13,14},{14,15},{15,16},{16,17},{17,18},{18,19},{19,0},
        // 대각선 20 ~ 28 중심 연결
        {5,21},{21,22},{22,20},
        {10,23},{23,24},{24,20},
        {20,25},{25,26},{26,15},
        {20,27},{27,28},{28,0}
    };
    /** 스케일링 된 최종 좌표 */
    public static final Point[] SLOT = new Point[29];

    static {
        for (int i = 0; i < RAW.length; i++) SLOT[i] = new Point(RAW[i]);
    }

    private BoardGeometry(){}

    /* -------- 정중앙 배치를 위해 패널 크기에 맞춰 한번만 스케일/이동 -------- */
    public static void scaleToPanel(Dimension panel){
        double scale = Math.min(panel.width, panel.height) / (double) BASE;
        int offsetX  = (panel.width  - (int)(BASE * scale)) / 2;
        int offsetY  = (panel.height - (int)(BASE * scale)) / 2;

        for (int i = 0; i < SLOT.length; i++){
            SLOT[i].x = (int)Math.round(RAW[i].x * scale) + offsetX;
            SLOT[i].y = (int)Math.round(RAW[i].y * scale) + offsetY;
        }
    }

    /* 클릭 좌표 → 슬롯 번호 (거리 < 18px) */
    public static int slotAt(int x, int y){
        for (int i = 0; i < SLOT.length; i++)
            if (SLOT[i].distanceSq(x, y) < 18*18) return i;
        return -1;
    }

    /* 말 겹침 오프셋 */
    public static Point offset(int slot, int order) {
        Point p = SLOT[slot];
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
