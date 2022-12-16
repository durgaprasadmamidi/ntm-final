package com.example.nftmarket.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nftmarket.Entity.CryptoCurrencies;
import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Entity.Wallet;
import com.example.nftmarket.Repository.UsersRepo;
import com.example.nftmarket.Service.CryptoCurrencyService;
import com.example.nftmarket.UserDetails.UserServices;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class UserController {
	
	@Autowired
	private UsersRepo userRepo;

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    private UserServices service;
	
    @GetMapping(value = "/user")
    public String welcome() {
        return "Index";
    }
    
    @GetMapping(value = "/createNFT")
    public String createNFT() {
        return "createNFT";
    }
    
    @GetMapping(value = "/Buy")
    public String buy() {
        return "Buy";
    }
    
    
    @GetMapping(value = "/test")
    public String welcometest() {
        return "Landing";
    }
    
    @RequestMapping("/wallet")
    public String defectDetails(Principal principal,Model model) throws JSONException {
    	//display existing user balance
    	System.out.println(principal.getName());
        Users user = userRepo.findByEmail(principal.getName());
        System.out.println("Is there a user in wallet");
        System.out.println(user);
        ResponseEntity<?> crypto =  cryptoCurrencyService.getCryptosOfUser(user);
        List<CryptoCurrencies> cryptoList = (List<CryptoCurrencies>)crypto.getBody();
        System.out.println("Do i have a cryptolist");
        for(CryptoCurrencies cr: cryptoList) {
        	
        	if( cr.getCurrencyType() == "BTC")
        		model.addAttribute("btcBalance",cr.getBalance());
        	if( cr.getCurrencyType() == "ETH")
        		model.addAttribute("etcBalance",cr.getBalance());
   
        }
    	
//    	model.addAttribute("wallet", new Wallet());
    	model.addAttribute("createdCrypto", new CryptoCurrencies());
        return "wallet"; //defect-details.html page name to open it
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	System.out.println("The user on register page is:");
    	System.out.println(model);
        model.addAttribute("user", new Users());
         
        return "SignUp";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
    	System.out.println("The user on login page is:");
    	System.out.println(model);
        model.addAttribute("user", new Users()); 
        return "SignIn";
    } 
    
    @PostMapping("/process_register")
    public String processRegister(Users user, HttpServletRequest request, Model model)
            throws UnsupportedEncodingException, MessagingException {
    	System.out.println("Inside Process Register");
    	System.out.println(user);
    	System.out.println(request);
    	
    	//design a service to check if the user already exists
    	if(service.checkUserExists(user)) {
    		model.addAttribute("user",new Users());
    		model.addAttribute("error",true);
    		return "SignUp";
    	} else {
	        service.register(user, getSiteURL(request));
	        return "SignUpSuccess";
    	}
    }
     
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    } 
//    @PostMapping("/process_register")
//    public String processRegister(Users user) {
//    	System.out.println("The user after registration is:");
//    	System.out.println(user.getUsername());
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword = passwordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPassword);
//    	System.out.println(user.getPassword());
//    	System.out.println("What is the user repo?");
//    	System.out.println(userRepo);
//    	
//        userRepo.save(user);
//         
//        return "SignUpSuccess";
//    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<Users> listUsers = (List<Users>) userRepo.findAll();
        model.addAttribute("listUsers", listUsers);
         
        return "Homepage";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
    	System.out.println("---Verifying code---");
        if (service.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
    
}