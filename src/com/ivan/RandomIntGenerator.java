package com.ivan;

public class RandomIntGenerator {
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
