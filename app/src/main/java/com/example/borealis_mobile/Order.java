package com.example.borealis_mobile;

import java.util.Map;

public class Order {
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private String productImageURL;
    private double totalAmount;
    private Map<String, OrderItem> items;
    private long timestamp;
    public Order() {}

    // Getters y setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImageURL() { return productImageURL; }
    public void setProductImageURL(String productImageURL) { this.productImageURL = productImageURL; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Map<String, OrderItem> getItems() { return items; }
    public void setItems(Map<String, OrderItem> items) { this.items = items; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public static class OrderItem {

        private String productId;
        private String name;
        private double price;
        private int quantity;
        private String imageURL;

        public OrderItem() {}

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getImageURL() { return imageURL; }
        public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    }
}


