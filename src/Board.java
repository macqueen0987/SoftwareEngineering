import java.util.ArrayList;
import java.util.Arrays;

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
    private BoardSlot[] next;
    private BoardSlot[] prev;
    private Piece piece;
    final int num;

    /**
     * 윷놀이판의 각각의 칸을 나타내는 클래스
     */
    public BoardSlot(int num) {
        this.num = num;
        this.next = new BoardSlot[2];
        this.prev = new BoardSlot[2];
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
     * @param num 0: 다음 칸, 1: 다음 칸(칸이 2개인 경우)
     * @param next 다음 칸
     */
    public void setNext(int num, BoardSlot next) {
        this.next[num] = next;
    }

    /**
     * @param num 0: 이전 칸, 1: 이전 칸(칸이 2개인 경우)
     * @param prev 이전 칸
     */
    public void setPrev(int num, BoardSlot prev) {
        this.prev[num] = prev;
    }

    public BoardSlot [] getNext() {
        return this.next;
    }

    public BoardSlot [] getPrev() {
        return this.prev;
    }
}

class Piece extends Entity {
    private int count;
    private Player player;
    public BoardSlot slot;
    private BoardSlot [] candidateSlots = new BoardSlot[2];

    /**
     * 윷놀이에서 말을 나타내는 클래스
     * @param player {@link Player}
     * @param slot 시작 칸
     */
    public Piece(Player player, BoardSlot slot) {
        this.count = 1;
        this.player = player;
        this.slot = slot;
    }

    public void printStatus(){
        System.out.println("Piece at: " + this.slot.num + ", count: " + this.count);
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

    private void calcMove(int steps){
        // 22번째 칸에서는 오던 방향에 따라 진행 방향이 다르기때문에 처리해주어야함
        // TODO: 말이 0번째 칸에 있는데 백도가 또 나오면 어떻게 처리해야할지
        // TODO: 아직 말이 완주를 했다는 판정이 없음
        for (int i = 0; i < 2; i++) {this.candidateSlots[i] = null;}
        if (steps == -1){
            if (this.slot.num == 0){
                this.candidateSlots[0] = this.slot;
                return;
            }
            this.candidateSlots[0] = this.slot.getPrev()[0];
            this.candidateSlots[1] = this.slot.getPrev()[1];
            return;
        }

        ArrayList<Integer> slots1 = new ArrayList<>(Arrays.asList(10, 25, 26));
        int moveOn22 = 0;
        if (slots1.contains(this.slot.num)){
            moveOn22 = 1;
        }
        for (int i = 0; i < 2; i++){
            if (this.slot.getNext()[i] != null){
                BoardSlot tempslot = this.slot.getNext()[i];
                for (int j = 0; j < steps-1; j++){
                    if(tempslot.num == 0 && steps-1 >= 0){
                        // 시작점으로 말이 되돌아 왔고 말이 나갈 수 있으면 그만 계산
                        this.candidateSlots[i] = new BoardSlot(-1); // 나갈 수 있음
                        return;
                    }
                    if (tempslot.num == 18){
                        tempslot = tempslot.getNext()[moveOn22];
                    } else {
                        tempslot = tempslot.getNext()[0];
                    }
                }
                this.candidateSlots[i] = tempslot;
            }
        }
    }

    public void showMove(int steps){
        if (steps < -1 || steps > 5 || steps == 0){
            System.out.println("Invalid move");
            return;
        }
        this.calcMove(steps);
        for (int i = 0; i < 2; i++){
            if (this.candidateSlots[i] != null){
                if (this.candidateSlots[i].num == -1){
                    System.out.println((i+1) + ": finish");
                } else {
                    System.out.println((i+1) + ": " + this.candidateSlots[i].num);
                }
            }
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
            System.out.println("Piece at " + this.slot.num + " already exists");
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

    /**
     * 윷놀이판을 나타내는 클래스
     * <pre><code>
     *(10)  (9)  (8)  (7)  (6)   (5)
     *(11) (25)            (20)  (4)
     *(12)      (26)   (21)      (3)
     *             (22)
     *(13)      (27)   (23)      (2)
     *(14) (28)            (24)  (1)
     *(15) (16) (17) (18)  (19)  (0)
     * </code></pre>
     */
    public Board(){
        this.slots = new BoardSlot[29];
        for (int i = 0; i < this.slots.length; i++) {
            this.slots[i] = new BoardSlot(i);
        }
        this.linkSlots();
    }

    private void linkSlots() {
        for (int i = 0; i < 19; i++) {
            this.slots[i].setNext(0, this.slots[i + 1]);
            this.slots[i + 1].setPrev(0, this.slots[i]);
        }
        for (int i = 20; i < 24; i++) {
            this.slots[i].setNext(0, this.slots[i+1]);
            this.slots[i + 1].setPrev(0, this.slots[i]);
        }
        this.slots[25].setNext(0, this.slots[26]);
        this.slots[25].setPrev(0, this.slots[10]);
        this.slots[26].setNext(0, this.slots[22]);
        this.slots[26].setPrev(0, this.slots[25]);
        this.slots[27].setNext(0, this.slots[28]);
        this.slots[27].setPrev(0, this.slots[22]);
        this.slots[28].setNext(0, this.slots[15]);
        this.slots[28].setPrev(0, this.slots[27]);
        this.slots[24].setNext(0, this.slots[0]);
        this.slots[19].setNext(0, this.slots[0]);
        this.slots[5].setNext(1, this.slots[20]);
        this.slots[10].setNext(1, this.slots[25]);
        this.slots[22].setNext(1, this.slots[27]);
        this.slots[0].setPrev(0, this.slots[19]);
        this.slots[0].setPrev(1, this.slots[24]);
        this.slots[15].setPrev(1, this.slots[28]);
        this.slots[20].setPrev(0, this.slots[5]);
        this.slots[22].setPrev(1, this.slots[26]);
    }


    public void testPrint(){
        for (int i = 0; i < 25; i++) {
            BoardSlot [] nextslot = this.slots[i].getNext();
            BoardSlot [] prevslot = this.slots[i].getPrev();
            System.out.print("Slot " + i + ": ");
            System.out.print("Prev: ");
            for (int j = 0; j < 2; j++) {
                if (prevslot[j] != null) {
                    System.out.print(prevslot[j].num + " ");
                }
            }
            System.out.print("Next: ");
            for (int j = 0; j < 2; j++) {
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
        return this.slots[0];
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
        System.out.println("Player " + this.player + " score " + score);
        this.score += score;
    }

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
