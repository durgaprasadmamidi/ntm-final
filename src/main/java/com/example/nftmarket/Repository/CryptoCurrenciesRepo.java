package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.CryptoCurrencies;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoCurrenciesRepo extends CrudRepository<CryptoCurrencies, Integer> {
}
