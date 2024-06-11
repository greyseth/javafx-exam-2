package org.example.intellijfx.intellijfx.models;

public class SupplyOrder {
    int orderId;
    String orderDate;
    int supplierId;
    String supplierName;
    String status;
    int productId;
    int quantity;
    int price;

    public SupplyOrder(int orderId, String orderDate, int supplierId, String supplierName, String status, int productId, int quantity, int price) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = status;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
