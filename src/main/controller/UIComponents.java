package main.controller;

import javafx.scene.control.Button;
import main.view.BoardPanel;
import main.view.PieceSelectPanel;
import main.view.StatusPanel;
import main.view.StickPanel;

/**
 * JavaFX 전용 UIComponents
 */
public class UIComponents {
    public final BoardPanel      boardPanel;
    public final StickPanel      stickPanel;
    public final StatusPanel     statusPanel;
    public final Button          forceThrowButton;
    public final Button          randomThrowButton;
    public final Button          newPieceButton;
    public final PieceSelectPanel piecePanel;

    public UIComponents(BoardPanel boardPanel,
                        StickPanel stickPanel,
                        StatusPanel statusPanel,
                        Button randomThrowButton,
                        Button forceThrowButton,
                        Button newPieceButton,
                        PieceSelectPanel piecePanel) {
        this.boardPanel        = boardPanel;
        this.stickPanel        = stickPanel;
        this.statusPanel       = statusPanel;
        this.randomThrowButton = randomThrowButton;
        this.forceThrowButton  = forceThrowButton;
        this.newPieceButton    = newPieceButton;
        this.piecePanel        = piecePanel;
    }
}
