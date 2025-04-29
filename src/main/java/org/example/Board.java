package org.example;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.foreign.MemorySegment.NULL;
import static org.example.MoveCalculator.*;

class Entity {
//    public int x;
//    public int y;
}

class Stick extends Entity {
    /** 윷가락 하나 */
    public Stick() {}
    public int throwStick() {
        return (int) (Math.random() * 2);
    }
}


class Sticks{
    private Stick[] sticks;

    /**
     * 윷놀이에서 윷을 던지는 클래스
     */
    public Sticks(){
        this.sticks = new Stick[4];
        for (int i = 0; i < 4; i++) {
            this.sticks[i] = new Stick();
        }
    }

    /**
     * 윷을 던지는 메소드;
     * @return 이동해야 하는 칸, -1이면 백도, 0이면 에러
     */
    public int throwSticks() {
        int [] results = new int[4];
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            results[i] = this.sticks[i].throwStick();
            sum += results[i];
        }
        if (sum == 0){
            return 5;
        } else if (sum == 1){
            if (results[0] == 1){
                return -1; // 백도
            } else {
                return 1; // 도
            }
        } else if (sum == 2){
            return 2;
        } else if (sum == 3){
            return 3;
        } else if (sum == 4){
            return 4;
        } else {
            return 0; // error
        }
    }
}

class BoardSlot extends Entity {
    private final BoardSlot[] next;
    private final BoardSlot[] prev;
    private Piece piece;
    final int num;

    /**
     * 윷놀이판의 각각의 칸을 나타내는 클래스
     */
    public BoardSlot(int num, int polygon) {
        this.num = num;
        this.next = new BoardSlot[polygon];
        this.prev = new BoardSlot[polygon];
    }

    public BoardSlot(BoardSlot boardSlot){
        this.next = boardSlot.getNext();
        this.prev = boardSlot.getPrev();
        this.piece = boardSlot.getPiece();
        this.num = boardSlot.num;
    }

    public void removePiece(){
        this.piece = null;
    }

    public void addPiece(Piece piece){
        this.piece = piece;
    }

    public Piece getPiece(){
        return this.piece;
    }

    /**
     * @param num 0: 다음 칸, n: 다음 칸(칸이 n개인 경우)
     * @param next 다음 칸
     */
    public void setNext(int num, BoardSlot next) { this.next[num] = next; }

    /**
     * @param num 0: 이전 칸, n: 이전 칸(칸이 n개인 경우)
     * @param prev 이전 칸
     */
    public void setPrev(int num, BoardSlot prev) {
        this.prev[num] = prev;
    }

    public BoardSlot [] getNext() { return this.next; }

    public BoardSlot [] getPrev() {
        return this.prev;
    }
}

class Piece extends Entity {
    private int count;
    private Player player;
    public BoardSlot slot;
    private BoardSlot [] candidateSlots;
    private MoveCalculator calculator = new MoveCalculator();

    /**
     * 윷놀이에서 말을 나타내는 클래스
     * @param player {@link Player}
     * @param slot 시작 칸
     */
    public Piece(Player player, BoardSlot slot) {
        this.count = 1;
        this.player = player;
        this.slot = slot;
        this.candidateSlots = new BoardSlot[polygon];
    }

    public void printStatus(){
        System.out.println("main.Piece at: " + this.slot.num + ", count: " + this.count);
    }

    public int getSlotNum() {
        return this.slot.num;
    }

    public void group(Piece piece) {
        this.count += piece.count;
    }

    public int getCount() {
        return this.count;
    }

    /**
     * @return {@link Player}
     */
    public Player getPlayer() {
        return this.player;
    }

    private void extendCalcMove(int steps){
        candidateSlots = calculator.calcMove(steps, this.slot);
    }

    public void showMove(int steps){
        if (steps < -1 || steps > 5 || steps == 0){
            System.out.println("Invalid move");
            return;
        }
        this.extendCalcMove(steps);
        int i = 0;
        while(candidateSlots[i].num < (side+ diagnal) * polygon + 1){
            if (this.candidateSlots[i].num == -1) {
                System.out.println((i + 1) + ": finish");
                break;
            } else {
                System.out.println((i + 1) + ": " + this.candidateSlots[i].num);
            }
            i++;
        }
    }

