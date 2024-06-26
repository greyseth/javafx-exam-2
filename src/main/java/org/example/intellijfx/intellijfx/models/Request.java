package org.example.intellijfx.intellijfx.models;

public class Request {
    int requestId;
    int productId;
    String reason;
    String requestDate;
    String status;
    String requester;

    public Request(int requestId, int productId, String reason, String requestDate, String status, String requester) {
        this.requestId = requestId;
        this.productId = productId;
        this.reason = reason;
        this.requestDate = requestDate;
        this.status = status;
        this.requester = requester;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }
}
