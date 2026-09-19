package com.jinanmuseum.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdCardValidatorSimpleTest {

    @Test
    void 种子数据中的身份证应该通过() {
        // 来自种子数据的两个身份证
        assertTrue(IdCardValidator.isValid("37010219900110274X"));
        assertTrue(IdCardValidator.isValid("370102199001104112"));
    }

    @Test
    void 检查各个身份证的校验结果() {
        // 打印每个身份证的校验结果
        System.out.println("37010219900110274X: " + IdCardValidator.isValid("37010219900110274X"));
        System.out.println("370102199001104112: " + IdCardValidator.isValid("370102199001104112"));
        System.out.println("370102199001105481: " + IdCardValidator.isValid("370102199001105481"));
        System.out.println("370102199001106855: " + IdCardValidator.isValid("370102199001106855"));
        System.out.println("110101199003078856: " + IdCardValidator.isValid("110101199003078856"));
        System.out.println("44030119910101523X: " + IdCardValidator.isValid("44030119910101523X"));
    }

    @Test
    void 确认至少种子数据身份证通过() {
        // 至少确认种子数据中的身份证应该通过
        assertTrue(IdCardValidator.isValid("37010219900110274X"));
        assertTrue(IdCardValidator.isValid("370102199001104112"));
    }
}