package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SticksTest {
    private Board board;
    private Player player;

    @BeforeEach
    void testStart(){
        System.out.println("=====test start=====");
        board = new Board(5, 2, 4);
        player = new Player(0, 1);
    }

    @Test
    void sticksThrowTest(){
        // 던진 윷의 값 범위 테스트
        Sticks sticks = new Sticks();
        int result = sticks.throwSticks();
        assertTrue(result >= -1 && result <= 5 && result != 0, "윷은 -1,1,2,3,4,5의 값만 가진다.");
    }
    @Test
    void testBoardInitialization() {
        // 윷놀이판 초기화 테스트
        BoardSlot firstSlot = board.getFirstSlot();
        assertNotNull(firstSlot, "첫 번째 슬롯은 null이 아니어야 한다.");
        assertEquals(0, firstSlot.num, "첫 번째 슬롯의 번호는 0이다.");
    }
    @Test
    void testPlayerScore() {
        // 점수 증가 테스트
        player.score(1); // 1점을 추가
        player.score(2); // 2점을 추가
        assertEquals(3, player.getScore(), "점수 합산이 올바르지 않다.");
    }
    @Test
    void testAddPieceToBoard() {
        // 말을 보드에 추가하는 테스트
        BoardSlot firstSlot = board.getFirstSlot();
        Piece piece = player.addPiece(firstSlot);
        assertNotNull(piece, "추가된 말은 null이 아니다.");
        assertEquals(firstSlot, piece.slot, "말의 위치는 첫 번째 슬롯이다.");
        assertEquals(player, piece.getPlayer(), "말은 플레이어에 속해야 한다.");
    }
    @Test
    void testRemovePiece() {
        // 말을 제거하는 테스트
        BoardSlot firstSlot = board.getFirstSlot();
        Piece piece = player.addPiece(firstSlot);
        firstSlot.addPiece(piece);
        firstSlot.removePiece();
        player.removePiece(piece);
        assertNull(firstSlot.getPiece(), "슬롯에서 말이 제거되어야 한다.");
        assertEquals(0, player.getPieceCount(), "플레이어는 더 이상 말을 가지고 있지 않는다.");
    }

    @AfterEach
    void testEnd(){
        System.out.println("=====test end=====");
    }
}