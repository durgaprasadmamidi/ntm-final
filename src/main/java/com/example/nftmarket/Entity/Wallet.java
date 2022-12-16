package com.example.nftmarket.Entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "walletId")
    private int walletId;

    @OneToOne(mappedBy = "wallet")
    private Users user;

    @OneToMany(targetEntity = NFT.class, cascade=CascadeType.ALL)
    private List<NFT> nftList;

    @OneToMany(targetEntity = CryptoCurrencies.class, cascade=CascadeType.ALL)
    private List<CryptoCurrencies> cryptoCurrenciesList;

    public Wallet() {
    }
    public Wallet(Users user) {
    	this.user = user;
    }
    public Wallet(int walletId, Users user, List<NFT> nftList, List<CryptoCurrencies> cryptoCurrenciesList) {
        this.walletId = walletId;
        this.user = user;
        this.nftList = nftList;
        this.cryptoCurrenciesList = cryptoCurrenciesList;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<NFT> getNftList() {
        return nftList;
    }

    public void setNftList(List<NFT> nftList) {
        this.nftList = nftList;
    }

    public List<CryptoCurrencies> getCryptoCurrenciesList() {
        return cryptoCurrenciesList;
    }

    public void setCryptoCurrenciesList(List<CryptoCurrencies> cryptoCurrenciesList) {
        this.cryptoCurrenciesList = cryptoCurrenciesList;
    }
}
