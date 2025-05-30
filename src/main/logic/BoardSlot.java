package main.logic;

import java.util.ArrayList;

public class BoardSlot extends Entity {
    private BoardSlot[] next;
    private BoardSlot[] prev;
    private ArrayList<Piece> piece;
    public final int num;

    public BoardSlot(int num, int polygon) {
        this.num = num;
        this.next = new BoardSlot[(polygon/2) + 1];
        this.prev = new BoardSlot[(polygon/2) + 1];
        this.piece = new ArrayList<>();
    }

    public void setNext(int index, BoardSlot slot) { next[index] = slot; }
    public void setPrev(int index, BoardSlot slot) { prev[index] = slot; }
    public BoardSlot[] getNext() { return next; }
    public BoardSlot[] getPrev() { return prev; }
    public void setPiece(Piece piece) { this.piece = new ArrayList<>(); this.piece.add(piece); }
    public void addPiece(Piece piece) { this.piece.add(piece); }
    public void removePiece(Piece piece) { this.piece.remove(piece); }
    public Piece getPiece() {
        if (piece.isEmpty()) return null;
        return piece.getFirst();
    }
    public ArrayList<Piece> getPieces() { return piece; }
}