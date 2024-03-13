package com.routard.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidEmailAddress(String email) {
        String emailPattern = "^[a-zA-Z0-9_\\-+.%]+@[a-zA-Z0-9_\\-.]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
