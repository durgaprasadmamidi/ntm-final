package com.example.nftmarket.Controller;

import com.example.nftmarket.Entity.NFT;
import com.example.nftmarket.Entity.NFTTransactions;
import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Repository.NFTRepo;
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
import org.springframework.http.HttpStatus;
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
    private NFTRepo nftRepo;

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
    public RedirectView addNft(@ModelAttribute("createdNft")NFT createdNft,
            Principal principal
    ) {
//    	@RequestParam("name") String name,
//        @RequestParam("type") String type,
//        @RequestParam("description") String desc,
//        @RequestParam("image_url") String imageUrl,
//        @RequestParam("asset_url") String assetUrl
        Users user = userRepo.findByEmail(principal.getName());
        ResponseEntity<?> created = nftService.addNft(createdNft.getName(), createdNft.getListingType(), createdNft.getDescription(), createdNft.getImageUrl(), createdNft.getAssetUrl(), user);
        return new RedirectView("/nft/viewAll");
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
        System.out.println("%%%%%ID%%%%%%%%"+ nfti.getNftId());
        System.out.println("%%%%%money%%%%%%%%"+ nfti.getCurrencyType());
        System.out.println("%%%%%%SALETYPE%%%%%%%%%"+ nfti.getListingType());
        System.out.println("%%%%%%%%PRICE%%%%%%"+ nfti.getListPrice());
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
    public String viewOnSaleNfts(Principal principal, Model model) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
//        ResponseEntity<?> nft = nftService.viewOnSaleNfts(user);
//        List<NFT> nftsbuy = null;
//        NFT ns =null;
//        if(!nft.getBody().toString().equals("Operation failed")) {
//        	 nftsbuy = (List<NFT>) nft.getBody();
//        	 for (NFT nf : nftsbuy) {
//             	System.out.println("****************"+nf.getNftId());
//             	ns = nf;
//             } 
//        }
        List<NFT> unSoldNfts = new ArrayList<>();
        List<NFT> noSold = null;
        
        try {
            List<NFT> nftList = (List<NFT>) nftRepo.findAll();
            //int userId = 1;
            int userId = user.getUserId();
            List<NFT> listedForSaleNfts = nftList.stream().
                    filter(nft -> nft.isListedForSale() && nft.getWallet().getWalletId() != userRepo.findById(userId).get().getWallet().getWalletId()).
                    toList();
            
            for (NFT nft : listedForSaleNfts) {
                List<NFTTransactions> nftTransactions = nft.getNftTransactionsList();
                List<NFTTransactions> openTransactions = nftTransactions.stream().filter(
                        nftTransactions1 -> nftTransactions1.getTransactionStatus().equalsIgnoreCase("open")
                ).toList();
                if (openTransactions.size() > 0) {
                    unSoldNfts.add(nft);
                    System.out.println("77777777Opentransactions7777777777");
                }
                
                System.out.println("777777777777777777");
            }
            model.addAttribute("nftsbuy", unSoldNfts);
            if (unSoldNfts.size() == 0) {
            	model.addAttribute("nftsbuy", noSold);
            	return "blank";
                
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "Operation failed";
        }
        
       // List<NFT> nfts = (List<NFT>) nft.getBody();
        
              
        //model.addAttribute("createdNft", new NFT());
        //model.addAttribute("nfti", ns);
        
        return "Buy";
    }

    @PostMapping(value = "/buy/pricedItem/{nftId}")
    public ResponseEntity<?> buyPricedItem(@PathVariable int nftId, Principal principal) {
        Users user = userRepo.findByEmail(principal.getName());
        return nftService.buyPricedNFT(nftId, user);
    }
    
    @PostMapping(value = "/buy/item")
    public String buyPricedItems(Principal principal) {
        Users user = userRepo.findByEmail(principal.getName());
        return "nft";
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
