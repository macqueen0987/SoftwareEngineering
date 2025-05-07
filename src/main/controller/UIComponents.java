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
    public final JButton throwButton;
    public final JButton newPieceButton;

    public UIComponents(BoardPanel boardPanel, StickPanel stickPanel, StatusPanel statusPanel,
                        JButton throwButton, JButton newPieceButton) {
        this.boardPanel = boardPanel;
        this.stickPanel = stickPanel;
        this.statusPanel = statusPanel;
        this.throwButton = throwButton;
        this.newPieceButton = newPieceButton;
    }
}
