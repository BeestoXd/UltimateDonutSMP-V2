package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignInputUtilTest {

    @Test
    void helperPromptsAreIdentified() {
        assertTrue(SignInputUtil.isHelperPrompt("", null));
        assertTrue(SignInputUtil.isHelperPrompt("   ", null));
        assertTrue(SignInputUtil.isHelperPrompt("^^^^^^^^^^^^^^^", null));
        assertTrue(SignInputUtil.isHelperPrompt("↑↑↑↑↑", null));
        assertTrue(SignInputUtil.isHelperPrompt("↑↑↑↑↑↑↑↑↑↑↑↑↑↑", null));
        assertTrue(SignInputUtil.isHelperPrompt("Search", null));
        assertTrue(SignInputUtil.isHelperPrompt("Enter Value", null));
        assertTrue(SignInputUtil.isHelperPrompt("Type price", null));
        assertTrue(SignInputUtil.isHelperPrompt("Player Name", null));
        assertTrue(SignInputUtil.isHelperPrompt("Amount", null));
        assertTrue(SignInputUtil.isHelperPrompt("Price", null));
        assertTrue(SignInputUtil.isHelperPrompt("Search", "Search"));
        assertTrue(SignInputUtil.isHelperPrompt("Expected Line", "Expected Line"));
    }

    @Test
    void userInputsAreAccepted() {
        assertFalse(SignInputUtil.isHelperPrompt("diamond", "↑↑↑↑↑"));
        assertFalse(SignInputUtil.isHelperPrompt("netherite ingot", "Search"));
        assertFalse(SignInputUtil.isHelperPrompt("1000", "Type price"));
        assertFalse(SignInputUtil.isHelperPrompt("BeestoXd", ""));
        assertFalse(SignInputUtil.isHelperPrompt("wood", null));
    }
}