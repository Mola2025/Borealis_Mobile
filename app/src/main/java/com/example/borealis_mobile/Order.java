package com.example.borealis_mobile;

public class Order {
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private String productImageURL;
    private double totalAmount;
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

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
