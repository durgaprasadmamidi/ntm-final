package com.example.nftmarket.Controller;

import com.example.nftmarket.Entity.CryptoCurrencies;
import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Repository.UsersRepo;
import com.example.nftmarket.Service.CryptoCurrencyService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/crypto")
public class CryptoCurrencyController {

    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    @GetMapping(value = "/viewAll")
    public String getCryptos(Principal principal,Model model) throws JSONException {
        Users user = userRepo.findByEmail(principal.getName());
 
        ResponseEntity<?> crypto =  cryptoCurrencyService.getCryptosOfUser(user);
        List<CryptoCurrencies> cryptoList = (List<CryptoCurrencies>)crypto.getBody();
        for(CryptoCurrencies cr: cryptoList) {
        	if( cr.getCurrencyType() == "BTC")
        		model.addAttribute("btcBalance",cr.getBalance());
        	if( cr.getCurrencyType() == "ETH")
        		model.addAttribute("etcBalance",cr.getBalance());
   
        }
        model.addAttribute("createdCrypto", new CryptoCurrencies());
        return "wallet";
        
    }
    
//    @PostMapping("/process-form")
//    public String processForm(@ModelAttribute("formData") FormData formData, Model model) {
//      // Process the currency and amount values as needed
//      String currency = formData.getCurrency();
//      Double amount = formData.getAmount();
//
//      // Add the processed values to the model
//      model.addAttribute("currency", currency);
//      model.addAttribute("amount", amount);
//
//      // Return the name of the view to render
//      return "form-results";
//    }

    @PostMapping(value = "/deposit")
    public RedirectView depositCrypto(
            Principal principal, Model model,
            @ModelAttribute("createdNft") CryptoCurrencies createdCrypto
//            @RequestParam("type") String type,
//            @RequestParam("amount") float amount
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        model.addAttribute("createdCrypto", new CryptoCurrencies());
        ResponseEntity<?> created =  cryptoCurrencyService.depositCrypto(createdCrypto.getCurrencyType(), createdCrypto.getBalance(), user);
        return new RedirectView("/wallet");
    }
    
    @PostMapping(value = "/withdraw")
    public RedirectView withdrawCrypto(
            Principal principal, Model model,
            @ModelAttribute("createdNft") CryptoCurrencies createdCrypto
//            @RequestParam("type") String type,
//            @RequestParam("amount") float amount
    ) {
        Users user = userRepo.findByEmail(principal.getName());
        model.addAttribute("createdCrypto", new CryptoCurrencies());
        ResponseEntity<?> created =  cryptoCurrencyService.withdrawCrypto(createdCrypto.getCurrencyType(), createdCrypto.getBalance(), user);
        return new RedirectView("/wallet");
    }

//    @PostMapping(value = "/withdraw")
//    public ResponseEntity<?> withdrawCrypto(
//            Principal principal,
//            @RequestParam("type") String type,
//            @RequestParam("amount") float amount
//    ) {
//        Users user = userRepo.findByEmail(principal.getName());
//        return cryptoCurrencyService.withdrawCrypto(type, amount, user);
//    }
}
