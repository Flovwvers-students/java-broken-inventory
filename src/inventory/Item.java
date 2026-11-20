package inventory;

import java.util.Objects;

public class Item {
    private static final int MINIMUM_QUANTITY = 0;

    private final String code;
    private final String name;
    private int quantity;

    public Item(String code, String name, int quantity) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Item code must not be blank");
        }

        this.code = code;
        this.name = Objects.requireNonNull(name, "name");
        setQuantity(quantity);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public final void setQuantity(int quantity) {
        this.quantity = Math.max(MINIMUM_QUANTITY, quantity);
    }

    public String use() {
        return "Item: " + name;
    }
}
