package inventory;

public final class Potion extends Item {
    private static final int EMPTY_QUANTITY = 0;

    public Potion(String code, String name, int quantity) {
        super(code, name, quantity);
    }

    @Override
    public String use() {
        if (getQuantity() == EMPTY_QUANTITY) {
            return "Empty: " + getName();
        }

        return "Drink: " + getName();
    }
}
