package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.commands.CrateCommand;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrateMoneyNanProofTest {

    @Test
    void addMoneyRewardMustRejectNonFiniteAmounts() throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> managerConstructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(CrateManager.class, objectConstructor);
        CrateManager manager = (CrateManager) managerConstructor.newInstance();

        assertFalse(manager.addMoneyReward("daily", 10, Double.NaN).success(),
                "addMoneyReward must reject NaN/Infinity before writeMoneyReward / grantMoneyReward");
        assertFalse(manager.addMoneyReward("daily", 10, Double.POSITIVE_INFINITY).success(),
                "addMoneyReward must reject positive infinity");
        assertFalse(manager.addMoneyReward("daily", 10, Double.NEGATIVE_INFINITY).success(),
                "addMoneyReward must reject negative infinity");
        assertFalse(manager.addMoneyReward("daily", 10, 0.0).success(),
                "addMoneyReward must reject zero amount");
        assertFalse(manager.addMoneyReward("daily", 10, -5.0).success(),
                "addMoneyReward must reject negative amount");
    }

    @Test
    void crateCommandParseDoubleRejectsNonFinite() throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> cmdConstructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(CrateCommand.class, objectConstructor);
        CrateCommand cmd = (CrateCommand) cmdConstructor.newInstance();

        Method parseDouble = CrateCommand.class.getDeclaredMethod("parseDouble", String.class);
        parseDouble.setAccessible(true);

        assertNull(parseDouble.invoke(cmd, "NaN"), "parseDouble must reject NaN");
        assertNull(parseDouble.invoke(cmd, "Infinity"), "parseDouble must reject Infinity");
        assertNull(parseDouble.invoke(cmd, "-Infinity"), "parseDouble must reject -Infinity");
        assertNull(parseDouble.invoke(cmd, "abc"), "parseDouble must reject non-numbers");
        assertEquals(10.5, (Double) parseDouble.invoke(cmd, "10.5"), 1.0e-9);
    }

    @Test
    void crateManagerGrantAndWriteAndParseGuardsArePresent() throws Exception {
        String managerSource = Files.readString(
                Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/CrateManager.java"));

        // Verify writeMoneyReward guards
        int writeStart = managerSource.indexOf("private void writeMoneyReward");
        int writeEnd = managerSource.indexOf("public ActionResult addShardsReward", writeStart);
        assertTrue(writeStart >= 0 && writeEnd > writeStart);
        String writeMethod = managerSource.substring(writeStart, writeEnd);
        assertTrue(writeMethod.contains("Double.isFinite(amount) && amount > 0D")
                || (writeMethod.contains("!Double.isFinite(amount)") && writeMethod.contains("amount <= 0D")),
                "writeMoneyReward must guard against non-finite or non-positive amounts");

        // Verify grantMoneyReward guards
        int grantStart = managerSource.indexOf("private boolean grantMoneyReward");
        int grantEnd = managerSource.indexOf("private boolean grantShardReward", grantStart);
        assertTrue(grantStart >= 0 && grantEnd > grantStart);
        String grantMethod = managerSource.substring(grantStart, grantEnd);
        assertTrue(grantMethod.contains("Double.isFinite"),
                "grantMoneyReward must check Double.isFinite");
        assertTrue(grantMethod.contains("<= 0D") || grantMethod.contains("<= 0"),
                "grantMoneyReward must check positive amount");

        // Verify CrateEditorMenu [MONEY] template guards
        String editorSource = Files.readString(
                Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/CrateEditorMenu.java"));
        int editorStart = editorSource.indexOf("display.startsWith(\"[MONEY] \")");
        int editorEnd = editorSource.indexOf("display.startsWith(\"[SHARDS] \")", editorStart);
        assertTrue(editorStart >= 0 && editorEnd > editorStart);
        String editorBlock = editorSource.substring(editorStart, editorEnd);
        assertTrue(editorBlock.contains("Double.isFinite"),
                "CrateEditorMenu must validate Double.isFinite for [MONEY]");
    }
}
