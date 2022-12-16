package com.example.nftmarket.Controller;

import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Repository.UsersRepo;
import com.example.nftmarket.Service.PersonalTransactionsService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PersonalTransactionsController {

    @Autowired
    private PersonalTransactionsService personalTransactionsService;
    @Autowired
    private UsersRepo userRepo;

    @GetMapping(value = "/myTransactions")
    public ResponseEntity<?> getMyTransactions(Principal principal) throws JSONException {
        //Users user = userRepo.findByEmail(principal.getName());
        Users user = userRepo.findById(1).get();
        return personalTransactionsService.getMyTransactions(user);
    }
}
