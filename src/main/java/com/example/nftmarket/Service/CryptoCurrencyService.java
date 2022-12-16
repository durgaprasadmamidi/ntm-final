package com.example.nftmarket.Service;

import com.example.nftmarket.Entity.*;
import com.example.nftmarket.Repository.CryptoCurrenciesRepo;
import com.example.nftmarket.Repository.CryptoTransactionsRepo;
import com.example.nftmarket.Repository.WalletRepo;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CryptoCurrencyService {

    @Autowired
    private CryptoCurrenciesRepo cryptoCurrenciesRepo;
    @Autowired
    private CryptoTransactionsRepo cryptoTransactionsRepo;
    @Autowired
    private WalletRepo walletRepo;

    private final String CRYPTO_DEPOSIT = "deposit";
    private final String CRYPTO_WITHDRAW = "withdraw";

    public ResponseEntity<?> getCryptosOfUser(Users user) throws JSONException {
        Wallet wallet = user.getWallet();
        List<CryptoCurrencies> cryptoCurrenciesList = wallet.getCryptoCurrenciesList();
        if (cryptoCurrenciesList == null || cryptoCurrenciesList.size() == 0) {
            return new ResponseEntity<>("No Crypto Currency found on wallet", HttpStatus.BAD_REQUEST);
        }

        List<JSONObject> entities = new ArrayList<>();
        for (CryptoCurrencies c : cryptoCurrenciesList) {
            JSONObject entity = new JSONObject();
            entity.put("cryptoId", c.getCryptoId());
            entity.put("cryptoType", c.getCurrencyType());
            entity.put("cryptoBalance", c.getBalance());
            entities.add(entity);
        }

//        return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
     return new ResponseEntity<>(cryptoCurrenciesList, HttpStatus.OK);
        
    }

    public ResponseEntity<?> depositCrypto(String type, float amount, Users user) {
        try{
            Wallet wallet = user.getWallet();
            List<CryptoCurrencies> cryptoCurrenciesList = wallet.getCryptoCurrenciesList();

            CryptoCurrencies currency;
            boolean currencyFound = false;
            if (cryptoCurrenciesList == null || cryptoCurrenciesList.size() == 0 ||
                    cryptoCurrenciesList.stream().noneMatch(
                            cryptoCurrencies -> cryptoCurrencies.getCurrencyType().equalsIgnoreCase(type)
                    )
            ) {
                currency = new CryptoCurrencies(type, 0);
            }
            else {
                currency = cryptoCurrenciesList.stream().filter(
                        cryptoCurrencies -> cryptoCurrencies.getCurrencyType().equalsIgnoreCase(type)
                ).findFirst().get();
                currencyFound = true;
            }

            if (cryptoCurrenciesList == null) {
                cryptoCurrenciesList = new ArrayList<>();
            }

            if (!currencyFound) {
                cryptoCurrenciesList.add(currency);
            }

            currency.setBalance(amount + currency.getBalance());
            currency.setWallet(wallet);
            cryptoCurrenciesRepo.save(currency);

            wallet.setCryptoCurrenciesList(cryptoCurrenciesList);
            walletRepo.save(wallet);

            CryptoTransactions cryptoTransactions = new CryptoTransactions(CRYPTO_DEPOSIT, currency.getCurrencyType(), amount, currency.getBalance() - amount, currency.getBalance(), currency);
            cryptoTransactionsRepo.save(cryptoTransactions);

            return new ResponseEntity<>("Cryptocurrency deposit successful", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Cryptocurrency deposit failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> withdrawCrypto(String type, float amount, Users user) {
        try{
            Wallet wallet = user.getWallet();
            Optional<CryptoCurrencies> curr = wallet.getCryptoCurrenciesList().stream().
                    filter(cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(type)).
                    findFirst();

            if (curr.isEmpty()) {
                return new ResponseEntity<>("Requested Cryptocurrency not found in wallet", HttpStatus.BAD_REQUEST);
            }

            CryptoCurrencies currency = curr.get();
            if (currency.getBalance() < amount) {
                return new ResponseEntity<>("Low balance for the requested cryptocurrency. Withdrawal Failed.", HttpStatus.BAD_REQUEST);
            }

            currency.setBalance(currency.getBalance() - amount);
            CryptoTransactions cryptoTransactions = new CryptoTransactions(CRYPTO_DEPOSIT, currency.getCurrencyType(), amount, currency.getBalance() + amount, currency.getBalance(), currency);
            cryptoTransactionsRepo.save(cryptoTransactions);

            return new ResponseEntity<>("Cryptocurrency withdrawal successful. New balance: " + currency.getBalance(), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Cryptocurrency withdrawal failed", HttpStatus.BAD_REQUEST);
        }
    }
}
