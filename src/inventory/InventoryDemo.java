package inventory;

public final class InventoryDemo {
    private InventoryDemo() {
    }

    public static void main(String[] arguments) {
        int initialQuantity = 1;
        Item weapon = new Weapon("sword", "Sword", initialQuantity);
        Item potion = new Potion("healing", "Potion", initialQuantity);
        Inventory inventory = new Inventory();

        inventory.add(weapon);
        inventory.add(potion);

        System.out.println(inventory.listCodes());
        System.out.println(inventory.useAll());
        System.out.println(inventory.useAll());
    }
}
