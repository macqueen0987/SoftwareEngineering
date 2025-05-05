package main.view;

import javax.swing.*;

public class OptionPanel {

    /**
     * 옵션 선택 창을 띄우고 선택값 반환 (0 ~ options.length-1)
     * @param message 메시지
     * @param options 선택지 문자열 배열
     * @return 선택된 인덱스 (왼쪽부터 0,1,2...)
     */
    public static int select(String message, String[] options) {
        return JOptionPane.showOptionDialog(null,
                message,
                "경로 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }
}