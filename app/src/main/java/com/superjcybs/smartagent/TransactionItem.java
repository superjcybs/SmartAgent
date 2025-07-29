package com.superjcybs.smartagent;

public class TransactionItem {
    public String phone;
    public String network;
    public String dateTime;
    public String amount;
    public String status;
    public String tnxtype;

    public TransactionItem(String phone, String network, String dateTime, String amount, String status, String tnxtype) {
        this.phone = phone;
        this.network = network;
        this.dateTime = dateTime;
        this.amount = amount;
        this.status = status;
        this.tnxtype = tnxtype;
    }

    public String getPhone() { return phone; }
    public String getNetwork() { return network; }
    public String getDate() { return dateTime; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getType() { return tnxtype; }

    @Override
    public String toString() {
        return phone + " | " + network + " | " + dateTime + " | " + amount + " | " + status + " | " + tnxtype;
    }
}
