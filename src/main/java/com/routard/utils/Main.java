package com.routard.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        String client = "routard";

        String apiKey = Argon2.getHashedKey(client);
        System.out.println(apiKey + " lengh="+apiKey.length());


        LocalDate date = LocalDate.now();
        System.out.println("date : " + date);

        LocalDateTime time = LocalDateTime.now();
        System.out.println("time : " + time);
        LocalTime heure = time.toLocalTime();
        System.out.println("Heure actuelle : " + heure);
    }
}
