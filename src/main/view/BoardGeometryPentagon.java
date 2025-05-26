package main.view;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX 버전의 5각형 보드 기하 정보 클래스
 */
public final class BoardGeometryPentagon {

    /** 기준 보드 크기 */
    private static final int BASE = 650;

    /** 원본 좌표를 저장하는 리스트 */
    private static final List<Point2D> RAW = new ArrayList<>();

    /** 패널에 맞춰 스케일 및 이동된 좌표 배열 */
    public static final Point2D[] T_SLOT = new Point2D[36];
    public static final Point2D[] T_SLOT_COPY = new Point2D[36];
    public static final Point2D[] SLOT = new Point2D[36];

    static {
        // 중심점
        Point2D center = new Point2D(BASE / 2.0, BASE / 2.0);

        // 5각형 꼭짓점 계산
        int sides = 5;
        double radius = 250;
        Point2D[] corners = new Point2D[sides];
        for (int i = 0; i < sides; i++) {
            double angle = Math.toRadians(54 - i * 360.0 / sides);
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            corners[i] = new Point2D(x, y);
        }

        // 외곽 꼭짓점 및 각 변마다 4개 보조점 추가
        for (int i = 0; i < sides; i++) {
            Point2D start = corners[i];
            Point2D end = corners[(i + 1) % sides];
            RAW.add(start);
            for (int j = 1; j <= 4; j++) {
                double t = j / 5.0;
                double ix = start.getX() + t * (end.getX() - start.getX());
                double iy = start.getY() + t * (end.getY() - start.getY());
                RAW.add(new Point2D(ix, iy));
            }
        }

        // 중앙점 추가
        RAW.add(center);

        // 중앙과 각 꼭짓점 사이 보조점 2개 추가
        for (int i = 0; i < sides; i++) {
            Point2D corner = corners[(i + 1) % sides];
            for (int j = 1; j <= 2; j++) {
                double t = (i <= 2 ? (3 - j) : j) / 3.0;
                double ix = center.getX() + t * (corner.getX() - center.getX());
                double iy = center.getY() + t * (corner.getY() - center.getY());
                RAW.add(new Point2D(ix, iy));
            }
        }

        // 배열에 RAW 복사
        for (int i = 0; i < RAW.size() && i < T_SLOT.length; i++) {
            T_SLOT[i] = RAW.get(i);
            T_SLOT_COPY[i] = RAW.get(i);
        }

        // 기본 게임 슬롯 순서로 복사
        System.arraycopy(T_SLOT, 0, SLOT, 0, SLOT.length);
    }

    private BoardGeometryPentagon() {
        // 유틸리티 클래스
    }

    /**
     * 패널 크기에 맞춰 스케일 및 중앙 배치
     */
    public static void scaleToPane(double paneWidth, double paneHeight) {
        double scale = Math.min(paneWidth, paneHeight) / BASE;
        double offsetX = (paneWidth - BASE * scale) / 2;
        double offsetY = (paneHeight - BASE * scale) / 2;
        for (int i = 0; i < RAW.size() && i < T_SLOT.length; i++) {
            double x = RAW.get(i).getX() * scale + offsetX;
            double y = RAW.get(i).getY() * scale + offsetY;
            T_SLOT[i] = new Point2D(x, y);
        }
        System.arraycopy(T_SLOT, 0, SLOT, 0, T_SLOT.length);
    }

    /**
     * 클릭 좌표로부터 슬롯 인덱스 검색
     */
    public static int slotAt(double x, double y) {
        for (int i = 0; i < T_SLOT.length; i++) {
            if (T_SLOT[i].distance(x, y) < 18) return i;
        }
        return -1;
    }

    /**
     * 동일 슬롯 내 말 겹침 오프셋 계산
     */
    public static Point2D offset(int slot, int order) {
        Point2D p = T_SLOT[slot];
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