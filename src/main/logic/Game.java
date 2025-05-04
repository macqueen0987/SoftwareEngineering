package main.logic;

import main.view.BoardPanel;
import main.view.OptionPanel;
import main.view.StickPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Game extends Observable {

    private Board board = new Board(4);
    private Sticks sticks = new Sticks();
    private List<Player> players = new ArrayList<>();
    private int turn = 0;
    private BoardPanel boardPanel;
    private StickPanel stickPanel;

    private List<Integer> pendingThrows = new ArrayList<>();

    public Game(BoardPanel boardPanel, StickPanel stickPanel) {
        this.boardPanel = boardPanel;
        this.stickPanel = stickPanel;

        players.add(new Player("Player1", "RED", board));
        players.add(new Player("Player2", "BLUE", board));

        updateBoardView();
    }

    /** 윷 던지기 + StickPanel 갱신 */
    public void throwSticks() {
        int result = sticks.throwSticks();
        pendingThrows.add(result);
        System.out.println("Throw result: " + result);

        // 윷 던진 결과를 StickPanel에 반영
        stickPanel.setFaces(sticks.getFaces(), sticks.isBackdo());
    }

    public void chooseSticks(){
        int result = 0;
        //TODO 윷 선택 과정
        pendingThrows.add(result);
        System.out.println("Throw result: " + result);

        // 윷 던진 결과를 StickPanel에 반영
        stickPanel.setFaces(sticks.getFaces(), sticks.isBackdo());
    }

    /** 말 이동 */
    public void movePiece() {
        Player current = players.get(turn);
        if (pendingThrows.isEmpty()) return;

        int moveValue = pendingThrows.remove(0);
        boolean bonusTurn = (moveValue == 4 || moveValue == 5);

        Piece target;
        if (current.getPieces().isEmpty()) {
            target = current.createPiece();
        } else {
            target = current.getPieces().get(0);
        }

        BoardSlot candidate = target.getMoveCandidate(moveValue, 4);
        BoardSlot dest = candidate;

        // --- 선택한 dest로 이동 ---
        boolean caught = false;
        if (dest != null) {
            caught = target.move(dest);
        }

        if(dest.num == -1){
            current.arrivePiece(target);
        }

        updateBoardView();

        // 윷/모 또는 잡았을 때 → 추가 턴 (턴 넘기지 않음)
        if (pendingThrows.isEmpty() && !bonusTurn && !caught) {
            turn = (turn + 1) % players.size();
        }
    }

    private void updateBoardView() {
        List<BoardPanel.Piece> uiPieces = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            BoardSlot slot = board.getSlot(i);
            Piece piece = slot.getPiece();
            if (piece != null) {
                uiPieces.add(new BoardPanel.Piece(i, piece.getOwner().getColor(), 0));
            }
        }
        boardPanel.setPieces(uiPieces);
    }

    public Player getCurrentPlayer() {
        return players.get(turn);
    }
}