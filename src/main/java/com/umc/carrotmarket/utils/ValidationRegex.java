package com.umc.carrotmarket.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {

    // 이메일 형식 체크
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 비밀번호 형식 체크(최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상)
    public static boolean isRegexPassword(String target){
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 전화 번호 형식 체크(11자리)

    /**
     * 핸드폰번호 첫/두번째 자리는 01로 시작하며 세번째 자리는 01+0/1/6/7/8/9 가 될 수 있다.
     * 번호 사이사이 대쉬('-')는 사용자가 작성하든 안하든 무시한다.
     * 번호 두번째 마디는 3-4자리가 가능하며 숫자는 0-9까지 들어올 수 있다.
     * 마지막 마디는 마찬가지로 0-9까지 가능하며 4자리만 가능하다.
     */
    public static boolean isRegexPhoneNum(String target){
        String regex = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

}
