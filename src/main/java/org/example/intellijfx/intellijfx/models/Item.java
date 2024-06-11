package org.example.intellijfx.intellijfx.models;

public class Item {
    int idBarang;
    String itemName;
    String category;
    String description;
    int price;
    int stock;

    public Item(int idBarang, String itemName, String category, String description, int price, int stock) {
        this.idBarang = idBarang;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
