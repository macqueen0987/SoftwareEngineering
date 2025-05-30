package main.view;

import javax.swing.*;

/**
 * 옵션 선택 및 알림창을 띄우는 유틸리티 클래스
 */
public class OptionPanel {

    /**
     * 옵션 선택 드롭다운 창을 띄우고 선택값 반환 (0 ~ options.length-1)
     * @param title 창 제목
     * @param options 선택지 문자열 배열
     * @return 선택된 인덱스 (-1 = 취소)
     */
    public static int select(String title, String[] options) {
        return select(title, "", options);
    }

    /**
     * 옵션 선택 드롭다운 창을 띄우고 선택값 반환 (0 ~ options.length-1)
     * @param title 창 제목
     * @param message 본문 메시지
     * @param options 선택지 문자열 배열
     * @return 선택된 인덱스 (-1 = 취소)
     */
    public static int select(String title, String message, String[] options) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(message)); // 본문 메시지 추가
        panel.add(Box.createVerticalStrut(10)); // 간격
        panel.add(comboBox); // 드롭다운 추가

        int result = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return comboBox.getSelectedIndex();
        } else {
            return -1;
        }
    }


    /**
     * 일반 텍스트 입력
     */
    public static String input(String message) {
        return JOptionPane.showInputDialog(null, message, "입력", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 알림창
     */
    public static void alert(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}