package main.view;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Flow;

public class StickPanel extends JPanel implements Flow.Subscriber<boolean[]>{

    private boolean[] faces = {true, true, true, true};
    private boolean   backdo = false;
    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(boolean[] item) {
        setFaces(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("StickPanel error: " + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("StickPanel updates complete");
    }

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

    public void setFaces(boolean[] arr){
        faces = Arrays.copyOfRange(arr,0,4);
        backdo = arr[4];
        repaint();
    }

    public int getResult(){
        int result = 0;
        if(backdo) return -1;
        for(int i = 0; i < 4; i ++){
            if(faces[i]) result++;
        }
        if(result == 0) return 5;
        return result;
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
        boolean isRealBackdo = (faces[0] && !faces[1] && !faces[2] && !faces[3]);

        for (int i = 0; i < 4; i++) {
            Image img;

            if (isRealBackdo) {
                if (i == 0) {
                    img = ResourceLoader.backdo().getImage(); // 백도
                } else {
                    img = ResourceLoader.stick(true).getImage(); // 나머지 앞면
                }
            } else {
                img = ResourceLoader.stick(faces[i]).getImage(); // 일반 상황
            }

            g2.drawImage(img, x, 50, stickW, stickW, null);
            x += stickGap;
        }
    }
}