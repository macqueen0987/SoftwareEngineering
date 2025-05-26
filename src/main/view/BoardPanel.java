package main.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import main.logic.StructPiece;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Flow;

/**
 * JavaFX 버전의 BoardPanel
 */
public class BoardPanel extends Pane implements Flow.Subscriber<List<StructPiece>> {
    private Flow.Subscription subscription;
    private final Canvas canvas;
    private List<StructPiece> pieces = List.of();
    private Integer highlight = null;
    private final String boardShape;
    private SlotClickListener listener;

    public interface SlotClickListener { void onSlotClick(int idx); }

    public BoardPanel(String shape) {
        this.boardShape = shape;
        canvas = new Canvas(650, 650);
        getChildren().add(canvas);

        // 초기 스케일링
        scaleBoard(canvas.getWidth(), canvas.getHeight());
        // 리사이즈 대응
        widthProperty().addListener((obs, oldW, newW) -> resizeCanvas());
        heightProperty().addListener((obs, oldH, newH) -> resizeCanvas());
        // 클릭 이벤트
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);

        draw();
    }

    @Override public void onSubscribe(Flow.Subscription s) {
        this.subscription = s;
        subscription.request(1);
    }
    @Override public void onNext(List<StructPiece> item) {
        // 1) 미리 요청해 두고…
        subscription.request(1);

        // 2) FX 쓰레드에서 실행되도록 스케줄
        javafx.application.Platform.runLater(() -> {
            // (선택) 불변 상태 보장 위해 복사본 사용
            this.pieces = List.copyOf(item);
            draw();
        });
    }
    @Override public void onError(Throwable t) {
        System.err.println("BoardPanel error: " + t);
    }
    @Override public void onComplete() {
        System.out.println("BoardPanel updates complete");
    }

    public void setSlotClickListener(SlotClickListener l) { this.listener = l; }
    public void setPieces(List<StructPiece> list) { this.pieces = list; draw(); }

    private void resizeCanvas() {
        double w = getWidth(), h = getHeight();
        canvas.setWidth(w); canvas.setHeight(h);
        scaleBoard(w, h);
        draw();
    }

    private void scaleBoard(double w, double h) {
        switch (boardShape) {
            case "오각" -> BoardGeometryPentagon.scaleToPane(w, h);
            case "육각" -> HexBoardGeometry.scaleToPane(w, h);
            default     -> BoardGeometry.scaleToPane(w, h);
        }
    }

    private void handleClick(MouseEvent e) {
        int idx;
        double x = e.getX(), y = e.getY();
        switch (boardShape) {
            case "오각" -> idx = BoardGeometryPentagon.slotAt(x, y);
            case "육각" -> idx = HexBoardGeometry.slotAt(x, y);
            default     -> idx = BoardGeometry.slotAt(x, y);
        }
        if (idx != -1) {
            highlight = idx;
            draw();
            if (listener != null) listener.onSlotClick(idx);
        }
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);
        // 배경
        gc.setFill(Color.web("#F5EEDC"));
        gc.fillRect(0, 0, w, h);
        // 보드 그리기
        gc.setLineWidth(2);
        switch (boardShape) {
            case "오각" -> drawPentagon(gc);
            case "육각" -> drawHexagon(gc);
            default     -> { drawLines(gc); drawSlots(gc); }
        }
        drawPieces(gc);
        drawHighlight(gc);
    }

    private void drawLines(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        for (int[] e : BoardGeometry.EDGE) {
            Point2D a = BoardGeometry.SLOT[e[0]];
            Point2D b = BoardGeometry.SLOT[e[1]];
            gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }

    private void drawSlots(GraphicsContext gc) {
        gc.setFill(Color.web("#A37321"));
        gc.setStroke(Color.web("#644614"));
        gc.setFont(Font.font("SansSerif", 10));
        Set<Integer> special = Set.of(0, 5, 10, 15, 20);
        for (int i = 0; i < BoardGeometry.SLOT.length; i++) {
            Point2D p = BoardGeometry.SLOT[i];
            double R = special.contains(i) ? 30 : 20;
            gc.fillOval(p.getX() - R, p.getY() - R, 2 * R, 2 * R);
            gc.strokeOval(p.getX() - R, p.getY() - R, 2 * R, 2 * R);
            gc.setFill(Color.DARKGRAY);
            gc.fillText(String.valueOf(i), p.getX() - R - 7, p.getY() - R - 3);
        }
    }

    private void drawPieces(GraphicsContext gc) {
        for (StructPiece pc : pieces) {
            Point2D p = switch (boardShape) {
                case "오각" -> BoardGeometryPentagon.offset(pc.slot(), pc.order());
                case "육각" -> HexBoardGeometry.offset(pc.slot(), pc.order());
                default     -> BoardGeometry.offset(pc.slot(), pc.order());
            };
            Image im = ResourceLoader.piece(pc.color(), 40, 40);
            gc.drawImage(im, p.getX() - 20, p.getY() - 20, 40, 40);
        }
    }

    private void drawHighlight(GraphicsContext gc) {
        if (highlight == null) return;
        Point2D p = switch (boardShape) {
            case "오각" -> BoardGeometryPentagon.T_SLOT[highlight];
            case "육각" -> HexBoardGeometry.SLOT[highlight];
            default     -> BoardGeometry.SLOT[highlight];
        };
        gc.setStroke(Color.RED);
        gc.strokeOval(p.getX() - 12, p.getY() - 12, 24, 24);
    }

    private void drawPentagon(GraphicsContext gc) {
        Point2D[] slots = BoardGeometryPentagon.T_SLOT;
        gc.setStroke(Color.BLACK);
        // 꼭짓점 선
        for (int i = 0; i < 5; i++) {
            Point2D a = slots[i * 5];
            Point2D b = slots[((i + 1) % 5) * 5];
            gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
        // 중심 연결선
        gc.setStroke(Color.GRAY);
        Point2D center = slots[25];
        for (int i = 0; i < 5; i++) {
            Point2D corner = slots[i * 5];
            gc.strokeLine(center.getX(), center.getY(), corner.getX(), corner.getY());
        }
        // 슬롯 노드
        for (Point2D p : slots) {
            boolean isCorner = (p == slots[0] || p == slots[5] || p == slots[10] || p == slots[15] || p == slots[20]);
            double size = isCorner ? 45 : 35;
            gc.setFill(Color.web("#A37321"));
            gc.fillOval(p.getX() - size / 2, p.getY() - size / 2, size, size);
            gc.setStroke(Color.web("#644614"));
            gc.strokeOval(p.getX() - size / 2, p.getY() - size / 2, size, size);
        }
        // 번호 표시
        gc.setFill(Color.DARKGRAY);
        gc.setFont(Font.font("Arial", 12));
        for (int i = 0; i < BoardGeometryPentagon.SLOT.length; i++) {
            Point2D p = BoardGeometryPentagon.SLOT[i];
            String s = String.valueOf(i);
            Text txt = new Text(s);
            txt.setFont(gc.getFont());
            Bounds bounds = txt.getLayoutBounds();
            double tx = p.getX() + 8;
            double ty = p.getY() - 8;
            gc.fillText(s, tx, ty);
        }
    }

    private void drawHexagon(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        for (int[] e : HexBoardGeometry.EDGE) {
            Point2D a = HexBoardGeometry.SLOT[e[0]];
            Point2D b = HexBoardGeometry.SLOT[e[1]];
            gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
        gc.setFont(Font.font(10));
        Set<Integer> big = Set.of(0, 5, 10, 15, 20, 25, 30);
        for (int i = 0; i < HexBoardGeometry.SLOT.length; i++) {
            Point2D p = HexBoardGeometry.SLOT[i];
            double R = big.contains(i) ? 25 : 18;
            gc.setFill(Color.web("#A37321")); gc.fillOval(p.getX()-R,p.getY()-R,2*R,2*R);
            gc.setStroke(Color.web("#644614")); gc.strokeOval(p.getX()-R,p.getY()-R,2*R,2*R);
            String s = String.valueOf(i);
            Text txt = new Text(s);
            txt.setFont(gc.getFont());
            Bounds bnd = txt.getLayoutBounds();
            double tx = p.getX() - bnd.getWidth()/2;
            double ty = p.getY() + R + bnd.getHeight();
            gc.setFill(Color.DARKGRAY); gc.fillText(s, tx, ty);
        }
    }
}