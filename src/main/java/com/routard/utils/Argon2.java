package com.routard.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class Argon2 {
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 16;
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 2 ^ 24;
    private static final int PARALLELISM = 1;
    private static final String SUFFIX = "$argon2id$v=19$m=" + MEMORY +
            ",t=" + ITERATIONS +
            ",p=" + PARALLELISM +
            "$";

    private Argon2() {

    }

    public static String getHashedKey(String data) {
        return getEncode(data).substring(SUFFIX.length());
    }

    private static String getEncode(String data) {
        Argon2PasswordEncoder argon2 = new Argon2PasswordEncoder(SALT_LENGTH,KEY_LENGTH,PARALLELISM,MEMORY,ITERATIONS);

        return argon2.encode(data);
    }

    public static boolean validate(String rawData, String hashedData) {
        hashedData = SUFFIX + hashedData;
        return new Argon2PasswordEncoder(SALT_LENGTH,KEY_LENGTH,PARALLELISM,MEMORY,ITERATIONS).matches(rawData, hashedData);
    }
}

// GET =>  mails
// POST => creer une api key + retourner la clé
// PUT => renouveler clé + modifier le quota
