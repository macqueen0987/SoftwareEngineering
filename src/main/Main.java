package main;

import java.util.ArrayList;
import java.util.Scanner;

class Game {
    private Player [] players;
    private Board board;
    private Sticks sticks;
    private ArrayList<Integer> stickResults;
    private MoveCalculator calculator;


    /**
     * 새로운 윷놀이 게임을 생성
     * @param playerCount 플레이어 수
     * @param piecePerPlayer 각 플레이어의 말 개수
     */
    public Game(int playerCount, int piecePerPlayer, int polygon) {
        int side = 5;
        int diagnal = 2;
        this.board = new Board(side, diagnal, polygon);    //TODO user input으로 값을 받도록 수정
        this.players = new Player[playerCount];
        this.sticks = new Sticks();
        for (int i = 0; i < playerCount; i++) {
            this.players[i] = new Player(i, piecePerPlayer);
        }
        this.stickResults = new ArrayList<>();
        this.calculator = new MoveCalculator(side, diagnal, polygon);
    }

    public void testprint(){
        // 4각형 board의 경우
        System.out.println("(10)  (9)  (8)  (7)  (6)   (5)");
        System.out.println("(11) (23)            (21)  (4)");
        System.out.println("(12)      (24)   (22)      (3)");
        System.out.println("             (20)");
        System.out.println("(13)      (25)   (27)      (2)");
        System.out.println("(14) (26)            (28)  (1)");
        System.out.println("(15) (16) (17) (18)  (19)  (0)");
    }

    /**
     * 윷을 던지는 메소드
     */
    private void throwSticks(){
        int resultValue;
        resultValue = sticks.throwSticks();
        this.stickResults.add(resultValue);
        while (resultValue == 4 || resultValue == 5) {
            resultValue = sticks.throwSticks();
            this.stickResults.add(resultValue);
        }
    }

    private void displayStickResults(){
        System.out.println("main.Sticks thrown: ");
        for (int i = 0; i < this.stickResults.size(); i++) {
            System.out.print((i+1) + ": " + this.stickResults.get(i) + " ");
        }
        System.out.println();
    }

    private void applyMove(Piece piece, int move){
        piece.move(move);
    }

    private boolean handleTurn(Player player) {
        clearScreen();
        this.testprint();
        //board.testPrint();
        System.out.println("main.Player " + player.getPlayer() + "'s turn");
        System.out.print("Press Enter to throw the sticks...");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equals("exit")){
            System.out.println("Exiting game...");
            return false;
        }
        this.throwSticks();
        while (true){
            this.displayStickResults();
            int moveValue = this.selectThrow();
            Piece selectedPiece = this.selectPiece(player, moveValue);
            int move = this.selectMove(selectedPiece, moveValue);
            if (selectedPiece.move(move)){
                System.out.println("You caught a piece! Throwing again.");
                this.throwSticks();
            }
            if (this.stickResults.isEmpty()){
                break;
            }
        }
        return true;
    }

    private int selectMove(Piece piece, int steps){
        Scanner scanner = new Scanner(System.in);
        int selectedSlotIndex;
        while (true){
            piece.showMove(steps);
            System.out.print("Select a slot to move to: ");
            selectedSlotIndex = scanner.nextInt();
            selectedSlotIndex--;
            if (selectedSlotIndex < 0 || selectedSlotIndex > 1) {
                System.out.println("Invalid slot. Try again.");
                continue;
            }
            break;
        }
        return selectedSlotIndex;
    }

    private int selectThrow(){
        Scanner scanner = new Scanner(System.in);
        int selectedThrow;
        while (true){
            System.out.print("Select a throw: ");
            selectedThrow = scanner.nextInt();
            selectedThrow--;
            if (selectedThrow < 0 || selectedThrow >= this.stickResults.size()) {
                System.out.println("Invalid throw. Try again.");
                continue;
            }
            break;
        }
        int resultValue = this.stickResults.get(selectedThrow);
        this.stickResults.remove(selectedThrow);
        return resultValue;
    }

    private Piece selectPiece(Player player, int steps){
        Scanner scanner = new Scanner(System.in);
        Piece selectedPiece;
        Piece[] pieces = player.getPieces();
        for (Piece piece : pieces) {
            piece.printStatus();
        }
        if (pieces.length == 0) {
            System.out.println("You have no pieces to move. Creating a new piece.");
            selectedPiece = player.addPiece(board.getFirstSlot());
            return selectedPiece;
        }
        System.out.println("Your pieces: ");
        for (int i = 0; i < pieces.length; i++) {
            System.out.println((i+1) + ": " + pieces[i].getSlotNum());
        }
        System.out.print("Select a piece to move, 0 for new piece: ");
        while (true){
            int selectedPieceIndex = scanner.nextInt();
            selectedPieceIndex--;
            if (selectedPieceIndex < -1 || selectedPieceIndex >= pieces.length) {
                System.out.println("Invalid piece. Try again.");
                continue;
            }
            if (selectedPieceIndex < 0) {
                if (!player.checkNewPieceAvailable()) {
                    System.out.println("You have reached the maximum number of pieces. You cannot create a new piece.");
                    continue;
                }
                if (steps == -1) {
                    System.out.println("You cannot create a new piece with a negative move.");
                    continue;
                }
                // 새로운 말을 생성
                System.out.println("Creating a new piece.");
                selectedPiece = player.addPiece(board.getFirstSlot());
                break;
            }
            selectedPiece = pieces[selectedPieceIndex];
            break;
        }
        return selectedPiece;
    }

    public void play(){
        // 일단 텍스트 기반으로 구현
        int turn = 0;
        boolean keepGoing = true;
        while (true){
            Player player = this.players[turn];
            keepGoing = this.handleTurn(player);
            if (!keepGoing) {
                break;
            }

            turn++;
            if (turn >= players.length) {
                turn = 0;
            }
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i  < 50; i++) {
            System.out.println();
        }
    }

}

public class Main {
    public static void main(String[] args) {
        Game game = new Game(1, 4, 4);  // default: 4
        game.play();
    }
}

