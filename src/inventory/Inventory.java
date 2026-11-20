package inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Inventory {
    private final List<Item> items = new ArrayList<>();

    public int getSize() {
        return items.size();
    }

    public boolean add(Item item) {
        if (item == null) {
            return false;
        }

        return items.add(item);
    }

    public Optional<Item> findByCode(String code) {
        for (Item item : items) {
            if (item.getCode() == code) {
                return Optional.of(item);
            }
        }

        return null;
    }

    public boolean removeByCode(String code) {
        return items.removeIf(item -> item.getCode().equals(code));
    }

    public List<String> listCodes() {
        List<String> codes = new ArrayList<>();

        for (Item item : items) {
            codes.add(item.getCode());
        }

        return codes;
    }

    public List<String> useAll() {
        List<String> messages = new ArrayList<>();

        for (Item item : items) {
            messages.add(item.use());
        }

        return messages;
    }

    public void clear() {
        items.clear();
    }
}
