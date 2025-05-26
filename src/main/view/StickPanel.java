package main.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.concurrent.Flow;

/**
 * JavaFX 버전의 StickPanel
 */
public class StickPanel extends Pane implements Flow.Subscriber<boolean[]> {
    private Flow.Subscription subscription;
    private boolean[] faces = {true, true, true, true};
    private boolean backdo = false;
    private final Canvas canvas;

    public StickPanel() {
        // 기본 크기 설정
        canvas = new Canvas(540, 200);
        getChildren().add(canvas);
        // 리사이즈 대응
        widthProperty().addListener((obs, oldW, newW) -> resizeCanvas());
        heightProperty().addListener((obs, oldH, newH) -> resizeCanvas());
        draw();
    }

    @Override
    public void onSubscribe(Flow.Subscription s) {
        this.subscription = s;
        subscription.request(1);
    }

    @Override
    public void onNext(boolean[] item) {
        setFaces(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("StickPanel error: " + t);
    }

    @Override
    public void onComplete() {
        System.out.println("StickPanel updates complete");
    }

    /**
     * 윷 결과 업데이트 (backdo 포함)
     */
    public void setFaces(boolean[] arr) {
        if (arr == null || arr.length < 5) return;
        faces = Arrays.copyOfRange(arr, 0, 4);
        backdo = arr[4];
        draw();
    }

    /**
     * 윷 결과 개수 반환 (-1: 백도, 1~4: 앞면 개수, 5: 모)
     */
    public int getResult() {
        if (backdo) return -1;
        int count = 0;
        for (boolean f : faces) if (f) count++;
        return count == 0 ? 5 : count;
    }

    private void resizeCanvas() {
        double w = getWidth();
        double h = getHeight();
        canvas.setWidth(w);
        canvas.setHeight(h);
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        // 1) 배경 타원
        gc.setFill(Color.web("#EBCF6F"));
        double stickGap = 90;
        double stickW = 120;
        double startX = 90;
        double totalW = stickW + 3 * stickGap;
        double ovalX = startX - 20;
        double ovalW = totalW + 40;
        double ovalY = 10;
        double ovalH = 200;
        gc.fillOval(ovalX, ovalY, ovalW, ovalH);

        // 2) 윷 그리기
        double x = startX;
        boolean isRealBackdo = faces[0] && !faces[1] && !faces[2] && !faces[3];
        for (int i = 0; i < 4; i++) {
            Image img;
            if (isRealBackdo) {
                img = (i == 0)
                        ? ResourceLoader.backdo()
                        : ResourceLoader.stick(true);
            } else {
                img = ResourceLoader.stick(faces[i]);
            }
            gc.drawImage(img, x, 50, stickW, stickW);
            x += stickGap;
        }
    }
}