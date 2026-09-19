package com.jinanmuseum.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class IdCardValidatorTest {

    @Test
    void shouldValidateValidIdCardWithX() {
        String validId = "37010219900110274X";
        assertTrue(IdCardValidator.isValid(validId));
        assertEquals("37010219900110274X", IdCardValidator.normalize(validId));
    }

    @Test
    void shouldValidateValidIdCardWithNumber() {
        String validId = "370102199001104112";
        assertTrue(IdCardValidator.isValid(validId));
        assertEquals("370102199001104112", IdCardValidator.normalize(validId));
    }

    @Test
    void shouldNormalizeLowercaseX() {
        String idWithLowerX = "37010219900110274x";
        assertTrue(IdCardValidator.isValid(idWithLowerX));
        assertEquals("37010219900110274X", IdCardValidator.normalize(idWithLowerX));
    }

    @Test
    void shouldRejectInvalidChecksum() {
        String invalidChecksum = "370102199001104113"; // Last digit should be 2
        assertFalse(IdCardValidator.isValid(invalidChecksum));
    }

    @Test
    void shouldRejectWrongLength() {
        String tooShort = "37010219900110";
        String tooLong = "370102199001102741X";
        assertFalse(IdCardValidator.isValid(tooShort));
        assertFalse(IdCardValidator.isValid(tooLong));
    }

    @Test
    void shouldRejectInvalidCharacters() {
        String withLetters = "3701021990011027AA";
        String withSymbols = "37010219900110271@";
        assertFalse(IdCardValidator.isValid(withLetters));
        assertFalse(IdCardValidator.isValid(withSymbols));
    }

    @Test
    void shouldRejectNullAndEmpty() {
        assertFalse(IdCardValidator.isValid(null));
        assertFalse(IdCardValidator.isValid(""));
        assertFalse(IdCardValidator.isValid("   "));
    }

    @Test
    void shouldRejectWithSpaces() {
        String withSpaces = "3701021990011027 X";
        assertFalse(IdCardValidator.isValid(withSpaces));
    }

    @Test
    void shouldRejectShortDigitsOnly() {
        String digitsOnly = "12345678901234567";
        assertFalse(IdCardValidator.isValid(digitsOnly));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "37010219900110274X",  // Valid with X (from seed data)
            "370102199001104112",  // Valid with number (from seed data)
            "370102199001105481",  // Another valid ID (from seed data)
            "370102199001106855"   // Another valid ID (from seed data)
    })
    void shouldAcceptValidIdCards(String id) {
        assertTrue(IdCardValidator.isValid(id), "Should accept valid ID: " + id);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "37010219900110274",   // Too short
            "37010219900110274X1", // Too long
            "37010219900110274Y",  // Invalid character
            "370102199001104113",  // Invalid checksum (should be 2)
            "AAAAAAAAAAAAAAAAAA",  // All letters
            "12345678901234567",   // Short with wrong checksum
            "3701021990011027X",   // Too short with X
            "37010219900A274X",     // Letter in middle
            "11010119900307885X",   // Wrong checksum (should be 6)
            "44030119910101523Y"    // Wrong checksum (should be X)
    })
    void shouldRejectInvalidIdCards(String id) {
        assertFalse(IdCardValidator.isValid(id), "Should reject invalid ID: " + id);
    }
}