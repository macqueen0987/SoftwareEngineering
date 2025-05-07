package main;

import main.model.GameConfig;
import main.view.MainFrame;
import main.view.SetupDialog;

import javax.swing.SwingUtilities;

public class MainGUI {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            SetupDialog dlg = new SetupDialog(null);   // 설정 창
            GameConfig cfg = dlg.showDialog();         // 사용자가 "확인" 누르면 값 반환
            if (cfg != null) {
                new MainFrame(cfg);                    // 선택값으로 메인 화면 띄우기
            }
        });
    }
}
