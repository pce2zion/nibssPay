package com.example.nibsstransfer.repository;

import com.example.nibsstransfer.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findByAccountNumberAndAccountName(String accountNumber, String accountName);

}

