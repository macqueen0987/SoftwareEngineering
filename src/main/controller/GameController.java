package main.controller;

import main.logic.Game;
import main.logic.GameEventListener;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import main.view.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final Game game;
    private final UIComponents ui;
    private final GameConfig config;
    private final JFrame mainFrame;
    private List<Integer> throwList;
    private boolean waitForClick = false;
    private int moveValue;

    public GameController(UIComponents ui, GameConfig config, String[] colors, JFrame mainFrame) {
        this.ui = ui;
        this.config = config;
        this.mainFrame = mainFrame;

        int polygon = switch (config.boardShape()) {
            case "사각" -> 4;
            case "오각" -> 5;
            case "육각" -> 6;
            default -> 4;
        };

        PieceSelectPanel panel = new PieceSelectPanel(config, colors);
        this.game = new Game(polygon, config.teamCount(), colors, config.piecePerTeam(), panel);

        game.getBoardPublisher().subscribe(ui.boardPanel);
        game.getSticksPublisher().subscribe(ui.stickPanel);

        ui.randomThrowButton.addActionListener(e -> onThrowSticks());
        ui.newPieceButton.addActionListener(e -> onNewPiece());
        ui.boardPanel.setSlotClickListener(this::onSelectSlot);
        ui.forceThrowButton.addActionListener(e -> forceThrowSticks());
        this.game.setGameEventListener(new GameEventListener() {
            @Override
            public void onPieceCaptured(Piece captured) {
                ui.piecePanel.returnPiece(captured.getOwner().getColor());
            }

            @Override
            public void onPieceUsed(Player player) {
                ui.piecePanel.usePiece(player.getColor());
            }
        });


        updateStatus();
    }

    private void forceThrowSticks() {
        /* 내가 원하는 윷의 결과를 지정할 수 있도록 하는 메소드 */
        while (true) {
            String[] options = {"백도", "도", "개", "걸", "윷", "모"};
            int result = OptionPanel.select("윷 결과를 선택하세요.", "확인 후 취소를 눌러 선택을 완료하세요.", options);

            if (result == -1) break; // 취소한 경우

            if (result == 0) result = -1; // 백도는 -1
            game.addPendingThrow(result);
        }
        selectThrow();
    }

    private void onThrowSticks() {
        game.throwSticks();
        //while(!throwList.isEmpty()) selectThrow();
        selectThrow();
    }

    private void selectThrow() {
        throwList = game.getPendingThrows();

        // 선택지 문자열 준비
        String[] str = new String[throwList.size()];
        for(int i = 0; i < throwList.size(); i++) {
            str[i] = String.valueOf(throwList.get(i));
        }

        // OptionPanel의 select 사용 → 드롭다운으로 선택
        int selectedIndex = OptionPanel.select("적용할 윷", str);

        if (selectedIndex == -1) return; // 취소하면 종료

        // 선택한 윷 값과 throwList 매칭 후 제거 및 적용
        moveValue = throwList.remove(selectedIndex);
        waitForClick = true;
    }

    private void onNewPiece() {
        if (game.getPendingThrows().isEmpty()){
            OptionPanel.alert("먼저 윷을 던져야 합니다.");
            return;
        }
        Player current = game.getCurrentPlayer();
        ArrayList<Piece> pieces = game.getBoard().getStart().getPieces();
        for (Piece piece : pieces) {
            if (piece.getOwner() == current) {
                OptionPanel.alert("시작 슬롯에 이미 말이 있습니다.");
                return;
            }
        }
        Piece newPiece = current.createPiece();

        if (newPiece == null) {
            OptionPanel.alert("꺼낼 수 있는 말이 없습니다.");
            return;
        }

        ui.piecePanel.usePiece(current.getColor());

        newPiece.setSlot(game.getBoard().getStart());
        game.getBoard().getStart().addPiece(newPiece);

        // 보드 UI 갱신
        game.updateBoardView();
        onSelectSlot(0); // 시작 슬롯을 선택한 것으로 간주
    }

    private void onSelectSlot(int idx){

        if(waitForClick){
            Piece p = game.selectPiece(idx);
            if (p == null) {
                OptionPanel.alert("이동할 수 있는 말이 없습니다.");
                return;
            }
            if (p.getOwner() != game.getCurrentPlayer()) {
                OptionPanel.alert("상대편의 말을 선택할 수 없습니다.");
                return;
            }
            game.movePiece(p, moveValue);

            updateStatus();
            checkWinner();
            waitForClick = false;
            if(!throwList.isEmpty()){
                selectThrow();
            }
        }
    }

    private void updateStatus() {
        Player current = game.getCurrentPlayer();
        ui.statusPanel.setTurn(current.getColor());

        int[] scores = new int[config.teamCount()];
        for (int i = 0; i < config.teamCount(); i++) {
            scores[i] = game.getPlayer(i).getScore();
        }
        ui.statusPanel.setScores(scores);
    }

    private void checkWinner() {
        for (int i = 0; i < config.teamCount(); i++) {
            Player player = game.getPlayer(i);
            if (player.getScore() >= 5) {
                ((MainFrame) mainFrame).declareWinner(player.getColor());
            }
        }
    }
}
