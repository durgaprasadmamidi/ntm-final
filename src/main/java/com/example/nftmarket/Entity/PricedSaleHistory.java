package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class PricedSaleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pricedTransactionId;

    private Timestamp soldTime;
    private int sellerWalletId;
    private int buyerWalletId;
    private float buyPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nftTransactionId", referencedColumnName = "id")
    private NFTTransactions nftTransactions;

    public PricedSaleHistory(Timestamp soldTime, int sellerWalletId, int buyerWalletId, float buyPrice) {
        this.soldTime = soldTime;
        this.sellerWalletId = sellerWalletId;
        this.buyerWalletId = buyerWalletId;
        this.buyPrice = buyPrice;
    }

    public PricedSaleHistory() {
    }

    public int getPricedTransactionId() {
        return pricedTransactionId;
    }

    public void setPricedTransactionId(int pricedTransactionId) {
        this.pricedTransactionId = pricedTransactionId;
    }

    public Timestamp getSoldTime() {
        return soldTime;
    }

    public void setSoldTime(Timestamp soldTime) {
        this.soldTime = soldTime;
    }

    public int getSellerWalletId() {
        return sellerWalletId;
    }

    public void setSellerWalletId(int sellerWalletId) {
        this.sellerWalletId = sellerWalletId;
    }

    public int getBuyerWalletId() {
        return buyerWalletId;
    }

    public void setBuyerWalletId(int buyerWalletId) {
        this.buyerWalletId = buyerWalletId;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public NFTTransactions getNftTransactions() {
        return nftTransactions;
    }

    public void setNftTransactions(NFTTransactions nftTransactions) {
        this.nftTransactions = nftTransactions;
    }
}
