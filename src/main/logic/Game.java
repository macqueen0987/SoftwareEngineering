package main.logic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class Game{
    private final SubmissionPublisher<List<StructPiece>> boardPublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<boolean[]> sticksPublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<Piece> capturedPublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<Player> userPublisher = new SubmissionPublisher<>();

    private final Board board;
    private Sticks sticks = new Sticks();
    private List<Player> players = new ArrayList<>();
    private int turn = 0;
    private int polygon = 4;
    private int additionalThrow = 0;
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

    public List<Integer> getPendingThrows() { return pendingThrows; }

    public void addPendingThrow(int value) {
        pendingThrows.add(value);
    }

    /** 윷 던지기 + StickPanel 갱신 */
    public void throwSticks() {
        int result = sticks.throwSticks();
        pendingThrows.add(result);
        System.out.println("Throw result: " + result);
        while(result == 4 || result == 5){
            result = sticks.throwSticks();
            pendingThrows.add(result);
            System.out.println("Throw result: " + result);
        }

        // 윷 던진 결과를 StickPanel에 반영
        boolean[] arr = Arrays.copyOf(sticks.getFaces(), 5);
        arr[4] = sticks.isBackdo();
        sticksPublisher.submit(arr);
    }

    /**말 선택*/
    public Piece selectPiece(int slotIdx){
        if(slotIdx == -1) return null;
        else return board.getSlot(slotIdx).getPiece();
    }

    /** 말 이동 */
    public void movePiece(Piece p, int moveValue) {
        Player current = players.get(turn);
        Piece captured = null; // 잡힌 말 저장 변수
        Piece target;

        if(p != null && p.getOwner() == current) target = p;
        else if (current.getPieces().isEmpty()) {
            target = current.createPiece();
            target.setSlot(board.getStart());

            // 새 말을 꺼냈으므로 이벤트로 UI에 알림
            userPublisher.submit(current);
            System.out.println("Piece moved: " + current);
        } else {
            target = current.getPieces().get(0);
        }

        BoardSlot dest = target.getMoveCandidate(moveValue, polygon);

        // --- 선택한 dest로 이동 ---
        if (dest != null) {
            captured = target.move(dest); // <- move()가 잡은 말 반환
            if (captured != null) {
                System.out.println("말 잡음");
                additionalThrow ++; // 잡았으면 턴 유지
            } else if (additionalThrow > 0) {
                additionalThrow --; // 추가 턴을 소모 했는데 말을 잡지 못한 경우
            }
        }
        //System.out.println("additionalThrow: " + additionalThrow);
        if(dest.num == -1){
            current.arrivePiece(target);
        }

        // 잡힌 말이 있으면 UI에 다시 표시
        if (captured != null) {
            for (int i = 0; i < captured.getCount(); i++) {
                Piece dummy = new Piece(captured.getOwner()); // 임시 말 생성
                capturedPublisher.submit(dummy); // 개수만큼 UI에 표시
            }
        }


        updateBoardView();

        // 윷/모 또는 잡았을 때 → 추가 턴 (턴 넘기지 않음)
        if (additionalThrow <= 0 && pendingThrows.isEmpty()) {
            turn = (turn + 1) % players.size();
            additionalThrow = 0;
        }
    }

    public void updateBoardView() {
        List<StructPiece> uiPieces = new ArrayList<>();
        for (int i = 0; i < polygon * 7 + 1; i++) {
            BoardSlot slot = board.getSlot(i);
            Piece piece = slot.getPiece();
            if (piece != null) {
                for (int j = 0; j < piece.getCount(); j++) {
                    uiPieces.add(new StructPiece(i, piece.getOwner().getColor(), j));
                }
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

    public SubmissionPublisher<Piece> getCapturedPublisher() {
        return capturedPublisher;
    }

    public SubmissionPublisher<Player> getUserPublisher() {
        return userPublisher;
    }
}