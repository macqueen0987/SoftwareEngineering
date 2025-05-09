import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import main.logic.*;

class LogicTest {
    private Game game;
    private Board board;
    private Player player;
    private Player player2;

    @BeforeEach
    void testStart(){
        System.out.println("=====test start=====");
        game = new Game(4, 2, new String[]{"Red", "Blue"}, 5);
        board = game.getBoard();
        player = game.getPlayer(0);
        player2 = game.getPlayer(1);
    }

    @Test
    void testAll(){
        testSticksThrow();
        testBoardInitialization();
        testPlayersInitialization();
        testAddPieceToBoard();
        testSelectPiece();
        testMovePiece();
        testRemovePiece();
    }

    @Test
    void testSticksThrow(){
        // 던진 윷의 값 범위 테스트
        Sticks sticks = new Sticks();
        int result = sticks.throwSticks();
        assertTrue(result >= -1 && result <= 5 && result != 0, "윷은 -1,1,2,3,4,5의 값만 가진다.");
        System.out.println("testSticksThrow clear");
    }
    @Test
    void testBoardInitialization() {
        // 윷놀이판 초기화 테스트
        BoardSlot firstSlot = board.getStart();
        assertNotNull(firstSlot, "첫 번째 슬롯은 null 이 아니어야 한다.");
        assertEquals(0, firstSlot.num, "첫 번째 슬롯의 번호는 0이다.");
        System.out.println("testBoardInitialization clear");
    }
    @Test
    void testPlayersInitialization() {
        // 플레이어가 초기화되었는지 테스트
        Player[] players = game.getPlayers();
        assertEquals(2, players.length, "두 명의 player가 존재해야 한다.");
        assertEquals("Red", players[0].getColor(), "첫 번째 플레이어의 색은 red다.");
        assertEquals("Blue", players[1].getColor(), "두 번째 플레이어의 색은 blue다.");
        System.out.println("testPlayersInitialization clear");

    }
    @Test
    void testAddPieceToBoard() {
        // 말을 보드에 추가하는 테스트
        Piece piece = player.createPiece();
        piece.setSlot(board.getStart());
        assertNotNull(piece, "추가된 말은 null이 아니어야 한다.");
        assertEquals(board.getStart(), piece.getSlot(), "말의 위치는 첫 번째 슬롯이다.");
        assertEquals(player, piece.getOwner(), "말은 플레이어에 속해야 한다.");
        player.removePiece(piece);
        System.out.println("testAddPieceToBoard clear");
    }
    @Test
    void testSelectPiece() {
        // 말 선택 테스트
        Piece piece = player.createPiece();
        piece.setSlot(board.getStart());
        piece.move(board.getSlot(1));
        assertNotNull(game.selectPiece(1), "첫 번째 슬롯의 말을 가져와야 한다.");
        board.getSlot(0).removePiece(piece);
        System.out.println("testSelectPiece clear");
    }
    @Test
    void testMovePiece() {
        // 말 이동 테스트
        Piece piece = player.createPiece();
        piece.setSlot(board.getStart());
        piece.move(board.getStart());
        game.movePiece(piece, 3); // 3칸 이동
        assertEquals(piece.getSlot(), board.getSlot(3), "말은 slot3으로 움직여야 한다.");
        board.getSlot(3).removePiece(piece);
        System.out.println("testMovePiece clear");
    }
    @Test
    void testRemovePiece() {
        // 말을 제거하는 테스트
        BoardSlot firstSlot = board.getStart();
        Piece piece = player2.createPiece();
        piece.setSlot(board.getStart());
        firstSlot.removePiece(firstSlot.getPiece());
        player2.removePiece(piece);
        assertNull(firstSlot.getPiece(), "슬롯에서 말이 제거되어야 한다.");
        assertTrue(player2.getPieces().isEmpty(), "플레이어는 더 이상 말을 가지고 있지 않아야 한다.");
        System.out.println("testRemovePiece clear");
    }

    @AfterEach
    void testEnd(){
        System.out.println("=====test end=====");
    }
}
