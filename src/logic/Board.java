package logic;

public class Board {

    private BoardSlot[] slots = new BoardSlot[29];

    public Board() {
        for (int i = 0; i < slots.length; i++) slots[i] = new BoardSlot(i);
        linkSlots();
    }

    private void linkSlots() {
        for (int i = 0; i < 19; i++) {
            slots[i].setNext(0, slots[i + 1]);
            slots[i + 1].setPrev(0, slots[i]);
        }
        for (int i = 20; i < 24; i++) {
            slots[i].setNext(0, slots[i + 1]);
            slots[i + 1].setPrev(0, slots[i]);
        }
        slots[24].setNext(0, slots[0]);
        slots[0].setPrev(0, slots[19]);
        slots[0].setPrev(1, slots[24]);
    }

    public BoardSlot getSlot(int num) { return slots[num]; }

    public BoardSlot getStart() { return slots[0]; }
}