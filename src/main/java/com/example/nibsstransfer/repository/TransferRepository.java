package com.example.nibsstransfer.repository;

import com.example.nibsstransfer.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<TransactionEntity, Long>{

    TransactionEntity findAccountByTransactionReference(String transactionReference);

    List<TransactionEntity> findByGmtCreatedBetween(Date startOfDay, Date endOfDay);

    List<TransactionEntity> findByStatusAndSenderAccountNumberAndGmtCreatedBetween(String status, String senderAccountNumber, Date startDate, Date endDate);

    List<TransactionEntity> findByStatusAndGmtCreatedBetween(String status, Date startDate, Date endDate);

    List<TransactionEntity> findBySenderAccountNumberAndGmtCreatedBetween(String senderAccountNumber, Date startDate, Date endDate);
}
