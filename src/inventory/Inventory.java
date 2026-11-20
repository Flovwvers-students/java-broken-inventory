package inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Inventory {
    private final Map<String, Item> items = new LinkedHashMap<>();

    public int getSize() {
        return items.size();
    }

    public boolean add(Item item) {
        if (item == null || items.containsKey(item.getCode())) {
            return false;
        }

        items.put(item.getCode(), item);
        return true;
    }

    public Optional<Item> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(items.get(code));
    }

    public boolean removeByCode(String code) {
        if (code == null) {
            return false;
        }

        return items.remove(code) != null;
    }

    public List<String> listCodes() {
        return List.copyOf(items.keySet());
    }

    public List<String> useAll() {
        List<String> messages = new ArrayList<>();

        for (Item item : items.values()) {
            messages.add(item.use());
        }

        return messages;
    }

    public void clear() {
        items.clear();
    }
}
