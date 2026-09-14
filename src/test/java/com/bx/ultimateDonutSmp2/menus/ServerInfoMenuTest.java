package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerInfoMenuTest {

    @Test
    void rtpCommandSelectorAcceptsTheBundledHelpButton() {
        assertEquals("", ServerInfoMenu.rtpCommandSelector("rtp"));
        assertEquals("", ServerInfoMenu.rtpCommandSelector("/rtp"));
        assertEquals("world", ServerInfoMenu.rtpCommandSelector("rtp world"));
        assertEquals("world_nether", ServerInfoMenu.rtpCommandSelector("/rtp world_nether"));
        assertNull(ServerInfoMenu.rtpCommandSelector("spawn"));
        assertNull(ServerInfoMenu.rtpCommandSelector(""));
        assertNull(ServerInfoMenu.rtpCommandSelector(null));
    }
}
