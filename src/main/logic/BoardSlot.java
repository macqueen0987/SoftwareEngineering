package main.logic;

public class BoardSlot extends Entity {
    private BoardSlot[] next;
    private BoardSlot[] prev;
    private Piece piece;
    public final int num;

    public BoardSlot(int num, int polygon) {
        this.num = num;
        this.next = new BoardSlot[(polygon/2) + 1];
        this.prev = new BoardSlot[(polygon/2) + 1];
    }

    public void setNext(int index, BoardSlot slot) { next[index] = slot; }
    public void setPrev(int index, BoardSlot slot) { prev[index] = slot; }
    public BoardSlot[] getNext() { return next; }
    public BoardSlot[] getPrev() { return prev; }
    public void addPiece(Piece piece) { this.piece = piece; }
    public void removePiece() { this.piece = null; }
    public Piece getPiece() { return piece; }
}