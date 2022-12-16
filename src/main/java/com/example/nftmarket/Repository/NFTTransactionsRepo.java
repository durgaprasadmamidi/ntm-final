package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.NFTTransactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFTTransactionsRepo extends CrudRepository<NFTTransactions, Integer> {
}
