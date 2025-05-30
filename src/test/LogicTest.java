import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import main.logic.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class TestCase {
    int shape;
    int teamCount;
    String[] colors = {"Red", "Blue", "Green", "Yellow"};
    int piecePerTeam;

    public TestCase(int shape, int teamCount, int piecePerTeam){
        this.shape = shape;
        this.teamCount = teamCount;
        this.piecePerTeam = piecePerTeam;
        this.colors = Arrays.copyOfRange(colors, 0, teamCount);
    }
}

public class LogicTest {
    static private final TestCase[] testCases = new TestCase[]{
            new TestCase(4, 2, 2),
            new TestCase(5,3,3),
            new TestCase(6,4,4),
            new TestCase(4,3,5)};
    static private int caseIdx = 0;
    static private int[][] moveTestCase;
    static private Game game;
    static private Board board;
    static private Player[] players;

    @BeforeAll
    static void testInit(){
        System.out.println("=====Test start=====");
        System.out.println("CASE - shape: "+testCases[caseIdx].shape+", players: "+testCases[caseIdx].teamCount+", Pieces: "+testCases[caseIdx].piecePerTeam);
        game = new Game(testCases[caseIdx].shape, testCases[caseIdx].teamCount,
                testCases[caseIdx].colors, testCases[caseIdx].piecePerTeam);
        board = game.getBoard();
        players = game.getPlayers();
        int shape = board.getShape();
        if(shape == 4){
            moveTestCase = new int[][]{{0, 3, 3},{0, -1, 19},{5, 3, 20},{5, 4, 25},{10, 4, 27},
                    {20, 2, 28},{20, -1, 22},{3, 5, 8},{15, -1, 14},{19, 1, -1}};
        }
        else if (shape == 5){
            moveTestCase = new int[][]{{0, 3, 3},{0, -1, 24},{5, 3, 25},{5, 4, 32},{10, 4, 32},
                    {25, 2, 35},{25, -1, 27},{3, 5, 8},{15, 5, 35},{20, -1, 19},{24, 1, -1}};
        }
        else {
            moveTestCase = new int[][]{{0, 3, 3},{0, -1, 29},{5, 3, 30},{5, 4, 39},{10, 4, 39},
                    {30, 2, 42},{30, -1, 32},{3, 5, 8},{15, 5, 42},{20, -1, 19},{29, 1, -1}};
        }
    }

    @BeforeEach
    void testStart(){
        // 테스트 시작 전마다 player, board 초기화
        List<Piece> pieces = players[0].getPieces();
        while(!pieces.isEmpty()) {
            pieces.getLast().getSlot().removePiece(pieces.getLast());
            players[0].removePiece(pieces.getLast());
        }
        List<Piece> pieces2 = players[1].getPieces();
        while(!pieces2.isEmpty()) {
            pieces2.getLast().getSlot().removePiece(pieces2.getLast());
            players[1].removePiece(pieces2.getLast());
        }
    }

//    @RepeatedTest(10)
//    void testAll() throws InterruptedException {
//        testSticksThrow();
//        testBoardInitialization();
//        testBoardLinking();
//        testPlayersInitialization();
//        testAddPieceToBoard();
//        testRemovePiece();
//        testSelectPiece();
//        testMoveCandidate();
//        testMovePiece();
//        testPieceCatch();
//        testSticksPublisher();
//        testBoardPublisher();
//        testCapturedPublisher();
//        testUserPublisher();
//    }

