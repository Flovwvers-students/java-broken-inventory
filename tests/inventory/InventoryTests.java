package inventory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InventoryTests {
    private static final int EMPTY_COUNT = 0;
    private static final int SINGLE_ITEM = 1;
    private static final int TWO_ITEMS = 2;
    private static final int NEGATIVE_QUANTITY = -5;
    private static final String POTION_CODE = "healing";
    private static final String POTION_NAME = "Potion";
    private static final String WEAPON_CODE = "sword";
    private static final String WEAPON_NAME = "Sword";

    private InventoryTests() {
    }

    public static void main(String[] arguments) {
        int failures = 0;
        failures += runTest("empty inventory", InventoryTests::testEmptyInventory);
        failures += runTest("duplicate code", InventoryTests::testDuplicateCode);
        failures += runTest("search by text", InventoryTests::testSearchByText);
        failures += runTest("missing code", InventoryTests::testMissingCode);
        failures += runTest("use order and potion quantity", InventoryTests::testUseOrderAndPotionQuantity);
        failures += runTest("null add and remove", InventoryTests::testNullAddAndRemove);
        failures += runTest("same name different codes", InventoryTests::testSameNameDifferentCodes);
        failures += runTest("case sensitive codes", InventoryTests::testCaseSensitiveCodes);
        failures += runTest("clear inventory", InventoryTests::testClearInventory);
        failures += runTest("remove and re-add order", InventoryTests::testRemoveAndReAddOrder);
        failures += runTest("negative quantity", InventoryTests::testNegativeQuantity);
        failures += runTest("codes snapshot", InventoryTests::testCodesSnapshot);
        failures += runTest("item validation and base use", InventoryTests::testItemValidationAndBaseUse);

        if (failures > 0) {
            throw new AssertionError("Failed tests: " + failures);
        }
    }

    private static void testEmptyInventory() {
        Inventory inventory = new Inventory();
        List<String> expectedEmpty = List.of();

        expectEqual(EMPTY_COUNT, inventory.getSize(), "Initial size");
        expectEqual(expectedEmpty, inventory.listCodes(), "Initial codes");
        expectEqual(expectedEmpty, inventory.useAll(), "Messages for an empty inventory");
    }

    private static void testDuplicateCode() {
        Inventory inventory = new Inventory();
        String duplicateName = "Other item";
        Item original = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        Item duplicate = new Item(new String(POTION_CODE), duplicateName, SINGLE_ITEM);

        expectEqual(true, inventory.add(original), "First insertion");
        expectEqual(false, inventory.add(duplicate), "Duplicate code");
        expectEqual(SINGLE_ITEM, inventory.getSize(), "Size after duplicate");
        expectEqual(Optional.of(original), inventory.findByCode(POTION_CODE), "Original item is kept");
    }

    private static void testSearchByText() {
        Inventory inventory = new Inventory();
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        inventory.add(potion);

        expectEqual(
                Optional.of(potion),
                inventory.findByCode(new String(POTION_CODE)),
                "Search by equal text");
    }

    private static void testMissingCode() {
        Inventory inventory = new Inventory();
        String missingCode = "missing";

        expectEqual(Optional.empty(), inventory.findByCode(missingCode), "Search for an absent code");
        expectEqual(Optional.empty(), inventory.findByCode(null), "Search using null");
    }

    private static void testUseOrderAndPotionQuantity() {
        Inventory inventory = new Inventory();
        Item weapon = new Weapon(WEAPON_CODE, WEAPON_NAME, SINGLE_ITEM);
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        List<String> expectedFirstUse = List.of("Attack: Sword", "Drink: Potion");
        List<String> expectedSecondUse = List.of("Attack: Sword", "Empty: Potion");

        inventory.add(weapon);
        inventory.add(potion);

        expectEqual(expectedFirstUse, inventory.useAll(), "First use");
        expectEqual(expectedSecondUse, inventory.useAll(), "Second use");
        expectEqual(EMPTY_COUNT, potion.getQuantity(), "Potion quantity after use");
        expectEqual(SINGLE_ITEM, weapon.getQuantity(), "Weapon quantity after use");
    }

    private static void testNullAddAndRemove() {
        Inventory inventory = new Inventory();
        Item weapon = new Weapon(WEAPON_CODE, WEAPON_NAME, SINGLE_ITEM);

        expectEqual(false, inventory.add(null), "Add null item");
        expectEqual(EMPTY_COUNT, inventory.getSize(), "Size after null add");

        inventory.add(weapon);
        expectEqual(false, inventory.removeByCode(null), "Remove null code");
        expectEqual(SINGLE_ITEM, inventory.getSize(), "Size after null remove");
    }

    private static void testSameNameDifferentCodes() {
        Inventory inventory = new Inventory();
        String firstCode = "potion-small";
        String secondCode = "potion-large";
        Item firstPotion = new Potion(firstCode, POTION_NAME, SINGLE_ITEM);
        Item secondPotion = new Potion(secondCode, POTION_NAME, SINGLE_ITEM);
        List<String> expectedCodes = List.of(firstCode, secondCode);

        expectEqual(true, inventory.add(firstPotion), "Add first item with shared name");
        expectEqual(true, inventory.add(secondPotion), "Add second item with shared name");
        expectEqual(TWO_ITEMS, inventory.getSize(), "Size with same names");
        expectEqual(Optional.of(firstPotion), inventory.findByCode(firstCode), "Find first by code");
        expectEqual(Optional.of(secondPotion), inventory.findByCode(secondCode), "Find second by code");
        expectEqual(expectedCodes, inventory.listCodes(), "Order of items with same name");
    }

    private static void testCaseSensitiveCodes() {
        Inventory inventory = new Inventory();
        String upperCode = "HEALING";
        String upperName = "Big Potion";
        Item lowerItem = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        Item upperItem = new Potion(upperCode, upperName, SINGLE_ITEM);

        expectEqual(true, inventory.add(lowerItem), "Add lowercase code");
        expectEqual(true, inventory.add(upperItem), "Add uppercase code");
        expectEqual(TWO_ITEMS, inventory.getSize(), "Size with different cases");
        expectEqual(Optional.of(lowerItem), inventory.findByCode(POTION_CODE), "Find lowercase code");
        expectEqual(Optional.of(upperItem), inventory.findByCode(upperCode), "Find uppercase code");
    }

    private static void testClearInventory() {
        Inventory inventory = new Inventory();
        Item weapon = new Weapon(WEAPON_CODE, WEAPON_NAME, SINGLE_ITEM);
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        List<String> expectedEmpty = List.of();

        inventory.add(weapon);
        inventory.add(potion);
        inventory.clear();
        inventory.clear();

        expectEqual(EMPTY_COUNT, inventory.getSize(), "Size after clear");
        expectEqual(expectedEmpty, inventory.listCodes(), "Codes after clear");
        expectEqual(expectedEmpty, inventory.useAll(), "Messages after clear");
    }

    private static void testRemoveAndReAddOrder() {
        Inventory inventory = new Inventory();
        String thirdCode = "key";
        String thirdName = "Key";
        String missingCode = "absent";
        Item weapon = new Weapon(WEAPON_CODE, WEAPON_NAME, SINGLE_ITEM);
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        Item key = new Item(thirdCode, thirdName, SINGLE_ITEM);
        List<String> expectedAfterRemove = List.of(WEAPON_CODE, thirdCode);
        List<String> expectedAfterReAdd = List.of(WEAPON_CODE, thirdCode, POTION_CODE);

        inventory.add(weapon);
        inventory.add(potion);
        inventory.add(key);

        expectEqual(false, inventory.removeByCode(missingCode), "Remove absent code");
        expectEqual(true, inventory.removeByCode(POTION_CODE), "Remove existing code");
        expectEqual(false, inventory.removeByCode(POTION_CODE), "Repeat remove");
        expectEqual(expectedAfterRemove, inventory.listCodes(), "Order after remove");

        expectEqual(true, inventory.add(potion), "Re-add removed code");
        expectEqual(expectedAfterReAdd, inventory.listCodes(), "Re-added code moves to end");
    }

    private static void testNegativeQuantity() {
        Item item = new Item(WEAPON_CODE, WEAPON_NAME, NEGATIVE_QUANTITY);
        Potion potion = new Potion(POTION_CODE, POTION_NAME, NEGATIVE_QUANTITY);
        int positiveQuantity = 2;

        expectEqual(EMPTY_COUNT, item.getQuantity(), "Negative constructor quantity");
        expectEqual(EMPTY_COUNT, potion.getQuantity(), "Negative potion constructor quantity");

        item.setQuantity(positiveQuantity);
        item.setQuantity(NEGATIVE_QUANTITY);
        expectEqual(EMPTY_COUNT, item.getQuantity(), "Negative setQuantity");

        expectEqual("Empty: Potion", potion.use(), "Use potion with zeroed quantity");
        expectEqual(EMPTY_COUNT, potion.getQuantity(), "Potion quantity remains zero");
    }

    private static void testCodesSnapshot() {
        Inventory inventory = new Inventory();
        String extraCode = "shield";
        String extraName = "Shield";
        Item weapon = new Weapon(WEAPON_CODE, WEAPON_NAME, SINGLE_ITEM);
        Item potion = new Potion(POTION_CODE, POTION_NAME, SINGLE_ITEM);
        Item shield = new Item(extraCode, extraName, SINGLE_ITEM);
        List<String> expectedSnapshot = List.of(WEAPON_CODE, POTION_CODE);

        inventory.add(weapon);
        inventory.add(potion);

        List<String> snapshot = inventory.listCodes();
        expectThrows(
                UnsupportedOperationException.class,
                () -> snapshot.add(extraCode),
                "Snapshot must reject modifications");

        inventory.add(shield);
        expectEqual(expectedSnapshot, snapshot, "Snapshot unchanged after add");

        inventory.removeByCode(WEAPON_CODE);
        expectEqual(expectedSnapshot, snapshot, "Snapshot unchanged after remove");

        inventory.clear();
        expectEqual(expectedSnapshot, snapshot, "Snapshot unchanged after clear");
    }

    private static void testItemValidationAndBaseUse() {
        String blankCode = "   ";
        String emptyCode = "";
        String emptyName = "";
        String torchCode = "torch";
        String torchName = "Torch";

        expectThrows(
                IllegalArgumentException.class,
                () -> new Item(null, torchName, SINGLE_ITEM),
                "Null code rejected");
        expectThrows(
                IllegalArgumentException.class,
                () -> new Item(emptyCode, torchName, SINGLE_ITEM),
                "Empty code rejected");
        expectThrows(
                IllegalArgumentException.class,
                () -> new Item(blankCode, torchName, SINGLE_ITEM),
                "Blank code rejected");
        expectThrows(
                NullPointerException.class,
                () -> new Item(torchCode, null, SINGLE_ITEM),
                "Null name rejected");

        Item unnamed = new Item(torchCode, emptyName, SINGLE_ITEM);
        Item torch = new Item(torchCode, torchName, SINGLE_ITEM);

        expectEqual("Item: ", unnamed.use(), "Empty name allowed");
        expectEqual("Item: Torch", torch.use(), "Base item use");
        expectEqual(SINGLE_ITEM, torch.getQuantity(), "Base item quantity unchanged");
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

    private static void expectThrows(
            Class<? extends RuntimeException> expectedType,
            Runnable action,
            String context) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(
                    context + ": expected " + expectedType.getSimpleName() + ", got " + exception);
        }

        throw new AssertionError(context + ": expected " + expectedType.getSimpleName() + ", got no exception");
    }
}
