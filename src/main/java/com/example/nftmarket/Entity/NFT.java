package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class NFT {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int nftId;

        private String tokenId;
        private String smartContactAddress;
        private String name;
        private String type;
        private String description;
        private String imageUrl;
        private String assetUrl;
        private Timestamp lastRecordTime;
        private boolean isListedForSale;
        private String listingType;

        private float listPrice;

        private String currencyType;

        @ManyToOne
        private Wallet wallet;

        @OneToMany(targetEntity = NFTTransactions.class, cascade=CascadeType.ALL)
        private List<NFTTransactions> nftTransactionsList;

        public NFT(String tokenId, String smartContactAddress, String name, String type, String description, String imageUrl, String assetUrl, Timestamp lastRecordTime, boolean isListedForSale, String listingType) {
                this.tokenId = tokenId;
                this.smartContactAddress = smartContactAddress;
                this.name = name;
                this.type = type;
                this.description = description;
                this.imageUrl = imageUrl;
                this.assetUrl = assetUrl;
                this.lastRecordTime = lastRecordTime;
                this.isListedForSale = isListedForSale;
                this.listingType = listingType;
        }

        public NFT() {
        }

        public int getNftId() {
                return nftId;
        }

        public void setNftId(int nftId) {
                this.nftId = nftId;
        }

        public String getTokenId() {
                return tokenId;
        }

        public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
        }

        public String getSmartContactAddress() {
                return smartContactAddress;
        }

        public void setSmartContactAddress(String smartContactAddress) {
                this.smartContactAddress = smartContactAddress;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }

        public String getAssetUrl() {
                return assetUrl;
        }

        public void setAssetUrl(String assetUrl) {
                this.assetUrl = assetUrl;
        }

        public Timestamp getLastRecordTime() {
                return lastRecordTime;
        }

        public void setLastRecordTime(Timestamp lastRecordTime) {
                this.lastRecordTime = lastRecordTime;
        }

        public boolean isListedForSale() {
                return isListedForSale;
        }

        public void setListedForSale(boolean listedForSale) {
                isListedForSale = listedForSale;
        }

        public String getListingType() {
                return listingType;
        }

        public void setListingType(String listingType) {
                this.listingType = listingType;
        }

        public List<NFTTransactions> getNftTransactionsList() {
                return nftTransactionsList;
        }

        public void setNftTransactionsList(List<NFTTransactions> nftTransactionsList) {
                this.nftTransactionsList = nftTransactionsList;
        }

        public Wallet getWallet() {
                return wallet;
        }

        public void setWallet(Wallet wallet) {
                this.wallet = wallet;
        }

        public float getListPrice() {
                return listPrice;
        }

        public void setListPrice(float listPrice) {
                this.listPrice = listPrice;
        }

        public String getCurrencyType() {
                return currencyType;
        }

        public void setCurrencyType(String currencyType) {
                this.currencyType = currencyType;
        }
}