    /**
     * 이동할 칸을 선택하는 메소드
     * @param num <code>this.candidateSlots[num]</code>에 해당하는 칸으로 이동
     * @return 윷을 한번 더 던질 수 있는지 여부
     */
    public boolean move(int num){
        this.slot.removePiece();
        if (this.candidateSlots[num] == null) {
            // 어차피 null 이 나올수가 없긴 한데 에디터에서 자꾸 넣으라그럼
            System.out.println("Invalid move");
            return false;
        }
        this.slot = this.candidateSlots[num];
        if (this.slot.num == -1){
            // 나갈 수 있음
            this.player.removePiece(this);
            this.player.score(this.count);
            return false;
        }
        if (this.slot.getPiece() != null){
            System.out.println("main.Piece at " + this.slot.num + " already exists");
            Piece piece = this.slot.getPiece();
            if (piece.getPlayer() == this.player){
                piece.group(this);
                this.player.removePiece(this);
                return false;
            } else {
                piece.getPlayer().removePiece(piece);
                this.slot.removePiece();
                this.slot.addPiece(this);
                return true;
            }
        }
        this.slot.addPiece(this);
        return false;
    }
}


class Board {
    private final BoardSlot [] slots;
    private int side;    // (윷놀이 판을 구성하는 각변의 slot 개수) - 1
    private int diagnal; // (윷놀이 판을 구성하는 대각선 - 3) /2
    private int polygon; // 윷놀이 판은 기본 4각형
    /**
     * 윷놀이판을 나타내는 클래스
     * <pre><code>
     *(10)  (9)  (8)  (7)  (6)   (5)
     *(11) (25)            (23)  (4)
     *(12)      (26)   (24)      (3)
     *             (20)
     *(13)      (27)   (21)      (2)
     *(14) (28)            (22)  (1)
     *(15) (16) (17) (18)  (19)  (0)
     * </code></pre>
     */
    public Board(int side, int diagnal, int polygon){
        this.side = side;
        this.diagnal = diagnal;
        this.polygon = polygon;
        this.slots = new BoardSlot[(side + diagnal) * polygon + 1];
        for (int i = 0; i < this.slots.length; i++) {
            this.slots[i] = new BoardSlot(i, polygon);
        }
        this.linkSlots();
    }

    public void linkSlots() {
        BoardSlot emptySlot = new BoardSlot((side+diagnal)*polygon + 1, polygon);
        for(int i = 0; i < (side+diagnal)*polygon + 1; i++){    // board의 가능한 next, prev 이외에는 전부 invaild 값을 갖도록 초기화
            for(int j = 0; j < polygon; j++){
                slots[i].setNext(j, emptySlot);
                slots[i].setPrev(j, emptySlot);
            }
        }
        for (int i = 0; i <= side * polygon - 1; i++) {         // 외곽선 슬롯들 연결
            this.slots[i].setNext(0, this.slots[i + 1]);
            this.slots[i + 1].setPrev(0, this.slots[i]);
            if(i == side * polygon - 1){
                this.slots[i].setNext(0, this.slots[0]);
                this.slots[0].setPrev(0, this.slots[i]);
            }
        }
        for(int i = 0; i < polygon; i++){                       // 대각선 슬롯들 연결
            for (int j = 1; j < diagnal; j++){
                this.slots[side*polygon + i*diagnal + j].setNext(0, this.slots[side*polygon + i*diagnal + j + 1]);
                this.slots[side*polygon + i*diagnal + j + 1].setPrev(0, this.slots[side*polygon + i*diagnal + j]);
            }
            if (i <= (polygon - 1)/2){          // 중앙으로 향하는 대각선
                this.slots[((i+1) * side) % (side*polygon)].setNext(1, this.slots[side*polygon + i*diagnal + 1]);
                this.slots[side*polygon + i*diagnal + 1].setPrev(0, this.slots[((i+1) * side) % (side*polygon)]);
                this.slots[side*polygon + (i+1)*diagnal].setNext(0, this.slots[side * polygon]);
                this.slots[side * polygon].setPrev(i, this.slots[side*polygon + (i+1)*diagnal]);
            }
            else{                               // 중앙에서 멀어지는 대각선
                this.slots[side*polygon + (i+1)*diagnal].setNext(0, this.slots[((i+1) * side) % (side*polygon)]);
                this.slots[((i+1) * side) % (side*polygon)].setPrev(1, this.slots[side*polygon + (i+1)*diagnal]);
                this.slots[side * polygon].setNext(i - (polygon + 1)/2, this.slots[side*polygon + i*diagnal + 1]);
                this.slots[side*polygon + i*diagnal + 1].setPrev(0, this.slots[side * polygon]);
            }
        }
    }


