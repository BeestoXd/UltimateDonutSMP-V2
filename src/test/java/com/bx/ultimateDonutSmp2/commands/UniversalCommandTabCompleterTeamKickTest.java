package com.bx.ultimateDonutSmp2.commands;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniversalCommandTabCompleterTeamKickTest {

    private static final Path COMMAND = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/commands/UniversalCommandTabCompleter.java");

    @Test
    void teamKickTabCompleteDoesNotQuerySqlForOfflineMemberNames() throws Exception {
        String source = Files.readString(COMMAND);
        int start = source.indexOf("private List<String> teamMemberNames");
        int end = source.indexOf("private List<String> duelArenaIds");
        assertTrue(start >= 0 && end > start, "teamMemberNames should still be the kick-completion helper");

        String method = source.substring(start, end);
        assertFalse(
                method.contains("getLastKnownUsername"),
                "Tab completion runs on the main thread. Resolving offline team members "
                        + "through DatabaseManager.getLastKnownUsername hits SQL on a cache miss."
        );
    }
}
