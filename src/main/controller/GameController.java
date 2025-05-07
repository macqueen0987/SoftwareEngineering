package main.controller;

import main.logic.Game;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import main.view.*;

import javax.swing.*;

public class GameController {

    private final Game game;
    private final UIComponents ui;
    private final GameConfig config;
    private final JFrame mainFrame;

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

        updateStatus();
    }

    private void onThrowSticks() {
        game.throwSticks();
        Piece p = game.selectPiece(); // <- 이거 리턴값 null 인데 어떻게 돌아가는지 모르겠네요
        game.movePiece(p); // <- 심지어 여기서 p는 null 인데도 실행됨???? 이게 뭐임
        updateStatus();
        checkWinner();
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
