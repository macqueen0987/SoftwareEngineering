package logic;

public class Piece extends Entity {
    private Player owner;
    private BoardSlot slot;
    private int count = 1;

    public Piece(Player owner, BoardSlot slot) {
        this.owner = owner;
        this.slot = slot;
    }

    public BoardSlot[] getMoveCandidates(int steps) {
        BoardSlot[] candidates = new BoardSlot[2];
        if (steps == -1) {
            candidates[0] = slot.getPrev()[0];
            candidates[1] = slot.getPrev()[1];
            return candidates;
        }
        for (int i = 0; i < 2; i++) {
            BoardSlot current = slot;
            for (int step = 0; step < steps; step++) {
                if (current == null) break;
                current = current.getNext()[i];
            }
            candidates[i] = current;
        }
        return candidates;
    }

    public boolean move(BoardSlot dest) {
        slot.removePiece();
        slot = dest;

        if (dest.getPiece() != null) {
            Piece other = dest.getPiece();
            if (other.owner == this.owner) {
                // 같은 팀 → 합치기
                other.count += this.count;
                owner.removePiece(this);
                return false;
            } else {
                // 상대 팀 → 잡기
                other.owner.removePiece(other);
                dest.removePiece();
                dest.addPiece(this);
                return true;  // 여기서 true 반환
            }
        }

        dest.addPiece(this);
        return false;
    }

    public BoardSlot getSlot() { return slot; }
    public int getCount() { return count; }
    public Player getOwner() { return owner; }
}