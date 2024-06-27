package services;

import entities.Item;

public class StockService {
    private ItemService itemService;

    public StockService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void updateStock(String itemId, int quantity) {
        Item item = itemService.getItemById(itemId);
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public ItemService getItemService() {
        return itemService;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }
}
