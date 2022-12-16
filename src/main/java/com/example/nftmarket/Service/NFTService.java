package com.example.nftmarket.Service;

import com.example.nftmarket.Entity.*;
import com.example.nftmarket.Repository.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

// TO DO

/**
 * Add feature to check expiration time and update wallet automatically
 */

@Service
public class NFTService {

    @Autowired
    private NFTRepo nftRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private NFTTransactionsRepo nftTransactionsRepo;

    @Autowired
    private AuctionHistoryRepo auctionHistoryRepo;

    @Autowired
    private CryptoTransactionsRepo cryptoTransactionsRepo;

    @Autowired
    private NFTCryptoTransactionsRepo nftCryptoTransactionsRepo;

    private final String LISTING_TYPE_PRICE = "priced";
    private final String LISTING_TYPE_AUCTION = "auction";
    private final String TRANSACTION_OPEN = "open";
    private final String TRANSACTION_CLOSED = "closed";
    private final String TRANSACTION_IN_PROGRESS = "inProgress";
    private final String AUCTION_ON_GOING = "onGoing";
    private final String AUCTION_ENDED = "ended";
    private final String AUCTION_BID_SUPERSEDED = "superseded";
    private final String AUCTION_OFFER_RESCINDED = "rescinded";
    private final String PURCHASE_NFT = "purchase";
    private final String SELL_NFT = "sell";

    public ResponseEntity<?> getNftsOfUser(Users user) throws JSONException {
        /*int user_id = 1;
        Users user = usersRepo.findById(user_id).get();*/
        Wallet wallet = user.getWallet();
        List<NFT> nfts = wallet.getNftList();
        //boolean boo = 0;
        NFT mock = new NFT("tokenId",  "smartContactAddress",  "name",  "type",  "description",  "imageUrl", "assetUrl",null, false, AUCTION_BID_SUPERSEDED);
        nfts.add(mock);
        
        if (nfts == null || nfts.size() == 0) {
            return new ResponseEntity<>("No nfts found", HttpStatus.BAD_REQUEST);
        }

        List<JSONObject> entities = new ArrayList<JSONObject>();
        for (NFT nft : nfts) {
            JSONObject entity = new JSONObject();
            entity.put("nftId", nft.getNftId());
            entity.put("name", nft.getName());
            entity.put("type", nft.getType());
            entity.put("description", nft.getDescription());
            entity.put("tokenId", nft.getTokenId());
            entity.put("smartContactAddress", nft.getSmartContactAddress());
            entity.put("imageUrl", nft.getImageUrl());
            entity.put("assetUrl", nft.getAssetUrl());
            entity.put("lastRecordTime", nft.getLastRecordTime());
            entities.add(entity);
            System.out.println("----------------------------========="+ entity.toString());
        }
        ResponseEntity<?> s = new ResponseEntity<>(entities.toString(), HttpStatus.OK);
        System.out.println("--------------------------------"+ s.getBody());
        return new ResponseEntity<>(nfts, HttpStatus.OK);
    }

