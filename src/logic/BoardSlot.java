package logic;

public class BoardSlot extends Entity {
    private BoardSlot[] next = new BoardSlot[2];
    private BoardSlot[] prev = new BoardSlot[2];
    private Piece piece;
    public final int num;

    public BoardSlot(int num) {
        this.num = num;
    }

    public void setNext(int index, BoardSlot slot) { next[index] = slot; }
    public void setPrev(int index, BoardSlot slot) { prev[index] = slot; }
    public BoardSlot[] getNext() { return next; }
    public BoardSlot[] getPrev() { return prev; }
    public void addPiece(Piece piece) { this.piece = piece; }
    public void removePiece() { this.piece = null; }
    public Piece getPiece() { return piece; }
}