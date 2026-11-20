package inventory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InventoryTests {
    private static final int SINGLE_ITEM = 1;
    private static final String POTION_CODE = "healing";
    private static final String POTION_NAME = "Potion";

    private InventoryTests() {
    }

    public static void main(String[] arguments) {
        int failures = 0;
        failures += runTest("empty inventory", InventoryTests::testEmptyInventory);
        failures += runTest("duplicate code", InventoryTests::testDuplicateCode);
        failures += runTest("search by text", InventoryTests::testSearchByText);
        failures += runTest("missing code", InventoryTests::testMissingCode);
        failures += runTest("use order and potion quantity", InventoryTests::testUseOrderAndPotionQuantity);

        if (failures > 0) {
            throw new AssertionError("Failed tests: " + failures);
        }
    }

    private static void testEmptyInventory() {
        Inventory inventory = new Inventory();

        expectEqual(0, inventory.getSize(), "Initial size");
        expectEqual(List.of(), inventory.listCodes(), "Initial codes");
        expectEqual(List.of(), inventory.useAll(), "Messages for an empty inventory");
    }

    private static void testDuplicateCode() {
        Inventory inventory = new Inventory();
        Item original = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        Item duplicate = new Item(new String(POTION_CODE), "Other item", SINGLE_ITEM);

        expectEqual(true, inventory.add(original), "First insertion");
        expectEqual(false, inventory.add(duplicate), "Duplicate code");
        expectEqual(SINGLE_ITEM, inventory.getSize(), "Size after duplicate");
        expectEqual(Optional.of(original), inventory.findByCode(POTION_CODE), "Original item is kept");
    }

    private static void testSearchByText() {
        Inventory inventory = new Inventory();
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        inventory.add(potion);

        expectEqual(Optional.of(potion), inventory.findByCode(new String(POTION_CODE)), "Search by equal text");
    }

    private static void testMissingCode() {
        Inventory inventory = new Inventory();

        expectEqual(Optional.empty(), inventory.findByCode("missing"), "Search for an absent code");
        expectEqual(Optional.empty(), inventory.findByCode(null), "Search using null");
    }

    private static void testUseOrderAndPotionQuantity() {
        Inventory inventory = new Inventory();
        Item weapon = new Weapon("sword", "Sword", SINGLE_ITEM);
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        inventory.add(weapon);
        inventory.add(potion);

        expectEqual(List.of("Attack: Sword", "Drink: Potion"), inventory.useAll(), "First use");
        expectEqual(List.of("Attack: Sword", "Empty: Potion"), inventory.useAll(), "Second use");
        expectEqual(0, potion.getQuantity(), "Potion quantity after use");
        expectEqual(SINGLE_ITEM, weapon.getQuantity(), "Weapon quantity after use");
    }

    private static int runTest(String name, Runnable test) {
        try {
            test.run();
            System.out.println("PASS " + name);
            return 0;
        } catch (AssertionError | RuntimeException failure) {
            System.err.println("FAIL " + name + ": " + failure);
            return 1;
        }
    }

    private static void expectEqual(Object expected, Object actual, String context) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(context + ": expected " + expected + ", got " + actual);
        }
    }
}
