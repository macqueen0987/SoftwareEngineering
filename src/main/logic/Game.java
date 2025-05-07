package main.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class Game{
    private final SubmissionPublisher<List<StructPiece>> boardPublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<boolean[]> sticksPublisher = new SubmissionPublisher<>();


    //public record StructPiece(int slot, String color, int order){}

    private final Board board;
    private Sticks sticks = new Sticks();
    private List<Player> players = new ArrayList<>();
    private int turn = 0;
    private int polygon = 4;

    private List<Integer> pendingThrows = new ArrayList<>();

    public Game(int polygon, int teamCount, String[] colors, int piecePerTeam) {
        this.polygon = polygon;
        board = new Board(polygon);

        // 플레이어 생성
        for (int i = 0; i < teamCount; i++) {
            String color = colors[i];
            String name = "Player " + (i + 1);
            Player player = new Player(name, color, piecePerTeam);
            players.add(player);
        }

        updateBoardView();
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Player[] getPlayers() {
        return players.toArray(new Player[0]);
    }

    /** 윷 던지기 + StickPanel 갱신 */
    public void throwSticks() {
        int result = sticks.throwSticks();
        pendingThrows.add(result);
        System.out.println("Throw result: " + result);

        // 윷 던진 결과를 StickPanel에 반영
        boolean[] arr = Arrays.copyOf(sticks.getFaces(), 5);
        arr[4] = sticks.isBackdo();
        sticksPublisher.submit(arr);
    }

    public void chooseSticks(){
        int result = 0;
        //TODO 윷 선택 과정
        pendingThrows.add(result);
        System.out.println("Throw result: " + result);

        // 윷 던진 결과를 StickPanel에 반영
        boolean[] arr = Arrays.copyOf(sticks.getFaces(), 5);
        arr[4] = sticks.isBackdo();
        sticksPublisher.submit(arr);
    }

    /**말 선택*/
    public Piece selectPiece(){
        return null;
    }

    /** 말 이동 */
    public void movePiece(Piece p) {
        Player current = players.get(turn);
        if (pendingThrows.isEmpty()) return;

        int moveValue = pendingThrows.remove(0);
        boolean bonusTurn = (moveValue == 4 || moveValue == 5);

        Piece target;
        if (current.getPieces().isEmpty()) {
            target = current.createPiece();
            target.setSlot(board.getStart());
        } else {
            target = current.getPieces().get(0); // TODO: 선택 UI 추가
        }

        BoardSlot candidate = target.getMoveCandidate(moveValue, polygon);
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

    public void updateBoardView() {
        List<StructPiece> uiPieces = new ArrayList<>();
        for (int i = 0; i < polygon * 7 + 1; i++) {
            BoardSlot slot = board.getSlot(i);
            Piece piece = slot.getPiece();
            if (piece != null) {
                uiPieces.add(new StructPiece(i, piece.getOwner().getColor(), 0));
            }
        }
        boardPublisher.submit(uiPieces);
    }

    public Player getCurrentPlayer() {
        return players.get(turn);
    }

    public SubmissionPublisher<List<StructPiece>> getBoardPublisher() {
        return boardPublisher;
    }

    public SubmissionPublisher<boolean[]> getSticksPublisher() {
        return sticksPublisher;
    }
}