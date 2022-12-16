package com.example.nftmarket.Service;

import com.example.nftmarket.Entity.NFT;
import com.example.nftmarket.Entity.NFTTransactions;
import com.example.nftmarket.Repository.NFTRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private NFTRepo nftRepo;

    public ResponseEntity<?> getDashboard() {

        try {
            List<NFT> nftList = (List<NFT>) nftRepo.findAll();
            System.out.println("Is my list coming?");
            int onSaleNfts = 0;
            int onSalePricedNfts = 0;
            int onSaleAuctionNfts = 0;
            int onSaleWithOffer = 0;
            int onSaleWithoutOffer = 0;
            int totalActiveOffers = 0;

            for (NFT nft : nftList) {
                if (nft.isListedForSale()) {
                    onSaleNfts++;
                    if (nft.getListingType()==null || nft.getListingType().equalsIgnoreCase("auction")) {
                        onSaleAuctionNfts++;
                        List<NFTTransactions> nftTransactions = nft.getNftTransactionsList();
                        if (nftTransactions.stream().anyMatch(
                                nftTransactions1 -> nftTransactions1.getCurrentMaxBid() != 0
                        )) {
                            onSaleWithOffer++;

                            totalActiveOffers += nftTransactions.stream().filter(
                                    nftTransactions1 -> nftTransactions1.getCurrentMaxBid() != 0
                            ).toList().size();
                        }
                        else {
                            onSaleWithoutOffer++;
                        }
                    } else {
                        onSalePricedNfts++;
                    }
                }
            }

            JSONObject entity = new JSONObject();
            entity.put("onSaleNfts", onSaleNfts);
            entity.put("onSalePricedNfts", onSalePricedNfts);
            entity.put("onSaleAuctionNfts", onSaleAuctionNfts);
            entity.put("onSaleWithOffer", onSaleWithOffer);
            entity.put("onSaleWithoutOffer", onSaleWithoutOffer);
            entity.put("totalActiveOffers", totalActiveOffers);
            
            return new ResponseEntity<>(entity.toString(), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }
}