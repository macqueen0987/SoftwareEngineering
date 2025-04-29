package org.example;

import javax.swing.*;
import java.awt.*;

public class YutnoriUI extends JFrame {

    public YutnoriUI() {
        setTitle("윷놀이 게임");
        setSize(800, 600); // 창 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 가운데 정렬

        BoardPanel boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(600, 400));

        JPanel buttonPanel = new JPanel();
        JButton randomButton = new JButton("랜덤 윷 던지기");
        JButton customButton = new JButton("지정 윷 던지기");

        buttonPanel.add(randomButton);
        buttonPanel.add(customButton);

        // 🧩 화면에 배치
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true); // 창 보이기
    }

    public static void main(String[] args) {
        new YutnoriUI();
    }
}