package main.logic;

public class Sticks {

    private Stick[] sticks = new Stick[4];
    private boolean[] faces = new boolean[4];  // UI에서 사용할 앞/뒤 여부 저장
    private boolean backdo = false;

    public Sticks() {
        for (int i = 0; i < 4; i++) sticks[i] = new Stick();
    }

    /** 윷 던지기 */
    public int throwSticks() {
        int sum = 0;

        // 4개 윷 던지기
        for (int i = 0; i < 4; i++) {
            faces[i] = sticks[i].throwStick() == 1;
            sum += faces[i] ? 1 : 0;
        }

        // 백도 판별 (맨 앞만 1)
        backdo = (sum == 1 && faces[0]);

        // 결과 리턴 (도, 개, 걸, 윷, 모, 백도)
        if (sum == 0) return 5;   // 모
        if (sum == 1) return faces[0] ? -1 : 1;
        return sum;
    }

    /** UI 연동용 Getter */
    public boolean[] getFaces() {
        return faces;
    }

    public boolean isBackdo() {
        return backdo;
    }
}

