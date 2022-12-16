package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.NFTCryptoTransactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFTCryptoTransactionsRepo extends CrudRepository<NFTCryptoTransactions, Integer> {
}
