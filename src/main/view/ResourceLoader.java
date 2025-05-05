package main.view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.Image;


/** /resources/img/ 아래 PNG를 캐싱해서 반환 */
public final class ResourceLoader {

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static final String DIR = "/img/";      // 클래스패스 기준 경로

    private static ImageIcon load(String file) {
        return CACHE.computeIfAbsent(file, f -> {
            var url = ResourceLoader.class.getResource(DIR + f);
            if (url == null) {
                System.err.println("❌ 이미지 못 찾음 : " + f);
                return new ImageIcon();
            }
            return new ImageIcon(url);
        });
    }

    /* ---------- 공개 메서드 ---------- */
    public static ImageIcon stick(boolean front) {     // true = 앞면
        return load(front ? "stick_front.png" : "stick_back.png");
    }

    public static ImageIcon backdo() {                 // 백도 전용
        return load("stick_backdo.png");
    }

    /* 말 아이콘 (원본 크기) */
    public static ImageIcon piece(String color) {
        return load("piece_" + color.toLowerCase() + ".png");
    }

    /* 말 아이콘을 원하는 크기로 축소/확대 */
    public static ImageIcon piece(String color, int size) {
        ImageIcon src = piece(color);
        Image scaled  = src.getImage()
                        .getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    
    private ResourceLoader() {}
}
