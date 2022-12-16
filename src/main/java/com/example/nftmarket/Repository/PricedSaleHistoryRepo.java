package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.PricedSaleHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricedSaleHistoryRepo extends CrudRepository<PricedSaleHistory, Integer> {
}
