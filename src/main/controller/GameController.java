package main.controller;

import main.logic.Game;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import main.view.*;

import javax.swing.*;
import java.util.List;

public class GameController {

    private final Game game;
    private final UIComponents ui;
    private final GameConfig config;
    private final JFrame mainFrame;
    private int selectedSlot = -1;
    private List<Integer> throwList;
    private boolean waitForClick = false;
    private int selectedIdx;
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

        this.game = new Game(polygon, config.teamCount(), colors, config.piecePerTeam());

        game.getBoardPublisher().subscribe(ui.boardPanel);
        game.getSticksPublisher().subscribe(ui.stickPanel);

        ui.throwButton.addActionListener(e -> onThrowSticks());
        ui.newPieceButton.addActionListener(e -> onNewPiece());
        ui.boardPanel.setSlotClickListener(this::onSelectSlot);

        updateStatus();
    }

    private void onThrowSticks() {
        game.throwSticks();
        throwList = game.getPendingThrows();
        //while(!throwList.isEmpty()) selectThrow();
        selectThrow();
    }

    private void selectThrow(){
        String[] str = new String[10];
        for(int i = 0; i < throwList.size(); i++) str[i] = String.valueOf(throwList.get(i));
        int option =  Integer.parseInt((String) JOptionPane.showInputDialog(null,
                "적용할 윷",
                "윷 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                str,
                str[0]));
        for(int i = 0; i < throwList.size(); i++) {
            if(throwList.get(i) == option) this.selectedIdx = i ; //selectedIdx = i;
        }
        moveValue = throwList.remove(this.selectedIdx);
        waitForClick = true;
    }

    private void onNewPiece() {
        Player current = game.getCurrentPlayer();
        Piece newPiece = current.createPiece();

        if (newPiece == null) {
            JOptionPane.showMessageDialog(null, "꺼낼 수 있는 말이 없습니다.");
            return;
        }

        newPiece.setSlot(game.getBoard().getStart());
        game.getBoard().getStart().addPiece(newPiece);

        // 보드 UI 갱신
        game.updateBoardView();
    }

    private void onSelectSlot(int idx){
        selectedSlot = idx;
        if(waitForClick){
            Piece p = game.selectPiece(selectedSlot);
            game.movePiece(p, moveValue);
            updateStatus();
            checkWinner();
            waitForClick = false;
        }
        if(!throwList.isEmpty()){
            selectThrow();
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
