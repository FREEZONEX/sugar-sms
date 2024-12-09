package org.niiish32x.sugarsms.app.tools;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * SuposUserMocker
 *
 * @author shenghao ni
 * @date 2024.12.09 14:18
 */


public class SuposUserMocker {

    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%*()?.";
    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成符合规则的密码
     *
     * @return 生成的密码字符串
     */
    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        // 确保至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符
        password.append(UPPERCASE_CHARS.charAt(RANDOM.nextInt(UPPERCASE_CHARS.length())));
        password.append(LOWERCASE_CHARS.charAt(RANDOM.nextInt(LOWERCASE_CHARS.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));

        // 填充剩余字符
        String allChars = UPPERCASE_CHARS + LOWERCASE_CHARS + DIGITS + SPECIAL_CHARS;
        int remainingLength = RANDOM.nextInt(25) + 4; // 总长度在8-32位之间
        for (int i = 4; i < remainingLength; i++) {
            password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
        }

        return password.toString();
    }

    /**
     * 生成符合规则的用户名，6位以内由大小写组成
     *
     * @return 生成的用户名字符串
     */
    public static String generateUsername() {
        StringBuilder username = new StringBuilder();
        int length = RANDOM.nextInt(6) + 1;
        for (int i = 0; i < length; i++) {
            if (RANDOM.nextBoolean()) {
                username.append(UPPERCASE_CHARS.charAt(RANDOM.nextInt(UPPERCASE_CHARS.length())));
            } else {
                username.append(LOWERCASE_CHARS.charAt(RANDOM.nextInt(LOWERCASE_CHARS.length())));
            }
        }
        return username.toString();
    }
}
