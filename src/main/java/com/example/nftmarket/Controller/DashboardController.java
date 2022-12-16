package com.example.nftmarket.Controller;

import com.example.nftmarket.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping(value = "/dashboard")
    public ResponseEntity<?> getDashboard() {
        ResponseEntity<?> data = dashboardService.getDashboard();
        System.out.println(data);
        return data;
    }
}