    public ResponseEntity<?> addNft(String name, String type, String desc, String imageUrl, String assetUrl, Users user) {

        try {
            Timestamp currTime = new Timestamp(System.currentTimeMillis());
            String tokenId = name + currTime;
            String sca = tokenId + "address";
            NFT nft = new NFT(tokenId, sca, name, type, desc, imageUrl, assetUrl, currTime, false, null);

            /*int user_id = 1;
            Users user = usersRepo.findById(user_id).get();*/
            System.out.println(user.getUsername());
            Wallet wallet = user.getWallet();

            nft.setWallet(wallet);
            nftRepo.save(nft);

            List<NFT> nftList = wallet.getNftList();
            nftList.add(nft);
            wallet.setNftList(nftList);
            walletRepo.save(wallet);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("NFT creation failed", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("NFT created: " + name, HttpStatus.CREATED);

    }

    public ResponseEntity<?> sellNft(int nftId, String currencyType, String saleType, float listPrice, Users user) {

        try {
            NFT nft = nftRepo.findById(nftId).get();
            List<NFTTransactions> nftTransactionsList = nft.getNftTransactionsList();

            List<NFTTransactions> openOrInProgressTransactions = nftTransactionsList.
                    stream().
                    filter(nftTransactions -> nftTransactions.getTransactionStatus().equalsIgnoreCase(TRANSACTION_OPEN)
                            || nftTransactions.getTransactionStatus().equalsIgnoreCase(TRANSACTION_IN_PROGRESS)).
                    toList();

            if (openOrInProgressTransactions.size() == 0) {
                nft.setListedForSale(true);
                nft.setListingType(saleType);
                nft.setListPrice(listPrice);
                nft.setCurrencyType(currencyType);

                NFTTransactions nftTransactions = new NFTTransactions(currencyType, listPrice, 0, TRANSACTION_OPEN, null);
                nftTransactions.setNft(nft);
                nftTransactionsList.add(nftTransactions);
                //nft.setNftTransactionsList(nftTransactionsList);

                nftRepo.save(nft);
                //nftTransactionsRepo.save(nftTransactions);
            } else {
                return new ResponseEntity<>("Given NFT already listed for sale", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation failed", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("NFT listed for sale: " + nftRepo.findById(nftId).get().getName(), HttpStatus.OK);
    }

    public ResponseEntity<?> viewListedNfts(Users user) throws JSONException {

        /*int user_id = 1;
        Users user = usersRepo.findById(user_id).get();*/
        Wallet wallet = user.getWallet();
        List<NFT> nfts = wallet.getNftList();

        List<NFT> listedNfts = nfts.stream().filter(NFT::isListedForSale).toList();

        if (listedNfts.size() == 0) {
            return new ResponseEntity<>("No nfts listed for sale", HttpStatus.BAD_REQUEST);
        }

        List<JSONObject> entities = new ArrayList<JSONObject>();
        for (NFT nft : listedNfts) {
            JSONObject entity = new JSONObject();
            entity.put("nftId", nft.getNftId());
            entity.put("name", nft.getName());
            entity.put("type", nft.getType());
            entity.put("description", nft.getDescription());
            entity.put("tokenId", nft.getTokenId());
            entity.put("smartContactAddress", nft.getSmartContactAddress());
            entity.put("imageUrl", nft.getImageUrl());
            entity.put("assetUrl", nft.getAssetUrl());
            entity.put("lastRecordTime", nft.getLastRecordTime());
            entity.put("isListedForSale", nft.isListedForSale());
            entity.put("saleType", nft.getListingType());
            entity.put("listPrice", nft.getListPrice());
            entity.put("currencyType", nft.getCurrencyType());
            entities.add(entity);
        }

        return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
    }

    public ResponseEntity<?> cancelListing(int nftId, Users user) {
        try {
            NFT nft = nftRepo.findById(nftId).get();
            NFTTransactions openTransaction = getOpenTransaction(nft);
            openTransaction.setTransactionStatus(TRANSACTION_CLOSED);
            nft.setListedForSale(false);
            nftTransactionsRepo.save(openTransaction);
            nftRepo.save(nft);
            return new ResponseEntity<>("Listing cancelled : " + nft.getName(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> viewOnSaleNfts(Users user) {
        try {
            List<NFT> nftList = (List<NFT>) nftRepo.findAll();

            //int userId = 1;
            int userId = user.getUserId();
            List<NFT> listedForSaleNfts = nftList.stream().
                    filter(nft -> nft.isListedForSale() && nft.getWallet().getWalletId() != usersRepo.findById(userId).get().getWallet().getWalletId()).
                    toList();
            List<NFT> unSoldNfts = new ArrayList<>();

            for (NFT nft : listedForSaleNfts) {
                List<NFTTransactions> nftTransactions = nft.getNftTransactionsList();
                List<NFTTransactions> openTransactions = nftTransactions.stream().filter(
                        nftTransactions1 -> nftTransactions1.getTransactionStatus().equalsIgnoreCase(TRANSACTION_OPEN)
                ).toList();
                if (openTransactions.size() > 0) {
                    unSoldNfts.add(nft);
                }
            }

            if (unSoldNfts.size() == 0) {
                return new ResponseEntity<>("No NFTs available to buy", HttpStatus.OK);
            }

            List<JSONObject> entities = new ArrayList<>();
            for (NFT nft : unSoldNfts) {
                JSONObject entity = new JSONObject();
                entity.put("nftId", nft.getNftId());
                entity.put("name", nft.getName());
                entity.put("type", nft.getType());
                entity.put("description", nft.getDescription());
                entity.put("imageUrl", nft.getImageUrl());
                entity.put("lastRecordTime", nft.getLastRecordTime());
                entity.put("saleType", nft.getListingType());
                if (nft.getListingType().equalsIgnoreCase("auction")) {
                    NFTTransactions nftTransactions = nft.getNftTransactionsList().stream().
                            max(Comparator.comparing(NFTTransactions::getCurrentMaxBid)).
                            orElse(new NFTTransactions());
                    float maxBid = 0;
                    if (nftTransactions.getNft() == null) {
                        maxBid = 0;
                    } else {
                        maxBid = nftTransactions.getCurrentMaxBid();
                    }
                    entity.put("highestOffer", maxBid);
                }
                entity.put("listPrice", nft.getListPrice());
                entity.put("currencyType", nft.getCurrencyType());
                entities.add(entity);
            }

            return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> buyPricedNFT(int nftId, Users user) {
        try {
            /*int user_id = 2;
            Users user = usersRepo.findById(user_id).get();*/
            Wallet buyerWallet = user.getWallet();
            List<CryptoCurrencies> cryptoCurrencies = buyerWallet.getCryptoCurrenciesList();

            NFT nft = nftRepo.findById(nftId).get();
            Optional<CryptoCurrencies> currency = cryptoCurrencies.stream().
                    filter(cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())).
                    findFirst();

            if (currency.isPresent()) {
                CryptoCurrencies curr = currency.get();
                if (curr.getBalance() < nft.getListPrice()) {
                    return new ResponseEntity<>("Balance too low to buy. NFT price: " + nft.getListPrice() + ". Available balance: " + curr.getBalance(), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("User " + user.getUsername() + " doesn't have the accepted crypto currency in his wallet to buy this NFT: " + nft.getName(), HttpStatus.BAD_REQUEST);
            }

            // update currencies and wallets
            CryptoCurrencies buyerCurrency = currency.get();
            float prevBalance = buyerCurrency.getBalance();
            float currBalance = prevBalance - nft.getListPrice();
            buyerCurrency.setBalance(currBalance);

            Wallet sellerWallet = nft.getWallet();
            nft.setWallet(buyerWallet);
            sellerWallet.getNftList().remove(nft);

            CryptoCurrencies sellerCurrency = sellerWallet.getCryptoCurrenciesList().stream().
                    filter(cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(buyerCurrency.getCurrencyType())).
                    findFirst().
                    orElse(new CryptoCurrencies(buyerCurrency.getCurrencyType(), 0));
            sellerCurrency.setBalance(sellerCurrency.getBalance() + nft.getListPrice());

            nft.setSmartContactAddress(nft.getSmartContactAddress() + user.getUserId());
            nft.setLastRecordTime(new Timestamp(System.currentTimeMillis()));
            nft.setListedForSale(false);

            // update transactions
            NFTTransactions openTransaction = getOpenTransaction(nft);
            openTransaction.setTransactionStatus(TRANSACTION_CLOSED);
            openTransaction.setPricedSaleHistory(
                    new PricedSaleHistory(new Timestamp(System.currentTimeMillis()), sellerWallet.getWalletId(), buyerWallet.getWalletId(), nft.getListPrice())
            );

            CryptoTransactions cryptoTransactionsBuyer = new CryptoTransactions(
                    PURCHASE_NFT, nft.getCurrencyType(), nft.getListPrice(), prevBalance, currBalance, buyerCurrency
            );
            CryptoTransactions cryptoTransactionsSeller = new CryptoTransactions(
                    SELL_NFT, nft.getCurrencyType(), nft.getListPrice(), sellerCurrency.getBalance() - nft.getListPrice(), sellerCurrency.getBalance(), sellerCurrency
            );

            NFTCryptoTransactions nftCryptoTransactionsBuyer = new NFTCryptoTransactions(
                    nft.getListPrice(), sellerWallet.getWalletId(), buyerWallet.getWalletId(), new Timestamp(System.currentTimeMillis()), openTransaction, cryptoTransactionsBuyer
            );
            NFTCryptoTransactions nftCryptoTransactionsSeller = new NFTCryptoTransactions(
                    nft.getListPrice(), sellerWallet.getWalletId(), buyerWallet.getWalletId(), new Timestamp(System.currentTimeMillis()), openTransaction, cryptoTransactionsSeller
            );

            List<NFTCryptoTransactions> cryptoTransactions = Arrays.asList(nftCryptoTransactionsSeller, nftCryptoTransactionsBuyer);
            openTransaction.setNftCryptoTransactions(cryptoTransactions);

            cryptoTransactionsRepo.save(cryptoTransactionsBuyer);
            nftCryptoTransactionsRepo.save(nftCryptoTransactionsBuyer);
            cryptoTransactionsRepo.save(cryptoTransactionsSeller);
            nftCryptoTransactionsRepo.save(nftCryptoTransactionsSeller);

            return new ResponseEntity<>("NFT " + nft.getName() + " is sold to user " + user.getUsername() + ". Sold by: " + sellerWallet.getUser().getUsername(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> makeOfferAuctionItem(int nftId, float offerPrice, int expirationSeconds, Users user) {

        try {
            /*int user_id = 2;
            Users user = usersRepo.findById(user_id).get()*/;
            Wallet wallet = user.getWallet();
            List<CryptoCurrencies> cryptoCurrencies = wallet.getCryptoCurrenciesList();

            NFT nft = nftRepo.findById(nftId).get();
            Optional<CryptoCurrencies> currency = cryptoCurrencies.stream().
                    filter(cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())).
                    findFirst();

            CryptoCurrencies buyerCurrency;
            NFTTransactions openTransaction;
            if (currency.isPresent()) {
                buyerCurrency = currency.get();
                openTransaction = nft.getNftTransactionsList().stream().
                        filter(nftTransactions1 -> !nftTransactions1.getTransactionStatus().equalsIgnoreCase(TRANSACTION_CLOSED)).findFirst().get();

                // offer lower than current maximum bid
                if (openTransaction.getCurrentMaxBid() >= offerPrice) {
                    return new ResponseEntity<>("Existing offer in place with higher or equal bid price. Existing maximum offer: " + openTransaction.getCurrentMaxBid(), HttpStatus.BAD_REQUEST);
                }
                // balance low to make offer
                if (buyerCurrency.getBalance() < offerPrice) {
                    return new ResponseEntity<>("Balance too low to make offer. Available balance: " + buyerCurrency.getBalance() + ". Current max offer: " + openTransaction.getCurrentMaxBid(), HttpStatus.BAD_REQUEST);
                }
            } else {
                // accepted currency not found
                return new ResponseEntity<>("User " + user.getUsername() + " doesn't have the accepted crypto currency in his wallet to make offer this NFT: " + nft.getName(), HttpStatus.BAD_REQUEST);
            }

            // parse expiration time
            Timestamp expirationTime = Timestamp.from(new Timestamp(System.currentTimeMillis()).toInstant().plusSeconds(expirationSeconds));

            // if this is the first bid, update only buyer wallet
            if (openTransaction.getCurrentMaxBid() == 0) {
                buyerCurrency.setBalance(buyerCurrency.getBalance() - offerPrice);
            }

            // user's latest bid cancelling his previous bid -> which is the maximum bid already. Update old auction record and update buyer wallet
            else if (openTransaction.getAuctionHistories().stream().anyMatch(
                    auctionHistory -> auctionHistory.getBuyerWalletId() == wallet.getWalletId() &&
                            !auctionHistory.isSold() &&
                            auctionHistory.getAuctionStatus().equalsIgnoreCase(AUCTION_ON_GOING)
            )) {
                AuctionHistory auctionHistory = openTransaction.getAuctionHistories().stream().filter(
                        auctionHistory1 -> !auctionHistory1.isSold() && auctionHistory1.getBuyerWalletId() == wallet.getWalletId()
                ).findFirst().get();
                auctionHistory.setAuctionStatus(AUCTION_BID_SUPERSEDED);

                // update buyer wallet
                buyerCurrency.setBalance(buyerCurrency.getBalance() + auctionHistory.getOfferPrice());
                buyerCurrency.setBalance(buyerCurrency.getBalance() - offerPrice);
            }

            // user bidding higher than existing bid of other user, update old auction record and update both buyer and existing offered user wallet
            else {
                //update previous auction record
                AuctionHistory existingAuctionHistory = openTransaction.getAuctionHistories().stream().filter(
                        auctionHistory1 -> auctionHistory1.getAuctionStatus().equalsIgnoreCase(AUCTION_ON_GOING) &&
                                !auctionHistory1.isSold()
                ).findFirst().get();
                existingAuctionHistory.setAuctionStatus(AUCTION_BID_SUPERSEDED);

                //update buyer wallet and previous offer's wallet
                Wallet prevOfferWallet = walletRepo.findById(existingAuctionHistory.getBuyerWalletId()).get();
                CryptoCurrencies sellerCurrency = prevOfferWallet.getCryptoCurrenciesList().stream().filter(
                        cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())
                ).findFirst().get();
                sellerCurrency.setBalance(sellerCurrency.getBalance() + existingAuctionHistory.getOfferPrice());

                buyerCurrency.setBalance(buyerCurrency.getBalance() - offerPrice);
            }

            // create new auction record
            AuctionHistory auctionHistory = new AuctionHistory(offerPrice, new Timestamp(System.currentTimeMillis()), expirationTime, false, AUCTION_ON_GOING, wallet.getWalletId());
            List<AuctionHistory> auctionHistories = new ArrayList<>();
            auctionHistories.add(auctionHistory);
            openTransaction.setAuctionHistories(auctionHistories);
            openTransaction.setCurrentMaxBid(offerPrice);
            openTransaction.setCurrentMaxOfferExpTime(expirationTime);
            auctionHistory.setNftTransactions(openTransaction);

            auctionHistoryRepo.save(auctionHistory);

            return new ResponseEntity<>("Successfully placed your bid for " + nft.getName() + " for " + offerPrice + " that expires on " + expirationTime + ".", HttpStatus.ACCEPTED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<?> viewReceivedOffersForAuctionItem(Users user) throws JSONException {

        try {
            //int userid = 1;
            int userid = user.getUserId();
            Wallet wallet = usersRepo.findById(userid).get().getWallet();

            List<NFT> nftList = wallet.getNftList().stream().filter(
                    nft -> nft.isListedForSale() &&
                            nft.getListingType().equalsIgnoreCase(LISTING_TYPE_AUCTION)
            ).toList();

            List<JSONObject> entities = new ArrayList<>();
            for (NFT nft : nftList) {
                JSONObject entity = new JSONObject();
                entity.put("nftId", nft.getNftId());
                entity.put("name", nft.getName());
                entity.put("type", nft.getType());
                entity.put("description", nft.getDescription());
                entity.put("imageUrl", nft.getImageUrl());
                entity.put("lastRecordTime", nft.getLastRecordTime());
                entity.put("saleType", nft.getListingType());
                NFTTransactions nftTransactions = nft.getNftTransactionsList().stream().
                        max(Comparator.comparing(NFTTransactions::getCurrentMaxBid)).
                        orElse(new NFTTransactions());
                entity.put("highestOffer", nftTransactions.getCurrentMaxBid());
                entity.put("highestOfferExpirationTime", nftTransactions.getCurrentMaxOfferExpTime());
                if (nftTransactions.getCurrentMaxBid() != 0) {
                    AuctionHistory auctionHistory = nftTransactions.getAuctionHistories().stream().filter(
                            auctionHistory1 -> auctionHistory1.getAuctionStatus().equalsIgnoreCase(AUCTION_ON_GOING) &&
                                    !auctionHistory1.isSold()
                    ).findFirst().get();
                    entity.put("highestOfferedUser", walletRepo.findById(auctionHistory.getBuyerWalletId()).get().getUser().getUsername());
                    entity.put("offerId", auctionHistory.getAuctionUpdateId());
                }
                entity.put("bidsReceived", nftTransactions.getAuctionHistories().stream().filter(auctionHistory1 -> !auctionHistory1.isSold()).toList().size());
                entity.put("listPrice", nft.getListPrice());
                entity.put("currencyType", nft.getCurrencyType());
                entities.add(entity);
            }
            return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> viewOffersMadeForAuctionItem(Users user) {
        try {
            //int userid = 2;
            int userid = user.getUserId();
            Wallet wallet = usersRepo.findById(userid).get().getWallet();

            List<AuctionHistory> auctionHistories = (List<AuctionHistory>) auctionHistoryRepo.findAll();
            List<AuctionHistory> userAuctionHistory = auctionHistories.stream().filter(
                    auctionHistory -> auctionHistory.getBuyerWalletId() == wallet.getWalletId()
            ).toList();

            if (userAuctionHistory.size() == 0) {
                return new ResponseEntity<>("No bids made yet by the user", HttpStatus.BAD_REQUEST);
            }

            List<JSONObject> entities = new ArrayList<>();

            for (AuctionHistory auctionHistory : userAuctionHistory) {
                JSONObject entity = new JSONObject();
                entity.put("nftId", auctionHistory.getNftTransactions().getNft().getNftId());
                entity.put("name", auctionHistory.getNftTransactions().getNft().getName());
                entity.put("type", auctionHistory.getNftTransactions().getNft().getType());
                entity.put("description", auctionHistory.getNftTransactions().getNft().getDescription());
                entity.put("saleType", auctionHistory.getNftTransactions().getNft().getListingType());
                entity.put("listPrice", auctionHistory.getNftTransactions().getNft().getListPrice());
                entity.put("offerPrice", auctionHistory.getOfferPrice());
                entity.put("currencyType", auctionHistory.getNftTransactions().getNft().getCurrencyType());
                entity.put("auctionStatus", auctionHistory.getAuctionStatus());
                entity.put("isItemSold", auctionHistory.isSold());
                entity.put("auctionBidId", auctionHistory.getAuctionUpdateId());
                entities.add(entity);
            }

            return new ResponseEntity<>(entities.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> cancelOfferAuctionItem(int nftId, int auctionBidId, Users user) {

        try {
            //int userid = 2;
            int userid = user.getUserId();
            Wallet wallet = usersRepo.findById(userid).get().getWallet();

            NFT nft = nftRepo.findById(nftId).get();
            Optional<AuctionHistory> ah = auctionHistoryRepo.findById(auctionBidId);
            if (ah.isEmpty()) {
                return new ResponseEntity<>("No auction ID matched with given request", HttpStatus.BAD_REQUEST);
            }
            AuctionHistory auctionHistory = ah.get();
            NFTTransactions openTransaction = auctionHistory.getNftTransactions();

            CryptoCurrencies buyerCurrency = wallet.getCryptoCurrenciesList().stream().filter(
                    cryptoCurrencies -> cryptoCurrencies.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())
            ).findFirst().get();

            auctionHistory.setAuctionStatus(AUCTION_OFFER_RESCINDED);
            buyerCurrency.setBalance(buyerCurrency.getBalance() + auctionHistory.getOfferPrice());

            Optional<AuctionHistory> nextMaxOffer = openTransaction.getAuctionHistories().stream().filter(
                    auctionHistory1 -> auctionHistory1.getAuctionStatus().equalsIgnoreCase(AUCTION_BID_SUPERSEDED)
            ).max(Comparator.comparing(AuctionHistory::getOfferPrice));

            if (nextMaxOffer.isPresent()) {
                openTransaction.setCurrentMaxBid(nextMaxOffer.get().getOfferPrice());
                openTransaction.setCurrentMaxOfferExpTime(nextMaxOffer.get().getExpirationTime());
            } else {
                openTransaction.setCurrentMaxBid(0);
                openTransaction.setCurrentMaxOfferExpTime(null);
            }

            auctionHistoryRepo.save(auctionHistory);
            return new ResponseEntity<>("Offer rescinded for the NFT " + nft.getName(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> acceptOfferAuctionItem(int nftId, int auctionBidId, Users user) {
        try {
            //int userid = 1;
            int userid = user.getUserId();
            Wallet sellerWallet = usersRepo.findById(userid).get().getWallet();

            NFT nft = nftRepo.findById(nftId).get();
            Optional<AuctionHistory> ah = auctionHistoryRepo.findById(auctionBidId);
            if (ah.isEmpty()) {
                return new ResponseEntity<>("No auction ID matched with given request", HttpStatus.BAD_REQUEST);
            }
            AuctionHistory auctionHistory = ah.get();
            NFTTransactions openTransaction = auctionHistory.getNftTransactions();
            Wallet buyerWallet = walletRepo.findById(auctionHistory.getBuyerWalletId()).get();

            // update auction
            auctionHistory.setAuctionStatus(AUCTION_ENDED);
            auctionHistory.setSold(true);

            // update NFT transaction
            openTransaction.setTransactionStatus(TRANSACTION_CLOSED);

            // update crypto balance
            Optional<CryptoCurrencies> currency = sellerWallet.getCryptoCurrenciesList().stream().
                    filter(cryptoCurrencies1 -> cryptoCurrencies1.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())).
                    findFirst();

            CryptoCurrencies sellerCurrency;
            if (currency.isPresent()) {
                sellerCurrency = currency.get();
                sellerCurrency.setBalance(sellerCurrency.getBalance() + auctionHistory.getOfferPrice());
            } else {
                sellerCurrency = new CryptoCurrencies(nft.getCurrencyType(), auctionHistory.getOfferPrice());
                List<CryptoCurrencies> cryptoCurrenciesList = new ArrayList<>();
                cryptoCurrenciesList.add(sellerCurrency);
                sellerWallet.setCryptoCurrenciesList(cryptoCurrenciesList);
            }
            // not setting up buyer currency since it is done during making bid

            // update wallet
            nft.setWallet(buyerWallet);
            sellerWallet.getNftList().remove(nft);

            // update nft
            Users buyer = buyerWallet.getUser();
            nft.setSmartContactAddress(nft.getSmartContactAddress() + buyer.getUserId());
            nft.setLastRecordTime(new Timestamp(System.currentTimeMillis()));
            nft.setListedForSale(false);

            // update crypto and nft crypto transactions
            CryptoCurrencies buyerCurrency = buyerWallet.getCryptoCurrenciesList().stream().filter(
                    cryptoCurrencies -> cryptoCurrencies.getCurrencyType().equalsIgnoreCase(nft.getCurrencyType())
            ).findFirst().get();
            CryptoTransactions cryptoTransactionsBuyer = new CryptoTransactions(
                    PURCHASE_NFT, nft.getCurrencyType(), nft.getListPrice(), buyerCurrency.getBalance(), buyerCurrency.getBalance() - auctionHistory.getOfferPrice(), buyerCurrency
            );
            CryptoTransactions cryptoTransactionsSeller = new CryptoTransactions(
                    SELL_NFT, nft.getCurrencyType(), nft.getListPrice(), sellerCurrency.getBalance() - auctionHistory.getOfferPrice(), sellerCurrency.getBalance(), sellerCurrency
            );

            NFTCryptoTransactions nftCryptoTransactionsBuyer = new NFTCryptoTransactions(
                    nft.getListPrice(), sellerWallet.getWalletId(), buyerWallet.getWalletId(), new Timestamp(System.currentTimeMillis()), openTransaction, cryptoTransactionsBuyer
            );
            NFTCryptoTransactions nftCryptoTransactionsSeller = new NFTCryptoTransactions(
                    nft.getListPrice(), sellerWallet.getWalletId(), buyerWallet.getWalletId(), new Timestamp(System.currentTimeMillis()), openTransaction, cryptoTransactionsSeller
            );

            List<NFTCryptoTransactions> cryptoTransactions = Arrays.asList(nftCryptoTransactionsSeller, nftCryptoTransactionsBuyer);
            openTransaction.setNftCryptoTransactions(cryptoTransactions);

            cryptoTransactionsRepo.save(cryptoTransactionsBuyer);
            nftCryptoTransactionsRepo.save(nftCryptoTransactionsBuyer);
            cryptoTransactionsRepo.save(cryptoTransactionsSeller);
            nftCryptoTransactionsRepo.save(nftCryptoTransactionsSeller);

            return new ResponseEntity<>("NFT " + nft.getName() + " is sold to user " + buyer.getUsername() + " through auctions for " + auctionHistory.getOfferPrice() + " " + nft.getCurrencyType() + ". Sold by: " + sellerWallet.getUser().getUsername(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Operation Failed", HttpStatus.BAD_REQUEST);
        }
    }

    private static NFTTransactions getOpenTransaction(NFT nft) {
        List<NFTTransactions> nftTransactionsList = nft.getNftTransactionsList();
        return nftTransactionsList.
                stream().
                filter(nftTransactions -> nftTransactions.getTransactionStatus().equalsIgnoreCase("open")).
                findFirst().
                get();
    }

}
