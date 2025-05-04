package main.logic;

public class Piece extends Entity {
    private Player owner;
    private BoardSlot slot;
    private int count = 1;

    public Piece(Player owner, BoardSlot slot) {
        this.owner = owner;
        this.slot = slot;
    }

    public BoardSlot getMoveCandidate(int steps, int polygon){
        BoardSlot candidate = slot;
        if (steps == -1) {      // 빽도만 별도 처리
            candidate = slot.getPrev()[0];
            return candidate;
        }
        if(candidate.num % 5 == 0) {
            if (candidate.num == polygon * 5 || (candidate.num / 5 <= (polygon + 1) / 2 && candidate.num / 5 > 0)){
                candidate = candidate.getNext()[(polygon / 2) - 1];
                for(int step = 0; step < steps - 1; step++){
                    candidate = candidate.getNext()[0];
                }
                return candidate;
            }
        }
        for(int step = 0; step < steps; step++){
            if(candidate.num == polygon * 5){
                candidate = candidate.getNext()[(polygon / 2) - 2];
            }
            else{
                candidate = candidate.getNext()[0];
            }
        }
        return candidate;
    }

    public BoardSlot[] getMoveCandidates(int steps, int polygon) {
        int side = 5; int diagnal = 2;
        BoardSlot[] candidates = new BoardSlot[(polygon/2) + 1];
        BoardSlot candidate = new BoardSlot(0, polygon);
        BoardSlot currentSlot = slot;
        int prevSlotNum = 0;
        int nDiagnal = 0;
        if (steps == -1) {
            for(int i = 0; i < (polygon/2) + 1; i++){
                if(currentSlot.getNext()[i] != null) candidates[i] = new BoardSlot(currentSlot.getPrev()[i]);
            }
            return candidates;
        }
        for(int i = 0; i < (polygon/2) + 1; i++){
            if(currentSlot.getNext()[i] != null) candidates[i] = new BoardSlot(currentSlot.getNext()[i]);
        }
        if (candidates[0].num == 0){
            candidates[0] = new BoardSlot(-1, polygon);
            candidates[1] = new BoardSlot(-1, polygon);
            return candidates;
        }
        prevSlotNum = currentSlot.num;
        int i = 0;
        while(candidates[i] != null)
        {
            for (int j = 0; j < steps - 1; j++) {
                if (candidates[i].num == 0){
                    candidates[i] = new BoardSlot(-1, polygon);
                    break;
                }
                if (candidates[i].num == side * polygon) {
                    nDiagnal = (prevSlotNum - side*polygon) / diagnal - 1;
                    if (polygon % 2 != 0 && nDiagnal == polygon / 2 + 1) {
                        nDiagnal -= 1;
                    }
                    prevSlotNum = candidates[i].num;
                    candidates[i] = candidates[i].getNext()[nDiagnal];
                } else {
                    prevSlotNum = candidates[i].num;
                    candidates[i] = candidates[i].getNext()[0];
                }
            }
            i++;
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