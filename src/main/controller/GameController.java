package main.controller;

import main.logic.Game;
import main.logic.Piece;
import main.logic.Player;
import main.model.GameConfig;
import main.view.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/*
TODO: 여기 곳곳에 JOptionPane.showMessageDialog(null, "메시지")가 있는데, 이걸 UIComponents로 빼서
UI는 완전히 분리해야 될것 같습니다. 안그러면 나중에 다른 UI를 사용하게 될때 여기저기 수정해야합니다.
*/

public class GameController {

    private final Game game;
    private final UIComponents ui;
    private final GameConfig config;
    private final JFrame mainFrame;
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

        PieceSelectPanel panel = new PieceSelectPanel(config, colors);
        this.game = new Game(polygon, config.teamCount(), colors, config.piecePerTeam(), panel);

        game.getBoardPublisher().subscribe(ui.boardPanel);
        game.getSticksPublisher().subscribe(ui.stickPanel);

        ui.randomThrowButton.addActionListener(e -> onThrowSticks());
        ui.newPieceButton.addActionListener(e -> onNewPiece());
        ui.boardPanel.setSlotClickListener(this::onSelectSlot);
        ui.forceThrowButton.addActionListener(e -> forceThrowSticks());
        this.game.setGameEventListener(captured -> {
            ui.piecePanel.returnPiece(captured.getOwner().getColor());
        });


        updateStatus();
    }

    private void forceThrowSticks() {
        /* 내가 원하는 윷의 결과를 지정할 수 있도록 하는 메소드 */
        while (true) {
            String input = JOptionPane.showInputDialog(null, "윷 결과를 입력하세요 (-1: 백도, 1~5: 도~모, 취소: 입력 종료)", "지정 윷 던지기", JOptionPane.PLAIN_MESSAGE);

            if (input == null) {
                // 취소 또는 닫기 누르면 종료
                break;
            }

            try {
                int result = Integer.parseInt(input);
                if (result < -1 || result > 5) {
                    JOptionPane.showMessageDialog(null, "잘못된 입력입니다. -1부터 5 사이 숫자를 입력하세요.");
                    continue;
                }
                game.addPendingThrow(result);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "숫자만 입력하세요.");
            }
        }
        selectThrow();
    }

    private void onThrowSticks() {
        game.throwSticks();
        //while(!throwList.isEmpty()) selectThrow();
        selectThrow();
    }

    private void selectThrow(){
        String[] str = new String[10];
        throwList = game.getPendingThrows();
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
        ArrayList<Piece> pieces = game.getBoard().getStart().getPieces();
        for (Piece piece : pieces) {
            if (piece.getOwner() == current) {
                JOptionPane.showMessageDialog(null, "시작 슬롯에 이미 말이 있습니다.");
                return;
            }
        }
        Piece newPiece = current.createPiece();

        if (newPiece == null) {
            JOptionPane.showMessageDialog(null, "꺼낼 수 있는 말이 없습니다.");
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
                JOptionPane.showMessageDialog(null, "이동할 수 있는 말이 없습니다.");
                return;
                // TODO: 이제 movePiece 에서 piece 가 null 이 될수 없으므로 해당 코드들은 불필요함
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
