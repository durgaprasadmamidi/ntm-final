package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.CryptoTransactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoTransactionsRepo extends CrudRepository<CryptoTransactions, Integer> {
}