    @Test
    void testSticksThrow(){
        // 던진 윷의 값 범위 테스트
        game.throwSticks();
        List<Integer> result = game.getPendingThrows();
        while(!result.isEmpty()){
            assertTrue(result.getFirst() >= -1 && result.getFirst() <= 5 && result.getFirst() != 0,
                    "윷은 -1,1,2,3,4,5의 값만 가진다.");
            result.removeFirst();
        }
        System.out.println("testSticksThrow clear");
    }
    @Test
    void testBoardInitialization() {
        // 윷놀이판 초기화 테스트
        BoardSlot firstSlot = board.getStart();
        int boardShape = board.getShape();
        assertEquals(testCases[caseIdx].shape, boardShape, "보드 모양 불일치." );
        assertNotNull(firstSlot, "첫 번째 슬롯은 null 이 아니어야 한다.");
        assertEquals(0, firstSlot.num, "첫 번째 슬롯의 번호는 0이다.");
        System.out.println("testBoardInitialization clear");
    }
    @Test
    void testBoardLinking(){
        // 모드 연결상태 테스트
        assertEquals(board.getSlot(0).getNext()[0], board.getSlot(1),"슬롯 0-1의 비정상적인 보드 연결");
        assertEquals(board.getSlot(0).getPrev()[1], board.getSlot(board.getShape() * 7),
                "슬롯 0-last 의 비정상적인 보드 연결");
        assertEquals(board.getSlot(5).getNext()[1], board.getSlot(board.getShape() * 5 + 1),
                "슬롯 5-x의 비정상적인 보드 연결");
        assertEquals(board.getSlot(board.getShape() * 5 + 1).getPrev()[0], board.getSlot(5),
                "슬롯 x-5의 비정상적인 보드 연결");
        System.out.println("testBoardLinking clear");

    }
    @Test
    void testPlayersInitialization() {
        // 플레이어가 초기화되었는지 테스트
        Player[] players = game.getPlayers();
        assertEquals(testCases[caseIdx].teamCount, players.length, "플레이어 인원수 불일치.");
        assertEquals("Red", players[0].getColor(), "첫 번째 플레이어의 색은 red다.");
        assertEquals("Blue", players[1].getColor(), "두 번째 플레이어의 색은 blue다.");
        assertEquals(players[0].getMaxPieceCount(), players[1].getMaxPieceCount(), "플레이어들의 말 최대 개수는 동일하다.");
        System.out.println("testPlayersInitialization clear");

    }
    @Test
    void testAddPieceToBoard() {
        // 말을 보드에 추가하는 테스트
        Piece piece = players[0].createPiece();
        assertNotNull(piece, "말이 정상적으로 생성되지 않았다.");
        assertEquals(players[0], piece.getOwner(), "말이 생성한 플레이어에 속하지 않는다.");
        piece.setSlot(board.getStart());
        board.getSlot(0).addPiece(piece);
        assertEquals(board.getStart(), piece.getSlot(), "말이 첫 번째 슬롯에 존재하지 않는다.");
        System.out.println("testAddPieceToBoard clear");
    }
    @Test
    void testRemovePiece() {
        // 말을 제거하는 테스트
        Piece piece = players[0].createPiece();
        piece.setSlot(board.getStart());
        board.getSlot(0).addPiece(piece);
        board.getStart().removePiece(board.getStart().getPiece());
        players[0].removePiece(piece);
        assertNull(board.getStart().getPiece(), "슬롯에서 말이 제거되어야 한다.");
        assertTrue(players[0].getPieces().isEmpty(), "플레이어는 더 이상 말을 가지고 있지 않아야 한다.");
        System.out.println("testRemovePiece clear");
    }
    @Test
    void testSelectPiece() {
        // 말 선택 테스트
        Piece piece = players[0].createPiece();
        piece.setSlot(board.getStart());
        board.getSlot(0).addPiece(piece);
        assertNotNull(game.selectPiece(0), "첫 번째 슬롯의 말을 가져와야 한다.");
        System.out.println("testSelectPiece clear");
    }
    @Test
    void testMoveCandidate() {
        // 말 움직임 계산 함수 테스트
        Piece piece = players[0].createPiece();
        for (int[] num : moveTestCase) {
            piece.setSlot(board.getSlot(num[0]));
            board.getSlot(num[0]).addPiece(piece);
            assertEquals(num[2], piece.getMoveCandidate(num[1], board.getShape()).num,"말의 움직임 계산이 부정확하다.");
            board.getSlot(num[0]).removePiece(piece);
        }
        System.out.println("testMoveCandidate clear");
    }
    @Test
    void testMovePiece() {
        // 말 이동 테스트
        Player player = game.getCurrentPlayer();
        game.movePiece(null, 3); // 3칸 이동
        Piece piece = player.getPieces().getFirst();
        assertEquals(3, piece.getSlot().num, "말이 슬롯3으로 움직여야 한다.");
        board.getSlot(3).removePiece(piece);
        player.removePiece(piece);
        Player player2 = game.getCurrentPlayer();
        Piece piece2 = player2.createPiece();
        piece2.setSlot(board.getSlot(board.getShape()*5));
        board.getSlot(board.getShape()*5).addPiece(piece2);
        game.movePiece(piece2, 3);
        assertEquals(-1, piece2.getSlot().num, "말이 도착해야 한다.");
        assertEquals(1, player2.getScore(), "점수가 증가해야 한다.");
        System.out.println("testMovePiece clear");
    }
    @Test
    void testPieceCatch(){
        // 말을 잡거나 합쳐지는 테스트
        Piece piece = players[0].createPiece();
        piece.setSlot(board.getSlot(3));
        board.getSlot(3).addPiece(piece);
        Piece piece2 = players[0].createPiece();
        piece2.setSlot(board.getStart());
        board.getSlot(0).addPiece(piece2);
        piece2.move(board.getSlot(3));
        assertEquals(2, piece.getCount(), "말이 합쳐져야 한다.");
        Piece piece3 = players[1].createPiece();
        piece3.setSlot(board.getStart());
        board.getSlot(0).addPiece(piece3);
        assertNotNull(piece3.move(board.getSlot(3)), "말을 잡아야 한다.");
        System.out.println("testPieceCatch clear");
    }
    @Test
    void testSticksPublisher() throws InterruptedException {
        final boolean[] check = {false};
        CountDownLatch latch = new CountDownLatch(1);
        Subscriber<boolean[]> stickSub = new Subscriber<boolean[]>() {
            @Override
            public void onSubscribe(Subscription subscription) { subscription.request(1); }
            @Override
            public void onNext(boolean[] item) {
                check[0] = true;
                latch.countDown();
            }
            @Override
            public void onError(Throwable throwable) { System.out.println("stickPublisher error: " + throwable); }
            @Override
            public void onComplete() { }
        };
        game.getSticksPublisher().subscribe(stickSub);
        game.throwSticks();
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(check[0], "stickPublisher 가 정상 작동하지 않는다.");
        System.out.println("testSticksPublisher clear");
    }
    @Test
    void testBoardPublisher() throws InterruptedException {
        final boolean[] check = {false};
        CountDownLatch latch = new CountDownLatch(1);
        Subscriber<List<StructPiece>> boardSub = new Subscriber<List<StructPiece>>() {
            @Override
            public void onSubscribe(Subscription subscription) { subscription.request(1); }
            @Override
            public void onNext(List<StructPiece> item) {
                check[0] = true;
                latch.countDown();
            }
            @Override
            public void onError(Throwable throwable) { System.out.println("boardPublisher error: " + throwable); }
            @Override
            public void onComplete() { }
        };
        game.getBoardPublisher().subscribe(boardSub);
        game.updateBoardView();
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(check[0], "boardPublisher 가 정상 작동하지 않는다.");
        System.out.println("testBoardPublisher clear");
    }
    @Test
    void testCapturedPublisher() throws InterruptedException {
        final boolean[] check = {false};
        CountDownLatch latch = new CountDownLatch(1);
        Subscriber<Piece> capturedSub = new Subscriber<Piece>() {
            @Override
            public void onSubscribe(Subscription subscription) { subscription.request(1); }
            @Override
            public void onNext(Piece item) {
                check[0] = true;
                latch.countDown();
            }
            @Override
            public void onError(Throwable throwable) { System.out.println("capturedPublisher error: " + throwable); }
            @Override
            public void onComplete() { }
        };
        game.getCapturedPublisher().subscribe(capturedSub);
        Piece piece = players[1].createPiece();
        piece.setSlot(board.getSlot(3));
        board.getSlot(3).addPiece(piece);
        Piece piece2 = players[0].createPiece();
        piece2.setSlot(board.getSlot(0));
        board.getSlot(0).addPiece(piece2);
        game.movePiece(piece2, 3);
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(check[0], "capturedPublisher 가 정상 작동하지 않는다.");
        System.out.println("testCapturedPublisher clear");
    }
    @Test
    void testUserPublisher() throws InterruptedException {
        final boolean[] check = {false};
        CountDownLatch latch = new CountDownLatch(1);
        Subscriber<Player> userSub = new Subscriber<Player>() {
            @Override
            public void onSubscribe(Subscription subscription) { subscription.request(1); }
            @Override
            public void onNext(Player item) {
                check[0] = true;
                latch.countDown();
            }
            @Override
            public void onError(Throwable throwable) { System.out.println("userPublisher error: " + throwable); }
            @Override
            public void onComplete() { }
        };
        game.getUserPublisher().subscribe(userSub);
        game.movePiece(null, 1);
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(check[0], "userPublisher 가 정상 작동하지 않는다.");
        System.out.println("testUserPublisher clear");
    }


    @AfterAll
    static void testEnd(){
        System.out.println("=====Test end=====");
        System.out.println();
        caseIdx++;
    }
}
