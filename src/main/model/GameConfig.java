package main.model;

/** 게임 시작 전에 선택한 설정값을 한 번에 담아 두기 위한 DTO */
public record GameConfig(
        String boardShape,   // "사각", "오각", "육각"
        int    teamCount,    // 2 ~ 4
        int    piecePerTeam  // 2 ~ 5
) {}
