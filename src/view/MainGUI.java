package view;

import model.GameConfig;

import javax.swing.SwingUtilities;

/** GUI 실행 진입점 (임시로 view 패키지에 둠) */
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
