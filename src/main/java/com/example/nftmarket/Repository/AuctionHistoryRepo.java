package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.AuctionHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionHistoryRepo extends CrudRepository<AuctionHistory, Integer> {
}
