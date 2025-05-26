package main.view;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * /resources/images/ 아래 PNG를 캐싱해서 반환하는 유틸리티 클래스 (JavaFX)
 */
public final class ResourceLoader {
    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final String DIR = "/img/"; // 클래스패스 기준 디렉토리

    private static Image load(String fileName) {
        return CACHE.computeIfAbsent(fileName, f -> {
            try {
                var stream = ResourceLoader.class.getResourceAsStream(DIR + f);
                if (stream == null) {
                    System.err.println("❌ 이미지 못 찾음: " + DIR + f);
                    return null;
                }
                return new Image(stream);
            } catch (Exception e) {
                System.err.println("❌ ResourceLoader 로딩 오류: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * 윷 stick 이미지 반환 (true = 앞면, false = 뒷면)
     */
    public static Image stick(boolean front) {
        return load(front ? "stick_front.png" : "stick_back.png");
    }

    /**
     * 백도 전용 stick 이미지
     */
    public static Image backdo() {
        return load("stick_backdo.png");
    }

    /**
     * 말 아이콘 (기본 크기) 반환
     */
    public static Image piece(String color) {
        return load("piece_" + color.toLowerCase() + ".png");
    }

    /**
     * 말 아이콘을 원하는 크기로 축소/확대하여 반환
     */
    public static Image piece(String color, double width, double height) {
        var key = color.toLowerCase() + "@" + width + "x" + height;
        return CACHE.computeIfAbsent(key, k -> {
            Image src = load("piece_" + color.toLowerCase() + ".png");
            if (src == null) return null;
            return new Image(ResourceLoader.class.getResourceAsStream(DIR + "piece_" + color.toLowerCase() + ".png"),
                    width, height, true, true);
        });
    }

    private ResourceLoader() {
        // 유틸리티 클래스: 인스턴스화 금지
    }
}