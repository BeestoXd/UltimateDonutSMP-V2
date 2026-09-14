package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SellStatsResetRequiresAuthTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/SellStatsExporter.java");

    @Test
    void statsResetRefusesUnauthenticatedCallers() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("if (\"/stats/reset\"");
        int end = source.indexOf("String html = new SellStatsExporter");
        assertTrue(start >= 0 && end > start, "the /stats/reset branch should still sit above the dashboard handler");

        String reset = source.substring(start, end);
        assertTrue(
                reset.contains("Authorization")
                        || reset.contains("getRemoteAddress")
                        || reset.contains("localhost")
                        || reset.contains("127.0.0.1")
                        || reset.contains("token"),
                "createContext(\"/stats\") answers /stats/reset by calling clearShopAnalyticsData with "
                        + "no auth and no loopback check. HttpServer.create(new InetSocketAddress(port)) "
                        + "listens on every interface, so anyone who can reach the port can wipe shop analytics."
        );
    }
}
