package com.routard.utils;

public class Main {
    public static void main(String[] args) {
        String client = "routard";

        String apiKey = Argon2.getHashedKey(client);
        System.out.println(apiKey);
    }
}
