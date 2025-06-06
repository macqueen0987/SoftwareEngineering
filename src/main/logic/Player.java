package main.logic;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private String color;
    private List<Piece> pieces = new ArrayList<>();
    private int score = 0;

    private int maxPieceCount;

    public Player(String name, String color, int maxPieceCount) {
        this.name = name;
        this.color = color;
        this.maxPieceCount = maxPieceCount;
    }

    public String getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public int getScore() {
        return score;
    }

    public void arrivePiece(Piece piece){
        pieces.remove(piece);
        score += piece.getCount();
    }

    public Piece createPiece() {
        if (!this.canCreatePiece()) return null;

        Piece p = new Piece(this);
        pieces.add(p);
        return p;
    }

    private boolean canCreatePiece() {
        int num = 0;
        for (Piece piece : pieces) {
            num += piece.getCount();
        }
        return num < maxPieceCount;
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    public int getMaxPieceCount(){ return maxPieceCount; }
}
