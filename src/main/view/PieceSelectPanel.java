package main.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

/**
 * 팀별 "남은 말" 목록을 버튼/아이콘으로 보여주는 JavaFX 패널
 */
public class PieceSelectPanel extends VBox {

    private final Button newPieceBtn;
    private final List<List<ImageView>> pieceViews = new ArrayList<>();
    private final String[] teamColors;

    public PieceSelectPanel(GameConfig cfg, String[] colors) {
        this.teamColors = colors;
        int teams = cfg.teamCount();
        int pieces = cfg.piecePerTeam();

        // 레이아웃 설정
        setSpacing(10);
        setPadding(new Insets(20, 10, 20, 10));
        setBackground(new Background(new BackgroundFill(Color.web("#FAF5E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        // 토큰 그리드
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 30, 0));

        for (int t = 0; t < teams; t++) {
            String color = colors[t];
            Image img = new Image(getClass().getResourceAsStream("/img/piece_" + color + ".png"), 40, 40, true, true);
            List<ImageView> teamList = new ArrayList<>();
            for (int p = 0; p < pieces; p++) {
                ImageView iv = new ImageView(img);
                iv.setFitWidth(40);
                iv.setFitHeight(40);
                grid.add(iv, p, t);
                teamList.add(iv);
            }
            pieceViews.add(teamList);
        }

        // 그리드와 버튼 배치
        getChildren().add(grid);
        VBox.setVgrow(grid, Priority.ALWAYS);

        // "새로운 말 꺼내기" 버튼
        newPieceBtn = new Button("새로운 말 꺼내기");
        newPieceBtn.setFont(Font.font("SansSerif", 16));
        newPieceBtn.setMaxWidth(200);
        newPieceBtn.setBackground(new Background(new BackgroundFill(Color.web("#C8DCF0"), new CornerRadii(5), Insets.EMPTY)));
        HBox buttonWrapper = new HBox(newPieceBtn);
        buttonWrapper.setAlignment(Pos.CENTER); // ⬅️ 중앙 정렬!
        getChildren().add(buttonWrapper);

    }

    /** 새 말 꺼내기 버튼 반환 */
    public Button getNewPieceButton() {
        return newPieceBtn;
    }

    /** 말 사용 시 해당 팀의 첫 번째 보이는 말 아이콘 숨김 */
    public void usePiece(String teamColor) {
        for (int i = 0; i < teamColors.length; i++) {
            if (teamColors[i].equals(teamColor)) {
                for (ImageView iv : pieceViews.get(i)) {
                    if (iv.isVisible()) {
                        iv.setVisible(false);
                        this.requestLayout();
                        return;
                    }
                }
                return;
            }
        }
    }

    /** 말 반환 시 해당 팀의 첫 번째 숨겨진 말 아이콘 보이게 */
    public void returnPiece(String teamColor) {
        for (int i = 0; i < teamColors.length; i++) {
            if (teamColors[i].equals(teamColor)) {
                for (ImageView iv : pieceViews.get(i)) {
                    if (!iv.isVisible()) {
                        iv.setVisible(true);
                        this.requestLayout();
                        return;
                    }
                }
                return;
            }
        }
    }
    public class CapturedSubscriber implements Flow.Subscriber<Piece> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(Piece piece) {
            Platform.runLater(() -> returnPiece(piece.getOwner().getColor()));
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("PieceSelectPanel error: " + throwable);
        }

        @Override
        public void onComplete() {
            System.out.println("PieceSelectPanel updates complete");
        }
    }

    public class UserSubscriber implements Flow.Subscriber<Player> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(Player player) {
            Platform.runLater(() -> usePiece(player.getColor()));
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("PieceSelectPanel error: " + throwable);
        }

        @Override
        public void onComplete() {
            System.out.println("PieceSelectPanel updates complete");
        }
    }

    public CapturedSubscriber getCapturedSubscriber() {
        return new CapturedSubscriber();
    }

    public UserSubscriber getUserSubscriber() {
        return new UserSubscriber();
    }
}