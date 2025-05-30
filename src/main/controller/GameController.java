package main.controller;

import main.logic.Game;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import main.view.BoardPanel;
import main.view.MainFrame;
import main.view.OptionPanel;
import main.view.PieceSelectPanel;
import main.view.StatusPanel;
import main.view.StickPanel;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final Game game;
    private final UIComponents ui;
    private final GameConfig config;
    private final MainFrame mainFrame;
    private List<Integer> throwList;
    private boolean waitForClick = false;
    private int moveValue = -2; // -2는 아직 윷을 안 던진 상태

    public GameController(UIComponents ui,
                          GameConfig config,
                          String[] colors,
                          MainFrame mainFrame) {
        this.ui        = ui;
        this.config    = config;
        this.mainFrame = mainFrame;

        int polygon = switch (config.boardShape()) {
            case "사각" -> 4;
            case "오각" -> 5;
            case "육각" -> 6;
            default     -> 4;
        };
        this.game = new Game(polygon,
                config.teamCount(),
                colors,
                config.piecePerTeam());

        // 백엔드에서 발행하는 스트림 구독
        game.getBoardPublisher().subscribe(ui.boardPanel);
        game.getSticksPublisher().subscribe(ui.stickPanel);
        game.getCapturedPublisher().subscribe(ui.piecePanel.getCapturedSubscriber());
        game.getUserPublisher().subscribe(ui.piecePanel.getUserSubscriber());

        // 버튼 클릭 핸들러 (JavaFX)
        ui.randomThrowButton.setOnAction(e -> onThrowSticks());
        ui.forceThrowButton.setOnAction(e -> forceThrowSticks());
        ui.newPieceButton.setOnAction(e -> onNewPiece());
        ui.boardPanel.setSlotClickListener(this::onSelectSlot);

        updateStatus();
    }

    private void forceThrowSticks() {
        while (true) {
            String[] options = {"백도", "도", "개", "걸", "윷", "모"};
            int r = OptionPanel.select("윷 결과 선택", "확인 후 취소로 완료", options);
            if (r == -1) break;
            if (r == 0)  r = -1;  // 백도 = -1
            game.addPendingThrow(r);
        }
        selectThrow();
    }

    private void onThrowSticks() {
        if (!game.getPendingThrows().isEmpty() || moveValue >= -1) {
            OptionPanel.alert("현재 던진 윷이 있습니다.");
            return;
        }
        game.throwSticks();
        selectThrow();
    }

    private void selectThrow() {
        throwList = game.getPendingThrows();
        String[] str = new String[throwList.size()];
        for (int i = 0; i < throwList.size(); i++) {
            str[i] = String.valueOf(throwList.get(i));
        }
        int idx = OptionPanel.select("적용할 윷 선택", str);
        if (idx == -1) return;
        moveValue    = throwList.remove(idx);
        waitForClick = true;
    }

    private void onNewPiece() {
        if (game.getPendingThrows().isEmpty() && moveValue < -1) {
            OptionPanel.alert("먼저 윷을 던지세요.");
            return;
        }
        Player cur = game.getCurrentPlayer();
        var startList = game.getBoard().getStart().getPieces();
        for (Piece p : startList) {
            if (p.getOwner() == cur) {
                OptionPanel.alert("시작 슬롯에 이미 말이 있습니다.");
                return;
            }
        }
        Piece newPiece = cur.createPiece();
        if (newPiece == null) {
            OptionPanel.alert("꺼낼 말이 없습니다.");
            return;
        }
        ui.piecePanel.usePiece(cur.getColor());
        newPiece.setSlot(game.getBoard().getStart());
        game.getBoard().getStart().addPiece(newPiece);
        game.updateBoardView();
        onSelectSlot(0);
    }

    private void onSelectSlot(int idx) {
        if (!waitForClick) return;
        Piece p = game.selectPiece(idx);
        if (p == null) {
            OptionPanel.alert("이동할 말이 없습니다.");
            return;
        }
        if (p.getOwner() != game.getCurrentPlayer()) {
            OptionPanel.alert("상대 말을 선택할 수 없습니다.");
            return;
        }
        game.movePiece(p, moveValue);
        game.updateBoardView();
        moveValue    = -2;
        waitForClick = false;
        updateStatus();
        if (!throwList.isEmpty()) selectThrow();
        checkWinner();
    }

    private void updateStatus() {
        Player cur = game.getCurrentPlayer();
        ui.statusPanel.setTurn(cur.getColor());

        int[] scores = new int[config.teamCount()];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = game.getPlayer(i).getScore();
        }
        ui.statusPanel.setScores(scores);
    }

    private void checkWinner() {
        for (int i = 0; i < config.teamCount(); i++) {
            Player pl = game.getPlayer(i);
            if (pl.getScore() >= config.piecePerTeam()) {
                mainFrame.declareWinner(pl.getColor());
            }
        }
    }
}
