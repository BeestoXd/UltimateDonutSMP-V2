package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.ScoreboardStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The sidebar has two layouts and players pick between them, so the fallback chain that lets an
 * old scoreboard.yml keep working has to hold.
 */
class ScoreboardStyleLayoutTest {

    /** Calls the private readLayout, which only reads its arguments. */
    private static Object readLayout(FileConfiguration config, String path, Object fallback) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> managerConstructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(ScoreboardManager.class, objectConstructor);
        Object manager = managerConstructor.newInstance();

        Class<?> layoutType = Class.forName("com.bx.ultimateDonutSmp2.managers.ScoreboardManager$StyleLayout");
        Method readLayout = ScoreboardManager.class.getDeclaredMethod(
                "readLayout", FileConfiguration.class, String.class, layoutType);
        readLayout.setAccessible(true);
        return readLayout.invoke(manager, config, path, fallback);
    }

    @SuppressWarnings("unchecked")
    private static List<String> lines(Object layout) throws Exception {
        Method lines = layout.getClass().getDeclaredMethod("lines");
        lines.setAccessible(true);
        return (List<String>) lines.invoke(layout);
    }

    @SuppressWarnings("unchecked")
    private static List<String> titles(Object layout) throws Exception {
        Method titles = layout.getClass().getDeclaredMethod("titles");
        titles.setAccessible(true);
        return (List<String>) titles.invoke(layout);
    }

    private static String teamLine(Object layout) throws Exception {
        Method teamLine = layout.getClass().getDeclaredMethod("teamLine");
        teamLine.setAccessible(true);
        return (String) teamLine.invoke(layout);
    }

    private static YamlConfiguration shipped() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(Path.of("src/main/resources/scoreboard.yml").toFile());
        return config;
    }

    @Test
    void aStyleThatSetsNothingInheritsTheFallback() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("SCOREBOARD.TITLE", List.of("Fallback"));
        config.set("SCOREBOARD.LINES", List.of("one", "two"));
        config.set("SCOREBOARD.TEAM", "team line");

        Object flat = readLayout(config, "SCOREBOARD", null);
        Object legacy = readLayout(config, "SCOREBOARD.LEGACY", flat);

        assertEquals(List.of("Fallback"), titles(legacy));
        assertEquals(List.of("one", "two"), lines(legacy));
        assertEquals("team line", teamLine(legacy),
                "a scoreboard.yml written before the split must keep rendering");
    }

    @Test
    void aStyleOverridesOnlyWhatItDeclares() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("SCOREBOARD.TITLE", List.of("Fallback"));
        config.set("SCOREBOARD.LINES", List.of("one"));
        config.set("SCOREBOARD.TEAM", "team line");
        config.set("SCOREBOARD.LEGACY.TITLE", List.of("%economy_username%"));

        Object flat = readLayout(config, "SCOREBOARD", null);
        Object legacy = readLayout(config, "SCOREBOARD.LEGACY", flat);

        assertEquals(List.of("%economy_username%"), titles(legacy));
        assertEquals(List.of("one"), lines(legacy), "an unset LINES falls through, it does not blank out");
        assertEquals("team line", teamLine(legacy));
    }

    @Test
    void shippedConfigGivesTheTwoStylesDifferentTitles() throws Exception {
        YamlConfiguration config = shipped();

        Object flat = readLayout(config, "SCOREBOARD", null);
        Object modern = readLayout(config, "SCOREBOARD.MODERN", flat);
        Object legacy = readLayout(config, "SCOREBOARD.LEGACY", modern);

        assertFalse(titles(modern).isEmpty());
        assertFalse(titles(legacy).isEmpty());
        assertNotEquals(
                titles(modern),
                titles(legacy),
                "legacy puts the player's own name in the title; modern animates the server name"
        );
        assertTrue(
                titles(legacy).get(0).contains("%economy_username%"),
                "the legacy title is the player's name"
        );
    }

    @Test
    void styleParsingIsForgiving() {
        assertEquals(ScoreboardStyle.LEGACY, ScoreboardStyle.parse("legacy"));
        assertEquals(ScoreboardStyle.LEGACY, ScoreboardStyle.parse(" LeGaCy "));
        assertEquals(ScoreboardStyle.MODERN, ScoreboardStyle.parse("modern"));
        assertEquals(ScoreboardStyle.MODERN, ScoreboardStyle.parse(null), "a missing column reads as modern");
        assertEquals(ScoreboardStyle.MODERN, ScoreboardStyle.parse("nonsense"));
    }

    @Test
    void theStylesCycleIntoEachOther() {
        assertEquals(ScoreboardStyle.LEGACY, ScoreboardStyle.MODERN.next());
        assertEquals(ScoreboardStyle.MODERN, ScoreboardStyle.LEGACY.next());
        assertEquals("Legacy", ScoreboardStyle.LEGACY.display());
    }
}
