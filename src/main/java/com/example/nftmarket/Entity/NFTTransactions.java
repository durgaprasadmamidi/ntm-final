package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class NFTTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int nftTransactionId;

    private String cryptoType;
    private float listedPrice;
    private float currentMaxBid;
    private String transactionStatus;
    private Timestamp currentMaxOfferExpTime;

    @OneToMany(targetEntity = NFTCryptoTransactions.class, cascade=CascadeType.ALL)
    private List<NFTCryptoTransactions> nftCryptoTransactions;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nftTransactions", fetch = FetchType.LAZY)
    private PricedSaleHistory pricedSaleHistory;

    @ManyToOne
    private NFT nft;

    @OneToMany(targetEntity = AuctionHistory.class, cascade=CascadeType.ALL)
    private List<AuctionHistory> auctionHistories;

    public NFTTransactions(String cryptoType, float listedPrice, float currentMaxBid, String transactionStatus, Timestamp currentMaxOfferExpTime) {
        this.cryptoType = cryptoType;
        this.listedPrice = listedPrice;
        this.currentMaxBid = currentMaxBid;
        this.transactionStatus = transactionStatus;
        this.currentMaxOfferExpTime = currentMaxOfferExpTime;
    }

    public NFTTransactions() {
    }

    public int getNftTransactionId() {
        return nftTransactionId;
    }

    public void setNftTransactionId(int nftTransactionId) {
        this.nftTransactionId = nftTransactionId;
    }

    public List<NFTCryptoTransactions> getNftCryptoTransactions() {
        return nftCryptoTransactions;
    }

    public void setNftCryptoTransactions(List<NFTCryptoTransactions> nftCryptoTransactions) {
        this.nftCryptoTransactions = nftCryptoTransactions;
    }

    public NFT getNft() {
        return nft;
    }

    public void setNft(NFT nft) {
        this.nft = nft;
    }

    public List<AuctionHistory> getAuctionHistories() {
        return auctionHistories;
    }

    public void setAuctionHistories(List<AuctionHistory> auctionHistories) {
        this.auctionHistories = auctionHistories;
    }

    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    public float getListedPrice() {
        return listedPrice;
    }

    public void setListedPrice(float listedPrice) {
        this.listedPrice = listedPrice;
    }

    public float getCurrentMaxBid() {
        return currentMaxBid;
    }

    public void setCurrentMaxBid(float currentMaxBid) {
        this.currentMaxBid = currentMaxBid;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Timestamp getCurrentMaxOfferExpTime() {
        return currentMaxOfferExpTime;
    }

    public void setCurrentMaxOfferExpTime(Timestamp expirationTime) {
        this.currentMaxOfferExpTime = expirationTime;
    }

    public PricedSaleHistory getPricedSaleHistory() {
        return pricedSaleHistory;
    }

    public void setPricedSaleHistory(PricedSaleHistory pricedSaleHistory) {
        this.pricedSaleHistory = pricedSaleHistory;
    }
}
