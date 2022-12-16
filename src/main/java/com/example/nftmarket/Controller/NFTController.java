package com.example.nftmarket.Controller;

import com.example.nftmarket.Entity.NFT;
import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Repository.UsersRepo;
import com.example.nftmarket.Service.NFTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;



@Controller()
@RequestMapping(value = "/nft")
public class NFTController {

    @Autowired
    private NFTService nftService;

    @Autowired
    private UsersRepo userRepo;

    @GetMapping(value = "/viewAll")
    public String getNfts(Principal principal, Model model) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
        ResponseEntity<?> nft = nftService.getNftsOfUser(user);
        List<NFT> nfts = (List<NFT>) nft.getBody();
        NFT ns =null;
        for (NFT nf : nfts) {
        	System.out.println("****************"+nf.getNftId());
        	ns = nf;
        }       
        model.addAttribute("nfti", ns);
        model.addAttribute("nfts", nfts);
        return "MyNFT";
    }
    
    @RequestMapping("/CurrentListedNFT")
    public String CurrentListedNFT() {
    return "CurrentListedNFT"; //defect-details.html page name to open it
    }


    @PostMapping(value = "/create")
    public ResponseEntity<?> addNft(
            Principal principal,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String desc,
            @RequestParam("image_url") String imageUrl,
            @RequestParam("asset_url") String assetUrl
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.addNft(name, type, desc, imageUrl, assetUrl, user);
    }

    @PostMapping(value = "/sell")
    public RedirectView sellNft(@ModelAttribute("nfti")NFT nfti,
            Principal principal
            
    ) {
//    	@RequestParam("nftId") int nftId,
//        @RequestParam("currencyType") String currencyType,
//        @RequestParam("saleType") String saleType,
//        @RequestParam("listPrice") float listPrice
        Users user = userRepo.findByEmail(principal.getName());
        ResponseEntity<?> sold = nftService.sellNft(nfti.getNftId(), nfti.getCurrencyType(), nfti.getListingType(), nfti.getListPrice(), user);
        //((Object) sold).sendRedirect();
        return new RedirectView("/nft/viewAll");
    }

    @GetMapping(value = "/sell/viewListings")
    public ResponseEntity<?> viewListedNfts(Principal principal) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.viewListedNfts(user);
    }

    @PostMapping(value = "/sell/cancelListing/{nftId}")
    public ResponseEntity<?> cancelListing(@PathVariable int nftId, Principal principal) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.cancelListing(nftId, user);
    }

    @GetMapping(value = "/buy/viewListings")
    public ResponseEntity<?> viewOnSaleNfts(Principal principal) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.viewOnSaleNfts(user);
    }

    @PostMapping(value = "/buy/pricedItem/{nftId}")
    public ResponseEntity<?> buyPricedItem(@PathVariable int nftId, Principal principal) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.buyPricedNFT(nftId, user);
    }

    @PostMapping(value = "/buy/makeOffer/auctionItem")
    public ResponseEntity<?> makeOfferAuctionItem(
            Principal principal,
            @RequestParam("nftId") int nftId,
            @RequestParam("offerPrice") float offerPrice,
            @RequestParam("expirationSeconds") int expirationSeconds
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.makeOfferAuctionItem(nftId, offerPrice, expirationSeconds, user);
    }

    @PostMapping(value = "/buy/cancelOffer/auctionItem")
    public ResponseEntity<?> cancelOfferAuctionItem(
            Principal principal,
            @RequestParam("nftId") int nftId,
            @RequestParam("auctionBidId") int auctionBidId
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.cancelOfferAuctionItem(nftId, auctionBidId, user);
    }

    @GetMapping(value = "/sell/viewOffers")
    public ResponseEntity<?> viewOffersReceived(Principal principal) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.viewReceivedOffersForAuctionItem(user);
    }

    @GetMapping(value = "/buy/viewOffers")
    public ResponseEntity<?> viewOffersMade(Principal principal) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.viewOffersMadeForAuctionItem(user);
    }

    @PostMapping(value = "/sell/acceptOffer/")
    public ResponseEntity<?> acceptOfferAuctionItem(
            Principal principal,
            @RequestParam("nftId") int nftId,
            @RequestParam("auctionBidId") int auctionBidId
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.acceptOfferAuctionItem(nftId, auctionBidId, user);
    }
}