    public void testPrint(){
        for (int i = 0; i < (side+diagnal)*polygon + 1; i++) {
            BoardSlot [] nextslot = this.slots[i].getNext();
            BoardSlot [] prevslot = this.slots[i].getPrev();
            System.out.print("Slot " + i + ": ");
            System.out.print("Prev: ");
            for (int j = 0; j < polygon; j++) {
                if (prevslot[j] != null) {
                    System.out.print(prevslot[j].num + " ");
                }
            }
            System.out.print("Next: ");
            for (int j = 0; j < polygon; j++) {
                if (nextslot[j] != null) {
                    System.out.print(nextslot[j].num + " ");
                }
            }
            System.out.println();
        }
    }

    public void printBoard(){
        // 테스트 목적으로 일단 만들어둠, UI제작시 대체하면 될듯
    }

    /**
     * 시작칸을 리턴
     * */
    public BoardSlot getFirstSlot(){
        return slots[0];
    }
}


/**
 * 플레이어를 나타내는 클래스
 */
class Player {
    private int player;
    private int maxPieceCount;
    private int score;
    private ArrayList<Piece> pieces = new ArrayList<>();

    /**
     * 플레이어를 나타내는 클래스
     * @param player 플레이어 번호
     * @param maxPieceCount 각 플레이어의 말 개수
     */
    public Player(int player, int maxPieceCount) {
        this.player = player;
        this.score = 0;
        this.maxPieceCount = maxPieceCount;
    }

    public boolean checkNewPieceAvailable() {
        int cnt = 0;
        for (Piece piece : this.pieces) {
            cnt += piece.getCount();
        }
        if (cnt+score >= this.maxPieceCount) {
            return false;
        }
        return true;
    }

    public int getPlayer() {
        return player;
    }

    public void score(int score) {
        System.out.println("main.Player " + this.player + " score " + score);
        this.score += score;
    }

    public int getScore(){ return score; }

    public void removePiece(Piece piece) {
        this.pieces.remove(piece);
    }

    public int getPieceCount() {
        return pieces.size();
    }

    public Piece [] getPieces() {
        Piece [] pieces = new Piece[this.pieces.size()];
        for (int i = 0; i < this.pieces.size(); i++) {
            pieces[i] = this.pieces.get(i);
        }
        return pieces;
    }

    /**
     * 플레이어의 말을 추가하는 메소드
     * @param slot 시작 칸
     * @return 추가된 말
     */
    public Piece addPiece(BoardSlot slot) {
        Piece piece = new Piece(this, slot);
        this.pieces.add(piece);
        return piece;
    }

//    public void reducePieceCount(){
//        this.pieceCount--;
//    }
//    public void reducePieceCount(int num) {
//        this.pieceCount -= num;
//    }
}

class MoveCalculator {
    static public int side;
    static public int diagnal;
    static public int polygon;

    public MoveCalculator(int side, int diagnal, int polygon){
        MoveCalculator.side = side;
        MoveCalculator.diagnal = diagnal;
        MoveCalculator.polygon = polygon;
    }

    public MoveCalculator() {

    }

    public BoardSlot[] calcMove(int steps, BoardSlot slot){
        BoardSlot[] nextSlots = new BoardSlot[polygon];
        BoardSlot currentSlot = slot;
        int prevSlotNum = 0;
        int nDiagnal = 0;
        if (steps == -1){
            for(int i = 0; i < polygon; i++){
                nextSlots[i] = new BoardSlot(currentSlot.getPrev()[i]);
            }
            return nextSlots;
        }
        for(int i = 0; i < polygon; i++){
            nextSlots[i] = new BoardSlot(currentSlot.getNext()[i]);
        }
        if (nextSlots[0].num == 0){
            nextSlots[0] = new BoardSlot(-1, polygon);
            return nextSlots;
        }
        prevSlotNum = currentSlot.num;
        int i = 0;
        while(nextSlots[i].num < (side+diagnal) * polygon + 1)
        {
            for (int j = 0; j < steps - 1; j++) {
                if (nextSlots[i].num == 0){
                    nextSlots[i] = new BoardSlot(-1, polygon);
                    break;
                }
                if (nextSlots[i].num == side * polygon) {
                    nDiagnal = (prevSlotNum - side*polygon) / diagnal - 1;
                    if (polygon % 2 != 0 && nDiagnal == polygon / 2 + 1) {
                        nDiagnal -= 1;
                    }
                    prevSlotNum = nextSlots[i].num;
                    nextSlots[i] = nextSlots[i].getNext()[nDiagnal];
                } else {
                    prevSlotNum = nextSlots[i].num;
                    nextSlots[i] = nextSlots[i].getNext()[0];
                }
            }
            i++;
        }
        return nextSlots;
    }
}
