package com.ou.utils;

public class DesensitizationUtil {
    /**
     * 邮箱脱敏：保留前3位和域名，中间加星号
     * 例：test@example.com → tes***t@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0], domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "*@" + domain;
        } else if(name.length() == 3) {
            return name.charAt(0) + "***" + name.charAt(2) + "@" + domain;
        } else {
            return name.substring(0, 3) + "***" + name.charAt(name.length() - 1) + "@" + domain;
        }
    }

    /**
     * 手机号脱敏：中间四位星号
     * 例：13812348888 → 138****8888
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 身份证号脱敏：保留前6位和后4位，其余星号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        return idCard.replaceAll("(\\d{6})\\d+(\\d{4})", "$1********$2");
    }
}
