package main.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import main.model.GameConfig;

/**
 * JavaFX 버전의 SetupDialog (Dialog<GameConfig>)
 */
public class SetupDialog extends Dialog<GameConfig> {
    public SetupDialog(Window owner) {
        setTitle("게임 설정");
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        ComboBox<String> cboShape = new ComboBox<>(FXCollections.observableArrayList("사각", "오각", "육각"));
        ComboBox<Integer> cboTeam  = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4));
        ComboBox<Integer> cboPiece = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4, 5));
        cboShape.getSelectionModel().selectFirst();
        cboTeam.getSelectionModel().selectFirst();
        cboPiece.getSelectionModel().selectFirst();

        grid.add(new Label("보드 모양"), 0, 0);
        grid.add(cboShape, 1, 0);
        grid.add(new Label("팀 수"),   0, 1);
        grid.add(cboTeam,  1, 1);
        grid.add(new Label("말 개수"), 0, 2);
        grid.add(cboPiece, 1, 2);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new GameConfig(
                        cboShape.getValue(),
                        cboTeam.getValue(),
                        cboPiece.getValue()
                );
            }
            return null;
        });
    }

    /**
     * 다이얼로그를 띄우고 선택값 반환 (취소 시 null)
     */
    public GameConfig showDialog() {
        return showAndWait().orElse(null);
    }
}
