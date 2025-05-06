package main.view;

import main.model.GameConfig;

import javax.swing.*;
import java.awt.*;

public class SetupDialog extends JDialog {

    private GameConfig config = null;          // 사용자가 고른 최종 결과

    public SetupDialog(Frame owner) {
        super(owner, "게임 설정", true);        // 모달 다이얼로그
        setLayout(new GridLayout(4, 2, 10, 10));
        setPreferredSize(new Dimension(300, 200));

        /* ── 1) 보드 모양 ─────────────────── */
        add(new JLabel("보드 모양"));
        JComboBox<String> cboShape =
                new JComboBox<>(new String[]{"사각", "오각", "육각"});
        add(cboShape);

        /* ── 2) 팀 수 ──────────────────────── */
        add(new JLabel("팀 수"));
        JComboBox<Integer> cboTeam =
                new JComboBox<>(new Integer[]{2, 3, 4});
        add(cboTeam);

        /* ── 3) 팀당 말 개수 ───────────────── */
        add(new JLabel("말 개수"));
        JComboBox<Integer> cboPiece =
                new JComboBox<>(new Integer[]{2, 3, 4, 5});
        add(cboPiece);

        /* ── 4) 확인 버튼 ──────────────────── */
        JButton ok = new JButton("확인");
        ok.addActionListener(e -> {
            config = new GameConfig(
                    (String)  cboShape.getSelectedItem(),
                    (Integer) cboTeam.getSelectedItem(),
                    (Integer) cboPiece.getSelectedItem()
            );
            dispose();                         // 창 닫기
        });
        add(ok);

        pack();
        setLocationRelativeTo(owner);
    }

    /** 다이얼로그를 띄우고 선택값을 돌려준다. (취소 시 null) */
    public GameConfig showDialog() {
        setVisible(true);
        return config;
    }
}
