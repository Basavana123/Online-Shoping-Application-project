package com.retail.ecom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retail.ecom.entity.AccessToken;

public interface AccessRepository extends JpaRepository<AccessToken, Integer> {

	boolean existsByTokenAndIsBlocked(String at, boolean b);


    Optional<AccessToken> findByToken(String accessToken);

}
