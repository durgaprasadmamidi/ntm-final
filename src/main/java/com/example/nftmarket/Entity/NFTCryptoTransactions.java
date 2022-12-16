package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class NFTCryptoTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int nftCryptoTransactionId;

    private float soldPrice;
    private int sellerId;
    private int buyerId;
    private Timestamp transactionTime;

    @ManyToOne
    private NFTTransactions nftTransactions;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cryptoTransactionId", referencedColumnName = "id")
    private CryptoTransactions cryptoTransactions;

    public NFTCryptoTransactions(float soldPrice, int sellerId, int buyerId, Timestamp transactionTime, NFTTransactions nftTransactions, CryptoTransactions cryptoTransactions) {
        this.soldPrice = soldPrice;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.transactionTime = transactionTime;
        this.nftTransactions = nftTransactions;
        this.cryptoTransactions = cryptoTransactions;
    }

    public NFTCryptoTransactions() {
    }

    public int getNftCryptoTransactionId() {
        return nftCryptoTransactionId;
    }

    public void setNftCryptoTransactionId(int nftCryptoTransactionId) {
        this.nftCryptoTransactionId = nftCryptoTransactionId;
    }

    public float getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(float soldPrice) {
        this.soldPrice = soldPrice;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Timestamp transactionTime) {
        this.transactionTime = transactionTime;
    }

    public NFTTransactions getNftTransactions() {
        return nftTransactions;
    }

    public void setNftTransactions(NFTTransactions nftTransactions) {
        this.nftTransactions = nftTransactions;
    }

    public CryptoTransactions getCryptoTransactions() {
        return cryptoTransactions;
    }

    public void setCryptoTransactions(CryptoTransactions cryptoTransactions) {
        this.cryptoTransactions = cryptoTransactions;
    }
}
