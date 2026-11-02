package com.example.realisticcattle.entity;

public enum CattleSex {
    FEMALE,
    MALE;

    public static CattleSex fromId(int id) {
        return id == 1 ? MALE : FEMALE;
    }
}
