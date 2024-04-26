package com.retail.ecom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retail.ecom.entity.User;

public interface UserRespository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);


	Optional<User>  findByUserName(String username) ;

}
