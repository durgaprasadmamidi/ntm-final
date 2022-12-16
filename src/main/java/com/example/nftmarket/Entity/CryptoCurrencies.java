package com.example.nftmarket.Entity;

import javax.persistence.*;

@Entity
public class CryptoCurrencies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cryptoId;

    private String currencyType;
    private float balance;

    @ManyToOne
    private Wallet wallet;

    public CryptoCurrencies(String currencyType, float balance) {
        this.currencyType = currencyType;
        this.balance = balance;
    }

    public CryptoCurrencies() {
    }

    public int getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(int cryptoId) {
        this.cryptoId = cryptoId;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
