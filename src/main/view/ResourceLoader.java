package main.view;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * /resources/img/ 아래 PNG를 캐싱해서 반환하는 유틸리티 클래스 (JavaFX)
 */
public final class ResourceLoader {
    private static final String DIR = "/img/";
    // 1) 원본 이미지만 담는 캐시
    private static final Map<String, Image> ORIGINAL_CACHE = new HashMap<>();
    // 2) 크기 조정된 이미지만 담는 캐시
    private static final Map<String, Image> SCALED_CACHE   = new HashMap<>();

    // 원본 이미지 로드 (캐시 분리)
    private static Image loadOriginal(String fileName) {
        return ORIGINAL_CACHE.computeIfAbsent(fileName, f -> {
            var stream = ResourceLoader.class.getResourceAsStream(DIR + f);
            if (stream == null) {
                System.err.println("❌ 이미지 못 찾음: " + DIR + f);
                return null;
            }
            return new Image(stream);
        });
    }

    /** 윷 stick 이미지 반환 (true = 앞면, false = 뒷면) */
    public static Image stick(boolean front) {
        return loadOriginal(front ? "stick_front.png" : "stick_back.png");
    }

    /** 백도 전용 stick 이미지 */
    public static Image backdo() {
        return loadOriginal("stick_backdo.png");
    }

    /** 말 아이콘 (기본 크기) 반환 */
    public static Image piece(String color) {
        return loadOriginal("piece_" + color.toLowerCase() + ".png");
    }

    /**
     * 말 아이콘을 원하는 크기로 축소/확대하여 반환
     * (스케일된 이미지는 별도 캐시에만 저장)
     */
    public static Image piece(String color, double width, double height) {
        // 고유 키: 색상 + 가로x세로
        String key = color.toLowerCase() + "@" + width + "x" + height;
        return SCALED_CACHE.computeIfAbsent(key, k -> {
            // 원본 먼저 가져오고
            Image src = loadOriginal("piece_" + color.toLowerCase() + ".png");
            if (src == null) return null;
            // 스트림을 새로 열어서 스케일 이미지 생성
            var stream = ResourceLoader.class
                    .getResourceAsStream(DIR + "piece_" + color.toLowerCase() + ".png");
            return new Image(stream, width, height, true, true);
        });
    }

    private ResourceLoader() { /* 인스턴스화 금지 */ }
}
