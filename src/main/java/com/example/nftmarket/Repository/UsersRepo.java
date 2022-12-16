package com.example.nftmarket.Repository;
import com.example.nftmarket.Entity.Users;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends CrudRepository<Users, Integer> {
	@Query("SELECT u FROM Users u WHERE u.username = ?1")
	public Users findByEmail(String username);
	
    @Query("SELECT u FROM Users u WHERE u.verificationCode = ?1")
    public Users findByVerificationCode(String code);

    @Query("SELECT u FROM Users u where u.nickname = ?1")
    public Users findByNickName(String nickname);
    
    @Query("SELECT u FROM Users u WHERE u.username = :username")
    public Users getUserByUsername(@Param("username") String username);    
}
