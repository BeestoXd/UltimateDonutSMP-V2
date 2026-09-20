package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Deque;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuNavigationTrackerTest {

    private MenuNavigationTracker tracker;
    private Player player;
    private UUID uuid;

    private static class DummyMenuA extends BaseMenu {
        DummyMenuA() {
            super(null, "Menu A", 9);
        }
        @Override
        public void build(Player player) {}
    }

    private static class DummyMenuB extends BaseMenu {
        DummyMenuB() {
            super(null, "Menu B", 9);
        }
        @Override
        public void build(Player player) {}
    }

    private static class DummyMenuC extends BaseMenu {
        DummyMenuC() {
            super(null, "Menu C", 9);
        }
        @Override
        public void build(Player player) {}
    }

    private static class DummyPaginatedMenu extends BaseMenu {
        final int page;
        DummyPaginatedMenu(int page) {
            super(null, "Page " + page, 9);
            this.page = page;
        }
        @Override
        public void build(Player player) {}
    }

    private static class DummyNonTargetMenu extends BaseMenu {
        final int id;
        DummyNonTargetMenu(int id) {
            super(null, "Confirm " + id, 9);
            this.id = id;
        }
        @Override
        public void build(Player player) {}
        @Override
        public boolean isEscBackTarget() {
            return false;
        }
        @Override
        public boolean isSameScreen(BaseMenu other) {
            return other instanceof DummyNonTargetMenu that && that.id == id;
        }
    }

    @BeforeEach
    void setUp() {
        tracker = new MenuNavigationTracker(null);
        uuid = UUID.randomUUID();
        player = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getUniqueId" -> uuid;
                    case "getName" -> "TestPlayer";
                    case "isOnline" -> true;
                    default -> null;
                }
        );
    }

    @Test
    void recordOpen_onEmptyStack_addsMenuAsRoot() {
        BaseMenu menuA = new DummyMenuA();
        tracker.recordOpen(player, menuA);

        Deque<BaseMenu> stack = tracker.getStack(uuid);
        assertNotNull(stack);
        assertEquals(1, stack.size());
        assertSame(menuA, stack.peekLast());
    }

    @Test
    void recordOpen_onDifferentMenu_pushesChildMenu() {
        BaseMenu menuA = new DummyMenuA();
        BaseMenu menuB = new DummyMenuB();

        tracker.recordOpen(player, menuA);
        tracker.recordOpen(player, menuB);

        Deque<BaseMenu> stack = tracker.getStack(uuid);
        assertNotNull(stack);
        assertEquals(2, stack.size());
        assertSame(menuB, stack.peekLast());
    }

    @Test
    void popPrevious_returnsParentAndKeepsParentOnTop() {
        BaseMenu menuA = new DummyMenuA();
        BaseMenu menuB = new DummyMenuB();

        tracker.recordOpen(player, menuA);
        tracker.recordOpen(player, menuB);

        BaseMenu previous = tracker.popPrevious(player);
        assertSame(menuA, previous);

        Deque<BaseMenu> stack = tracker.getStack(uuid);
        assertNotNull(stack);
        assertEquals(1, stack.size());
        assertSame(menuA, stack.peekLast());
    }

    @Test
    void popPrevious_onRootMenu_returnsNullAndClearsStack() {
        BaseMenu menuA = new DummyMenuA();
        tracker.recordOpen(player, menuA);

        BaseMenu previous = tracker.popPrevious(player);
        assertNull(previous, "Root menu should have no previous menu to navigate back to");
        assertNull(tracker.getStack(uuid), "Stack should be removed after popping the root menu");
    }

    @Test
    void multiLevelNavigation_unwindsStepByStep() {
        BaseMenu menuA = new DummyMenuA();
        BaseMenu menuB = new DummyMenuB();
        BaseMenu menuC = new DummyMenuC();

        tracker.recordOpen(player, menuA);
        tracker.recordOpen(player, menuB);
        tracker.recordOpen(player, menuC);

        assertEquals(3, tracker.getStack(uuid).size());

        // ESC on Menu C -> returns Menu B
        BaseMenu step1 = tracker.popPrevious(player);
        assertSame(menuB, step1);
        assertEquals(2, tracker.getStack(uuid).size());

        // ESC on Menu B -> returns Menu A
        BaseMenu step2 = tracker.popPrevious(player);
        assertSame(menuA, step2);
        assertEquals(1, tracker.getStack(uuid).size());

        // ESC on Menu A -> closes (returns null)
        BaseMenu step3 = tracker.popPrevious(player);
        assertNull(step3);
        assertNull(tracker.getStack(uuid));
    }

    @Test
    void recordOpen_sameScreen_replacesTopInsteadOfPushing() {
        DummyPaginatedMenu page1 = new DummyPaginatedMenu(1);
        DummyPaginatedMenu page2 = new DummyPaginatedMenu(2);
        DummyPaginatedMenu page3 = new DummyPaginatedMenu(3);

        tracker.recordOpen(player, page1);
        assertEquals(1, tracker.getStack(uuid).size());
        assertSame(page1, tracker.getStack(uuid).peekLast());

        // Changing page replaces the current screen in history
        tracker.recordOpen(player, page2);
        assertEquals(1, tracker.getStack(uuid).size());
        assertSame(page2, tracker.getStack(uuid).peekLast());

        tracker.recordOpen(player, page3);
        assertEquals(1, tracker.getStack(uuid).size());
        assertSame(page3, tracker.getStack(uuid).peekLast());

        // ESC on page 3 closes since it was the root
        assertNull(tracker.popPrevious(player));
    }

    @Test
    void recordOpen_existingMenuInStack_unwindsStack() {
        BaseMenu menuA = new DummyMenuA();
        BaseMenu menuB = new DummyMenuB();

        tracker.recordOpen(player, menuA);
        tracker.recordOpen(player, menuB);
        assertEquals(2, tracker.getStack(uuid).size());

        // Player clicks in-menu back button to open a new DummyMenuA
        BaseMenu newMenuA = new DummyMenuA();
        tracker.recordOpen(player, newMenuA);

        // Stack should unwind back to Menu A rather than growing to [A, B, A]
        assertEquals(1, tracker.getStack(uuid).size());
        assertSame(newMenuA, tracker.getStack(uuid).peekLast());
    }

    @Test
    void clear_removesPlayerHistoryAndFlags() {
        BaseMenu menuA = new DummyMenuA();
        tracker.recordOpen(player, menuA);
        tracker.setTransitioning(uuid, true);
        tracker.setHandlingClick(uuid, true);
        tracker.setExplicitClose(uuid, true);

        tracker.clear(uuid);

        assertNull(tracker.getStack(uuid));
        assertFalse(tracker.isTransitioning(uuid));
        assertFalse(tracker.isHandlingClick(uuid));
        assertFalse(tracker.isExplicitClose(uuid));
    }

    @Test
    void isPlayerInitiatedClose_respectsTransitioningAndHandlingClickFlags() {
        assertFalse(tracker.isTransitioning(uuid));
        assertFalse(tracker.isHandlingClick(uuid));
        assertFalse(tracker.isExplicitClose(uuid));
        assertTrue(tracker.isPlayerInitiatedClose(null, uuid));

        tracker.setTransitioning(uuid, true);
        assertFalse(tracker.isPlayerInitiatedClose(null, uuid));
        tracker.setTransitioning(uuid, false);

        tracker.setHandlingClick(uuid, true);
        assertFalse(tracker.isPlayerInitiatedClose(null, uuid));
        tracker.setHandlingClick(uuid, false);

        tracker.setExplicitClose(uuid, true);
        assertFalse(tracker.isPlayerInitiatedClose(null, uuid));
        tracker.setExplicitClose(uuid, false);

        assertTrue(tracker.isPlayerInitiatedClose(null, uuid));
    }

    @Test
    void popPrevious_skipsMenusThatAreNotEscBackTargets() {
        BaseMenu browse = new DummyMenuA();
        BaseMenu confirm = new DummyNonTargetMenu(1);
        BaseMenu yourItems = new DummyMenuB();

        tracker.recordOpen(player, browse);
        tracker.recordOpen(player, confirm);
        tracker.recordOpen(player, yourItems);

        BaseMenu previous = tracker.popPrevious(player);
        assertSame(browse, previous, "ESC after listing must skip the consumed confirm screen");
        assertEquals(1, tracker.getStack(uuid).size());
        assertSame(browse, tracker.getStack(uuid).peekLast());
    }

    @Test
    void popPrevious_whenConfirmWasRoot_closesInsteadOfReopeningIt() {
        BaseMenu confirm = new DummyNonTargetMenu(1);
        BaseMenu yourItems = new DummyMenuA();

        tracker.recordOpen(player, confirm);
        tracker.recordOpen(player, yourItems);

        assertNull(tracker.popPrevious(player), "ESC after /ah sell must not reopen confirm listing");
        assertNull(tracker.getStack(uuid));
    }

    @Test
    void popPrevious_skipsAChainOfNonTargets() {
        BaseMenu browse = new DummyMenuA();
        tracker.recordOpen(player, browse);
        tracker.recordOpen(player, new DummyNonTargetMenu(1));
        tracker.recordOpen(player, new DummyNonTargetMenu(2));
        tracker.recordOpen(player, new DummyMenuB());

        assertSame(browse, tracker.popPrevious(player));
        assertEquals(1, tracker.getStack(uuid).size());
    }

    @Test
    void shopMenu_isSameScreenLogic() {
        // Main shop menu vs category menu
        ShopMenu mainShop = new ShopMenu(null);
        ShopMenu blocksCategoryPage0 = new ShopMenu(null, "BLOCKS", 0);
        ShopMenu blocksCategoryPage1 = new ShopMenu(null, "BLOCKS", 1);
        ShopMenu foodCategory = new ShopMenu(null, "FOOD", 0);
        ShopMenu favorites = new ShopMenu(null, true, 0);

        // Main menu and category are different screens
        assertFalse(mainShop.isSameScreen(blocksCategoryPage0));
        assertFalse(blocksCategoryPage0.isSameScreen(mainShop));

        // Two pages of the same category are the same screen (pagination)
        assertTrue(blocksCategoryPage0.isSameScreen(blocksCategoryPage1));

        // Two different categories are different screens
        assertFalse(blocksCategoryPage0.isSameScreen(foodCategory));

        // Favorites is different from main shop and categories
        assertFalse(favorites.isSameScreen(mainShop));
        assertFalse(favorites.isSameScreen(blocksCategoryPage0));
    }
}
