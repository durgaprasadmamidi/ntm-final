package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends CrudRepository<Wallet, Integer> {
}
