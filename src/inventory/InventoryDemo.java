package inventory;

public final class InventoryDemo {
    private InventoryDemo() {
    }

    public static void main(String[] arguments) {
        String weaponCode = "sword";
        String weaponName = "Sword";
        String potionCode = "healing";
        String potionName = "Potion";
        int initialQuantity = 1;
        Item weapon = new Weapon(weaponCode, weaponName, initialQuantity);
        Item potion = new Potion(potionCode, potionName, initialQuantity);
        Inventory inventory = new Inventory();

        inventory.add(weapon);
        inventory.add(potion);

        System.out.println(inventory.listCodes());
        System.out.println(inventory.useAll());
        System.out.println(inventory.useAll());
    }
}
