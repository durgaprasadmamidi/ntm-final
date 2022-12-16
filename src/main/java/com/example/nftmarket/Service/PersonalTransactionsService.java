package com.example.nftmarket.Service;

import com.example.nftmarket.Entity.*;
import com.example.nftmarket.Repository.CryptoTransactionsRepo;
import com.example.nftmarket.Repository.NFTCryptoTransactionsRepo;
import com.example.nftmarket.Repository.UsersRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalTransactionsService {

    @Autowired
    private CryptoTransactionsRepo cryptoTransactionsRepo;
    @Autowired
    private NFTCryptoTransactionsRepo nftCryptoTransactionsRepo;
    @Autowired
    private UsersRepo usersRepo;

    public ResponseEntity<?> getMyTransactions(Users user) {

        try {

            Wallet wallet = user.getWallet();
            List<CryptoCurrencies> cryptoCurrenciesList = wallet.getCryptoCurrenciesList();
            List<CryptoTransactions> cryptoTransactions = (List<CryptoTransactions>) cryptoTransactionsRepo.findAll();
            List<CryptoTransactions> userCryptoTransactions = new ArrayList<>();
            List<NFTCryptoTransactions> userNFTCryptoTransactions = new ArrayList<>();

            for (CryptoCurrencies c : cryptoCurrenciesList) {
                userCryptoTransactions.addAll(cryptoTransactions.stream().filter(
                        cryptoTransactions1 -> cryptoTransactions1.getCryptoCurrencies().equals(c)
                ).toList());
            }

            List<NFTCryptoTransactions> nftCryptoTransactions = (List<NFTCryptoTransactions>) nftCryptoTransactionsRepo.findAll();
            for (NFTCryptoTransactions n: nftCryptoTransactions) {
                for (CryptoTransactions c: userCryptoTransactions) {
                    if (n.getCryptoTransactions().equals(c)) {
                        userNFTCryptoTransactions.add(n);
                    }
                }
            }

            if (userCryptoTransactions.size() == 0) {
                return new ResponseEntity<>("No transactions found for user", HttpStatus.BAD_REQUEST);
            }

            List<JSONObject> entities = new ArrayList<>();

            for (CryptoTransactions c : userCryptoTransactions) {
                JSONObject entity = new JSONObject();
                entity.put("cryptoTransactionId", c.getCryptoTransactionId());
                entity.put("transactionType", c.getTransactionType());
                entity.put("currencyType", c.getCurrencyType());
                entity.put("oldBalance", c.getPreviousBalance());
                entity.put("newBalance", c.getUpdatedBalance());
                entity.put("transactionAmount", c.getTransactionAmount());
                if (userNFTCryptoTransactions.size() != 0) {
                    for (NFTCryptoTransactions n : userNFTCryptoTransactions) {
                        if (n.getCryptoTransactions().equals(c)) {
                            entity.put("nftBuyer", usersRepo.findById(n.getBuyerId()).get().getUsername());
                            entity.put("nftSeller", usersRepo.findById(n.getSellerId()).get().getUsername());
                            entity.put("transactionTime", n.getTransactionTime());
                            entity.put("nftName", n.getNftTransactions().getNft().getName());
                        }
                    }
                }
                entities.add(entity);
            }

            return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("No transactions found for user", HttpStatus.BAD_REQUEST);
        }
    }
}
