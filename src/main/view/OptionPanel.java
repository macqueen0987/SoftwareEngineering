package main.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * JavaFX 버전의 OptionPanel 유틸리티 클래스
 */
public class OptionPanel {

    /**
     * 옵션 선택 드롭다운 창을 띄우고 선택값 반환 (0 ~ options.length-1)
     * @param title   창 제목
     * @param options 선택지 문자열 배열
     * @return 선택된 인덱스 (-1 = 취소)
     */
    public static int select(String title, String[] options) {
        return select(title, "", options);
    }

    /**
     * 옵션 선택 드롭다운 창을 띄우고 선택값 반환 (0 ~ options.length-1)
     * @param title   창 제목
     * @param message 본문 메시지
     * @param options 선택지 문자열 배열
     * @return 선택된 인덱스 (-1 = 취소)
     */
    public static int select(String title, String message, String[] options) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.length>0?options[0]:null, options);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.getDialogPane().setPrefWidth(300);

        return dialog.showAndWait()
                .map(choice -> java.util.Arrays.asList(options).indexOf(choice))
                .orElse(-1);
    }

    /**
     * 일반 텍스트 입력
     * @param message 메시지
     * @return 입력된 문자열 (취소 시 null)
     */
    public static String input(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("입력");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.getDialogPane().setPrefWidth(300);

        return dialog.showAndWait().orElse(null);
    }

    /**
     * 알림창
     * @param message 메시지
     */
    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
