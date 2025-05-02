package view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

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
    public static ImageIcon piece(String color) {      // 말 아이콘
        return load("piece_" + color.toLowerCase() + ".png");
    }

    private ResourceLoader() {}
}
