package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuyBedrockPagesTest {

    @Test
    void pageSizeStaysInsideTheRangeBedrockCanDraw() {
        assertEquals(20, QuickBuyBedrockPages.pageSize(0));
        assertEquals(20, QuickBuyBedrockPages.pageSize(-4));
        assertEquals(20, QuickBuyBedrockPages.pageSize(20));
        assertEquals(40, QuickBuyBedrockPages.pageSize(2000));
    }

    @Test
    void sliceNeverReturnsTheWholeCatalogue() {
        List<Integer> items = IntStream.range(0, 45).boxed().toList();

        assertEquals(3, QuickBuyBedrockPages.pageCount(items.size(), 20));
        assertEquals(List.of(0, 1), QuickBuyBedrockPages.slice(items, -1, 2).stream().limit(2).toList());
        assertEquals(20, QuickBuyBedrockPages.slice(items, 0, 20).size());
        assertEquals(20, QuickBuyBedrockPages.slice(items, 1, 20).getFirst());
        assertEquals(5, QuickBuyBedrockPages.slice(items, 9, 20).size());
        assertTrue(QuickBuyBedrockPages.slice(List.of(), 0, 20).isEmpty());
    }
}
