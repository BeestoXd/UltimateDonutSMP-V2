package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablistConfigurationTest {

    private static final String HEADER_TITLE = "<#00A6FF><bold>UltimateDonutSMP V2";

    @Test
    void tablistHeaderUsesUltimateDonutSmpV2InBlueBold() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));

        assertTrue(config.getBoolean("TABLIST.ENABLED"));
        assertFalse(config.getBoolean("TABLIST.SHOW-TEAM-NAME"));
        assertEquals("📹", config.getString("TABLIST.ICON-MEDIA"));
        assertEquals("<#FF00A6><icon_media>", config.getString("TABLIST.MEDIA-BADGE-FORMAT"));
        assertEquals("media", config.getString("TABLIST.MEDIA-BADGE-PERMISSION"));
        assertEquals("<#00A4FC>+", config.getString("TABLIST.DONUT-PLUS-FORMAT"));
        assertEquals("<#00A4FC>++", config.getString("TABLIST.DONUT-PLUS-PLUS-FORMAT"));
        assertEquals("<#00A4FC>+++", config.getString("TABLIST.DONUT-PLUS-PLUS-PLUS-FORMAT"));
        assertEquals("<media_badge>%prefix%<donut_badge>&f<nick>%team_suffix%", config.getString("TABLIST.NAME-FORMAT"));

        List<String> header = config.getStringList("TABLIST.HEADER");
        assertEquals(4, header.size());
        assertEquals("", header.get(0));
        assertEquals(HEADER_TITLE, header.get(1));
        assertEquals("&f%online_compact% Players", header.get(2));
        assertEquals("", header.get(3));

        String rendered = ColorUtils.colorize(header.get(1));
        assertTrue(rendered.contains("UltimateDonutSMP V2"), rendered);
        assertFalse(rendered.contains("Donut SMP"), rendered);
        assertTrue(rendered.startsWith("\u00A7x\u00A70\u00A70\u00A7A\u00A76\u00A7F\u00A7F"), rendered);
        assertTrue(rendered.contains("\u00A7lUltimateDonutSMP V2"), rendered);

        List<String> footer = config.getStringList("TABLIST.FOOTER");
        assertEquals(3, footer.size());
        assertEquals("", footer.get(0));
        assertEquals("<#00FF00>$ &f%money% <#00FF00>• &f%ping% ms", footer.get(1));
        assertEquals("", footer.get(2));
    }

    @Test
    void formatCompactCountFormatsCorrectly() throws Exception {
        Method method = TablistManager.class.getDeclaredMethod("formatCompactCount", long.class);
        method.setAccessible(true);

        TablistManager manager = new TablistManager(null);

        assertEquals("0", method.invoke(manager, 0L));
        assertEquals("5", method.invoke(manager, 5L));
        assertEquals("999", method.invoke(manager, 999L));
        assertEquals("1K", method.invoke(manager, 1_000L));
        assertEquals("1.5K", method.invoke(manager, 1_500L));
        assertEquals("38.5K", method.invoke(manager, 38_500L));
        assertEquals("1M", method.invoke(manager, 1_000_000L));
        assertEquals("2.5M", method.invoke(manager, 2_500_000L));
    }

    @Test
    void applyInternalPlaceholdersHandlesNullPlayerGracefully() throws Exception {
        Method method = TablistManager.class.getDeclaredMethod("applyInternalPlaceholders", String.class, org.bukkit.entity.Player.class);
        method.setAccessible(true);

        TablistManager manager = new TablistManager(null);

        String template = HEADER_TITLE + "\n&f%online_compact% Players\n<#00FF00>$ &f%money% <#00FF00>• &f%ping% ms";
        String resolved = (String) method.invoke(manager, template, null);

        assertNotNull(resolved);
        assertFalse(resolved.contains("%online_compact%"));
        assertFalse(resolved.contains("%money%"));
        assertFalse(resolved.contains("%ping%"));
        assertTrue(resolved.contains(HEADER_TITLE));
        assertTrue(resolved.contains("Players"));
        assertTrue(resolved.contains("ms"));
    }

    @Test
    void nameFormatPlacesMediaBeforePrefixAndDonutBadgeBeforeNickname() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
        String format = config.getString("TABLIST.NAME-FORMAT");
        assertNotNull(format);

        int mediaIndex = format.indexOf("<media_badge>");
        int donutIndex = format.indexOf("<donut_badge>");
        int nickIndex = format.indexOf("<nick>");

        assertTrue(mediaIndex >= 0, "must contain <media_badge>");
        assertTrue(donutIndex >= 0, "must contain <donut_badge>");
        assertTrue(nickIndex >= 0, "must contain <nick>");

        assertTrue(mediaIndex < donutIndex, "<media_badge> must come before <donut_badge> (📹++<nick>)");
        assertTrue(donutIndex < nickIndex, "<donut_badge> must come before <nick> (++<nick>, not <nick>++)");
    }
}
