package main.controller;

import main.logic.Game;
import main.logic.Piece;
import main.logic.Player;
import main.view.BoardPanel;
import main.view.StatusPanel;
import main.view.StickPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController{

    private final Game game;
    private final BoardPanel boardPanel;
    private final StickPanel stickPanel;
    private final StatusPanel statusPanel;
    private final JButton throwButton;

    public GameController(BoardPanel boardPanel, StickPanel stickPanel,
                          StatusPanel statusPanel, JButton throwButton, String boardShape) {

        this.boardPanel = boardPanel;
        this.stickPanel = stickPanel;
        this.statusPanel = statusPanel;
        this.throwButton = throwButton;
        int polygon = switch (boardShape) {
            case "사각" -> 4;
            case "오각" -> 5;
            case "육각" -> 6;
            default -> 4;
        };

        // Game 초기화 (UI 연동)
        this.game = new Game(polygon);

        game.getBoardPublisher().subscribe(boardPanel);
        game.getSticksPublisher().subscribe(stickPanel);

        // 던지기 버튼과 이벤트 연결
        throwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onThrowSticks();
             }
        });

        // 초기 턴 상태 업데이트
        updateStatus();
    }

    /** 던지기 버튼 클릭시 호출 */
    private void onThrowSticks() {
        // 윷 던지기
        game.throwSticks();

        // 말 선택
        Piece p = game.selectPiece();

        // 말 이동
        game.movePiece(p);

        // 턴 상태 갱신
        updateStatus();
    }

    /** 상태 패널 갱신 */
    private void updateStatus() {
        Player current = game.getCurrentPlayer();
        String color = current.getColor();
        statusPanel.setTurn(color);

        // (추후 점수까지 넣을 수 있음. 지금은 0:0으로 고정)
        statusPanel.setScore(0, 0);
    }
}