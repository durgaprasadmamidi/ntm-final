package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class AuctionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auctionUpdateId;

    private float offerPrice;
    private Timestamp offerTime;
    private Timestamp expirationTime;
    private boolean isSold;
    private String auctionStatus;
    private int buyerWalletId;

    @ManyToOne
    private NFTTransactions nftTransactions;

    public AuctionHistory() {
    }

    public AuctionHistory(float offerPrice, Timestamp offerTime, Timestamp expirationTime, boolean isSold, String auctionStatus, int buyerWalletId) {
        this.offerPrice = offerPrice;
        this.offerTime = offerTime;
        this.expirationTime = expirationTime;
        this.isSold = isSold;
        this.auctionStatus = auctionStatus;
        this.buyerWalletId = buyerWalletId;
    }

    public int getAuctionUpdateId() {
        return auctionUpdateId;
    }

    public void setAuctionUpdateId(int auctionUpdateId) {
        this.auctionUpdateId = auctionUpdateId;
    }

    public float getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(float offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Timestamp getOfferTime() {
        return offerTime;
    }

    public void setOfferTime(Timestamp offerTime) {
        this.offerTime = offerTime;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public int getBuyerWalletId() {
        return buyerWalletId;
    }

    public void setBuyerWalletId(int buyerWalletId) {
        this.buyerWalletId = buyerWalletId;
    }

    public NFTTransactions getNftTransactions() {
        return nftTransactions;
    }

    public void setNftTransactions(NFTTransactions nftTransactions) {
        this.nftTransactions = nftTransactions;
    }
}
