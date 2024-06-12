package com.example.nibsstransfer.repository;

import com.example.nibsstransfer.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Peace Obute
 * @since 09/06/2024
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findByAccountNumberAndAccountName(String accountNumber, String accountName);

}

