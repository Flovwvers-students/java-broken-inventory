package inventory;

public final class Weapon extends Item {
    public Weapon(String code, String name, int quantity) {
        super(code, name, quantity);
    }

    @Override
    public String use() {
        return "Attack: " + getName();
    }
}
