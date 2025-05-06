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
        int midToStart = (polygon / 2) - 2;
        int temp = (polygon + 1) / 2;
        BoardSlot candidate = slot;
        // 계산 중 중앙 슬롯을 지나게 될 경우 이동방향 미리 설정
        if(candidate.num == temp * 5 ||
                candidate.num == polygon * 5 + temp * 2 ||
                candidate.num == polygon * 5 + temp * 2 - 1 ||
                candidate.num == polygon * 5) midToStart = polygon / 2 - 1;
        if (steps == -1) {      // 빽도만 별도 처리
            candidate = slot.getPrev()[0];
            return candidate;
        }
        // 대각선 및 중앙 슬롯의 경우의 이동 처리
        if(candidate.num % 5 == 0 && (candidate.num / 5 <= (polygon + 1) / 2 && candidate.num / 5 > 0)) {
            candidate = candidate.getNext()[1];
            for(int step = 0; step < steps - 1; step++){
                if(candidate.num == polygon * 5) {
                    candidate = candidate.getNext()[midToStart];
                    break;
                }
                candidate = candidate.getNext()[0];
                if(candidate.num == 0) {
                    candidate = new BoardSlot(-1, polygon);
                    break;
                }
            }
            return candidate;
        }
        else if (candidate.num % 5 == 0 && candidate.num == polygon * 5){
            candidate = candidate.getNext()[polygon / 2 - 1];
            for(int step = 0; step < steps - 1; step++){
                if(candidate.num == polygon * 5) {
                    candidate = candidate.getNext()[midToStart];
                    break;
                }
                candidate = candidate.getNext()[0];
                if(candidate.num == 0) {
                    candidate = new BoardSlot(-1, polygon);
                    break;
                }
            }
            return candidate;
        }
        for(int step = 0; step < steps; step++){
            if(candidate.num == polygon * 5){
                candidate = candidate.getNext()[midToStart];
            }
            else{
                candidate = candidate.getNext()[0];
            }
            if(candidate.num == 0) {
                candidate = new BoardSlot(-1, polygon);
                break;
            }
        }
        return candidate;
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
