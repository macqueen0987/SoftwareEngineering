package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.model.GameConfig;
import main.view.SetupDialog;  // JavaFX 버전 SetupDialog
import main.view.MainFrame;    // JavaFX 버전 MainFrame

/**
 * JavaFX 애플리케이션 진입점
 */
public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // JavaFX 다이얼로그 호출 (no-arg 생성자 사용)
        SetupDialog dlg = new SetupDialog(primaryStage);
        GameConfig cfg = dlg.showDialog();
        if (cfg == null) {
            primaryStage.close();
            return;
        }

        // JavaFX 메인 화면 표시
        new MainFrame(primaryStage, cfg);
    }
}
