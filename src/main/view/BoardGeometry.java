package main.view;

import javafx.geometry.Point2D;

/**
 * JavaFX 버전의 보드 기하 정보 클래스
 * Swing의 java.awt.Point, Dimension을 javafx.geometry.Point2D 및 pane 크기로 대체
 */
public final class BoardGeometry {

    /**
     * 기준 보드 크기 (원본 이미지 크기)
     */
    private static final int BASE = 550;

    /**
     * 슬롯별 절대 좌표 (RAW) – 원본 좌표값
     */
    private static final Point2D[] RAW = {
            new Point2D(500, 500), new Point2D(500, 420), new Point2D(500, 340), new Point2D(500, 260), new Point2D(500, 180), new Point2D(500, 100),
            new Point2D(420, 100), new Point2D(340, 100), new Point2D(260, 100), new Point2D(180, 100), new Point2D(100, 100),
            new Point2D(100, 180), new Point2D(100, 260), new Point2D(100, 340), new Point2D(100, 420), new Point2D(100, 500),
            new Point2D(180, 500), new Point2D(260, 500), new Point2D(340, 500), new Point2D(420, 500),
            /* 대각 (우상→중앙) */ new Point2D(300, 300), new Point2D(420, 180), new Point2D(360, 240),
            /* 대각 (좌상→중앙) */ new Point2D(180, 180), new Point2D(240, 240),
            /* 대각 (중앙→좌하) */ new Point2D(240, 360), new Point2D(180, 420),
            /* 대각 (중앙→우하) */ new Point2D(360, 360), new Point2D(420, 420)
    };

    /**
     * 슬롯 간 연결 정보 (EDGE)
     */
    public static final int[][] EDGE = {
            // 외곽 0 ~ 19
            {0,1},{1,2},{2,3},{3,4},{4,5},{5,6},{6,7},{7,8},{8,9},{9,10},
            {10,11},{11,12},{12,13},{13,14},{14,15},{15,16},{16,17},{17,18},{18,19},{19,0},
            // 대각선 중심 연결
            {5,21},{21,22},{22,20},
            {10,23},{23,24},{24,20},
            {20,25},{25,26},{26,15},
            {20,27},{27,28},{28,0}
    };

    /**
     * 스케일링 된 최종 좌표 (SLOT)
     */
    public static final Point2D[] SLOT = new Point2D[RAW.length];

    static {
        // 초기화: RAW 좌표를 복사
        for (int i = 0; i < RAW.length; i++) {
            SLOT[i] = new Point2D(RAW[i].getX(), RAW[i].getY());
        }
    }

    private BoardGeometry() {
        // 유틸리티 클래스: 인스턴스화 금지
    }

    /**
     * 패널 크기에 맞춰 한 번만 스케일 및 중앙 배치 수행
     * @param paneWidth  패널(또는 Scene) 폭
     * @param paneHeight 패널(또는 Scene) 높이
     */
    public static void scaleToPane(double paneWidth, double paneHeight) {
        double scale = Math.min(paneWidth, paneHeight) / BASE;
        double offsetX = (paneWidth - BASE * scale) / 2;
        double offsetY = (paneHeight - BASE * scale) / 2;
        for (int i = 0; i < RAW.length; i++) {
            double x = RAW[i].getX() * scale + offsetX;
            double y = RAW[i].getY() * scale + offsetY;
            SLOT[i] = new Point2D(x, y);
        }
    }

    /**
     * 클릭 좌표로부터 슬롯 인덱스 검색 (거리 < 18px)
     * @param x 클릭한 X 좌표
     * @param y 클릭한 Y 좌표
     * @return 슬롯 번호 (없으면 -1)
     */
    public static int slotAt(double x, double y) {
        for (int i = 0; i < SLOT.length; i++) {
            if (SLOT[i].distance(x, y) < 18) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 같은 슬롯에 여러 말이 겹칠 때 오프셋 좌표 반환
     * @param slot  슬롯 번호
     * @param order 말 순서 (1~8)
     * @return 오프셋된 Point2D
     */
    public static Point2D offset(int slot, int order) {
        Point2D p = SLOT[slot];
        double off = 10;
        switch (order) {
            case 1:  return new Point2D(p.getX() + off, p.getY());
            case 2:  return new Point2D(p.getX(),        p.getY() + off);
            case 3:  return new Point2D(p.getX() - off, p.getY());
            case 4:  return new Point2D(p.getX(),        p.getY() - off);
            case 5:  return new Point2D(p.getX() + off, p.getY() + off);
            case 6:  return new Point2D(p.getX() - off, p.getY() + off);
            case 7:  return new Point2D(p.getX() + off, p.getY() - off);
            case 8:  return new Point2D(p.getX() - off, p.getY() - off);
            default: return p;
        }
    }
}