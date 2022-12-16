package com.example.nftmarket.Repository;

import com.example.nftmarket.Entity.NFT;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFTRepo extends CrudRepository<NFT, Integer> {
}
