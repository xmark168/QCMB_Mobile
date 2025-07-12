package vn.fpt.qcmb_mobile.data.model;

public class TempInventoryItem {
    private String name;
    private String iconEmoji;
    private String description;
    private int totalQuantity;
    private int selectedQuantity;
    private String itemType;

    public TempInventoryItem(String name, String iconEmoji, String description, int totalQuantity, String itemType) {
        this.name = name;
        this.iconEmoji = iconEmoji;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.selectedQuantity = 0;
        this.itemType = itemType;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public int getSelectedQuantity() { return selectedQuantity; }
    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = Math.max(0, Math.min(selectedQuantity, totalQuantity));
    }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public boolean hasItem() {
        return totalQuantity > 0;
    }
    public boolean isSelected() {
        return selectedQuantity > 0;
    }
    public int getAvailableQuantity() {
        return totalQuantity - selectedQuantity;
    }

    public boolean canSelectMore() {
        return selectedQuantity < totalQuantity;
    }

}
