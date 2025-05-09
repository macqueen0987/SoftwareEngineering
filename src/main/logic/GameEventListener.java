// GameEventListener.java (새로 생성)
package main.logic;

public interface GameEventListener {
    void onPieceCaptured(Piece captured);
    void onPieceUsed(Player player);
}
