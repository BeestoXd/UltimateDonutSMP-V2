package com.bx.ultimateDonutSmp2.dialogs;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogActionsTest {

    @Test
    void aBareActionIsAssumedToBeOurs() {
        Key key = DialogActions.key("homes_menu");

        assertEquals(DialogActions.NAMESPACE, key.namespace());
        assertEquals("homes_menu", key.value());
    }

    @Test
    void aNamespacedActionKeepsItsNamespace() {
        Key key = DialogActions.key("otherplugin:some_screen");

        assertEquals("otherplugin", key.namespace());
        assertEquals("some_screen", key.value());
        assertFalse(DialogActions.isOurs(key), "another plugin's handler must not be routed to ours");
    }

    @Test
    void uuidsSurviveBeingEmbeddedInAnActionId() {
        UUID target = UUID.fromString("0e3f1a2b-4c5d-6e7f-8a9b-0c1d2e3f4a5b");
        Key key = DialogActions.key(DialogActions.NAMESPACE + ':' + DialogActions.PAY_TARGET + target);

        assertEquals(DialogActions.PAY_TARGET + target, key.value());
        assertEquals(target.toString(), DialogActions.argument(key.value(), DialogActions.PAY_TARGET));
    }

    @Test
    void charactersAResourceLocationCannotHoldAreDropped() {
        // Player names are mixed case and dialog ids are not, so the fold has to be lossy but safe.
        assertEquals("notch", DialogActions.sanitise("Notch"));
        assertEquals("someone", DialogActions.sanitise("Some One"));
        assertEquals("bad", DialogActions.sanitise("b#a$d"));
        assertEquals("", DialogActions.sanitise(null));
    }

    @Test
    void anEmptyOrBrokenActionYieldsNoKey() {
        assertNull(DialogActions.key(null));
        assertNull(DialogActions.key("   "));
        assertNull(DialogActions.key("namespace:"), "an id with nothing after the colon addresses nothing");
        assertNull(DialogActions.key(":value"));
    }

    @Test
    void argumentOnlyMatchesItsOwnPrefix() {
        assertEquals("4", DialogActions.argument("manage_home_4", DialogActions.HOME_MANAGE));
        assertNull(
                DialogActions.argument("teleport_home_4", DialogActions.HOME_MANAGE),
                "teleport and manage share a suffix, so the prefix is what tells them apart"
        );
    }

    @Test
    void ourOwnKeysAreRecognised() {
        assertTrue(DialogActions.isOurs(DialogActions.of(DialogActions.MAIN_MENU)));
        assertFalse(DialogActions.isOurs(Key.key("minecraft", "custom")));
        assertFalse(DialogActions.isOurs(null));
    }

    @Test
    void ordersActionBuildsOurNamespacedKey() {
        Key ordersKey = DialogActions.key("ultimatedonutsmp2:orders");
        assertNotNull(ordersKey);
        assertTrue(DialogActions.isOurs(ordersKey));
        assertEquals(DialogActions.ORDERS, ordersKey.value());

        Key ordersMenuKey = DialogActions.key("orders_menu");
        assertNotNull(ordersMenuKey);
        assertTrue(DialogActions.isOurs(ordersMenuKey));
        assertEquals(DialogActions.ORDERS_MENU, ordersMenuKey.value());
    }

    @Test
    void statsViewActionBuildsOurNamespacedKey() {
        UUID uuid = UUID.randomUUID();
        Key key = DialogActions.key(DialogActions.STATS_VIEW + uuid);
        assertNotNull(key);
        assertTrue(DialogActions.isOurs(key));
        assertEquals(DialogActions.STATS_VIEW + uuid, key.value());
        assertEquals(uuid.toString(), DialogActions.argument(key.value(), DialogActions.STATS_VIEW));
    }

    @Test
    void statsViewFullActionKeepsThePlayerUuid() {
        UUID uuid = UUID.randomUUID();
        Key key = DialogActions.key(DialogActions.STATS_VIEW_FULL + uuid);
        assertNotNull(key);
        assertTrue(DialogActions.isOurs(key));
        assertEquals(uuid.toString(), DialogActions.argument(key.value(), DialogActions.STATS_VIEW_FULL));
    }
}
