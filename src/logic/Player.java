package logic;

import java.util.*;

public class Player {

    private String name;
    private String color;
    private Board board;
    private List<Piece> pieces = new ArrayList<>();
    private int score = 0;

    public Player(String name, String color, Board board) {
        this.name = name;
        this.color = color;
        this.board = board;
    }

    public Piece createPiece() {
        Piece p = new Piece(this, board.getStart());
        pieces.add(p);
        return p;
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
        score += piece.getCount();
    }

    public List<Piece> getPieces() { return pieces; }
    public String getColor() { return color; }
    public String getName() { return name; }
}