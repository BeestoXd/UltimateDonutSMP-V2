package com.bx.ultimateDonutSmp2.dialogs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A dialog command template is a macro, and the server rejects one that substitutes nothing with
 * "No variables in macro". Every plain {@code COMMAND: "auction"} in the config is exactly that
 * case, so the choice between a template and a plugin-dispatched command has to be right.
 */
class DialogCommandButtonTest {

    @Test
    void aPlainCommandIsNotAMacro() {
        assertFalse(DialogFactory.usesInputSubstitution("auctionhouse"));
        assertFalse(DialogFactory.usesInputSubstitution("tpa Notch"));
        assertFalse(DialogFactory.usesInputSubstitution(""));
        assertFalse(DialogFactory.usesInputSubstitution(null));
    }

    @Test
    void aCommandThatReadsAnInputIsAMacro() {
        assertTrue(DialogFactory.usesInputSubstitution("msg $(player_name) hello"));
        assertTrue(DialogFactory.usesInputSubstitution("pay $(target) $(amount)"));
    }

    @Test
    void anUnclosedOrEmptySubstitutionIsNotAMacro() {
        assertFalse(
                DialogFactory.usesInputSubstitution("say $(unclosed"),
                "the server would reject this, so it must not be handed over as a template"
        );
        assertFalse(DialogFactory.usesInputSubstitution("say $()"));
    }

    @Test
    void aCommandKeepsOneIdForTheWholeSession() {
        DialogSession session = new DialogSession();

        String first = session.registerCommand("auctionhouse");
        String second = session.registerCommand("auctionhouse");

        assertEquals(first, second, "rebuilding a screen must not grow the map on every open");
        assertEquals("auctionhouse", session.commandFor(first));
    }

    @Test
    void differentCommandsGetDifferentIds() {
        DialogSession session = new DialogSession();

        String auction = session.registerCommand("auctionhouse");
        String orders = session.registerCommand("orders");

        assertNotEquals(auction, orders);
        assertEquals("orders", session.commandFor(orders));
    }

    @Test
    void anIdSurvivesBeingPutInAnActionKey() {
        DialogSession session = new DialogSession();
        String id = session.registerCommand("tpa Notch");

        String action = DialogActions.RUN_COMMAND + id;
        assertEquals(action, DialogActions.sanitise(action));
        assertEquals(id, DialogActions.argument(action, DialogActions.RUN_COMMAND));
    }

    @Test
    void anUnknownIdResolvesToNothing() {
        DialogSession session = new DialogSession();

        assertNull(session.commandFor("nope"));
        assertNull(session.commandFor(null));
        assertNull(session.registerCommand("  "), "a blank command gets no button action at all");
    }
}
