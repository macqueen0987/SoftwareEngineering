package main.logic;

public class Board {

    private BoardSlot[] slots;
    private int polygon;

    public Board(int polygon) {
        int side = 5; int diagnal = 2;
        this.polygon = polygon;
        slots = new BoardSlot[(side+diagnal) * polygon + 1];
        for (int i = 0; i < slots.length; i++) slots[i] = new BoardSlot(i, polygon);
        linkSlots();
    }

    private void linkSlots() {
        int side = 5; int diagnal = 2;
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

    public BoardSlot getSlot(int num) { return slots[num]; }

    public BoardSlot getStart() { return slots[0]; }
}