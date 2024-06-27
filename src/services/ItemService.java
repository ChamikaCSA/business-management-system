package services;

import entities.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemService {
    private Map<String, Item> itemRegistry = new HashMap<>();

    public void registerItem(Item item) {
        itemRegistry.put(item.getId(), item);
    }

    public Item getItemById(String id) {
        return itemRegistry.get(id);
    }

    public Map<String, Item> getItemRegistry() {
        return itemRegistry;
    }

    public void setItemRegistry(Map<String, Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }
}
