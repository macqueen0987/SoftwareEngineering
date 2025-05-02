package view;

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

        /* -------- 1) 타원형 배경판 -------- */
        int stickCount = 4 + (backdo ? 1 : 0);        // 백도 포함 시 5개
        int stickGap   = 90;                          // 현재 간격
        int stickW     = 120;
        int totalW     = stickW + (stickCount - 1) * stickGap;   // 전체 폭 = 390(4개) or 480(5개)
        int startX     = 90;                          // 첫 윷의 x (기존 값)
        
        int ovalX = startX - 20;                      // 양쪽 20px 여유
        int ovalW = totalW  + 40;                     // 폭 +40px 여유
        int ovalY = 10;                               // 위쪽 10px 여유
        int ovalH = 200;                              

        g2.setColor(new Color(235, 202, 111));      
        g2.fillOval(ovalX, ovalY, ovalW, ovalH);

        /* -------- 2) 윷(막대) 그리기 -------- */
        int x = startX;

        for (int i = 0; i < 4; i++) {
            Image img = ResourceLoader.stick(faces[i]).getImage();
            g2.drawImage(img, x, 50, stickW, stickW, null); 
            x += stickGap;
        }
        if (backdo) {
            Image bd = ResourceLoader.backdo().getImage();
            g2.drawImage(bd, x, 20, stickW, stickW, null);
        }
    }
}
