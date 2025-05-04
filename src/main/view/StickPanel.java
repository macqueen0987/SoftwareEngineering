package main.view;

import javax.swing.*;
import java.awt.*;

public class StickPanel extends JPanel {

    private boolean[] faces = {true, true, true, true};
    private boolean   backdo = false;

    public StickPanel() {
        setPreferredSize(new Dimension(540, 170));   // 기존 크기 그대로
        setOpaque(false);
    }

    public void setFaces(boolean[] arr, boolean isBackdo) {
        if (arr == null || arr.length != 4) return;
        faces  = arr.clone();
        backdo = isBackdo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        /* 1) 타원형 배경판 */
        int stickGap = 90;                 // 막대 간격
        int stickW   = 120;
        int startX   = 90;
        int totalW   = stickW + 3 * stickGap;      // 항상 4개만 그리므로 3칸 간격
        int ovalX    = startX - 20;
        int ovalW    = totalW + 40;
        int ovalY    = 10;
        int ovalH    = 200;

        g2.setColor(new Color(235, 202, 111));
        g2.fillOval(ovalX, ovalY, ovalW, ovalH);

        /* 2) 윷(막대) 그리기 */
        int x = startX;
        boolean backdoDrawn = false;       // 백도 이미지를 이미 그렸는지 플래그

        for (int i = 0; i < 4; i++) {
            Image img;
            if (backdo && !faces[i] && !backdoDrawn) {
                // 뒤면( false ) 중 첫 번째를 백도로 교체
                img = ResourceLoader.backdo().getImage();
                backdoDrawn = true;
            } else {
                img = ResourceLoader.stick(faces[i]).getImage();
            }
            g2.drawImage(img, x, 50, stickW, stickW, null);
            x += stickGap;
        }
    }
}