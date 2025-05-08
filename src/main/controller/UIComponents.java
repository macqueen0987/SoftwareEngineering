package main.controller;

import main.view.BoardPanel;
import main.view.PieceSelectPanel;
import main.view.StatusPanel;
import main.view.StickPanel;

import javax.swing.*;

public class UIComponents {

    public final BoardPanel boardPanel;
    public final StickPanel stickPanel;
    public final StatusPanel statusPanel;
    public final JButton forceThrowButton;
    public final JButton randomThrowButton;
    public final JButton newPieceButton;
    public final PieceSelectPanel piecePanel;

    public UIComponents(BoardPanel boardPanel, StickPanel stickPanel, StatusPanel statusPanel,
                        JButton randomThrowButton, JButton forceThrowButton,JButton newPieceButton, PieceSelectPanel pp) {
        this.boardPanel = boardPanel;
        this.stickPanel = stickPanel;
        this.statusPanel = statusPanel;
        this.forceThrowButton = forceThrowButton;
        this.randomThrowButton = randomThrowButton;
        this.newPieceButton = newPieceButton;
        this.piecePanel = pp;
    }
}
