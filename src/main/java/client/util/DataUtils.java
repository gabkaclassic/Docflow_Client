package client.util;

import java.util.regex.Pattern;

/**
 * Класс-утилита для проверки валидности данных пользователя
 * */
public class DataUtils {
    
    private static final int MIN_LOGIN_LENGTH = 2;
    private static final int MAX_LOGIN_LENGTH = 64;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN_FIRST = Pattern.compile("[а-яА-Яa-zA-Z]+");
    private static final Pattern PASSWORD_PATTERN_SECOND = Pattern.compile("[0-9]+");
    private static final Pattern PASSWORD_PATTERN_THIRD = Pattern.compile("[^а-яА-Я0-9a-zA-Z]+");
    
    public static boolean checkLogin(String login) {
        
        return login != null && login.length() >= MIN_LOGIN_LENGTH && login.length() <= MAX_LOGIN_LENGTH;
    }
    
    public static boolean checkPassword(String password) {
    
        return password != null && password.length() >= MIN_PASSWORD_LENGTH
                && PASSWORD_PATTERN_FIRST.matcher(password).find()
                && PASSWORD_PATTERN_SECOND.matcher(password).find()
                && PASSWORD_PATTERN_THIRD.matcher(password).find();
    }
    
}
