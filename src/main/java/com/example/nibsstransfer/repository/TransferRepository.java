package com.example.nibsstransfer.repository;

import com.example.nibsstransfer.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */

@Repository
public interface TransferRepository extends JpaRepository<TransactionEntity, Long>{

    TransactionEntity findAccountByTransactionReference(String transactionReference);

    List<TransactionEntity> findByGmtCreatedBetween(Date startOfDay, Date endOfDay);

    List<TransactionEntity> findByStatusAndSenderAccountNumberAndGmtCreatedBetween(String status, String senderAccountNumber, Date startDate, Date endDate);
    
    List<TransactionEntity> findByStatusAndSenderAccountNumber(String status ,String senderAccountNumber);

    List<TransactionEntity> findByStatus(String status);

    List<TransactionEntity> findBySenderAccountNumber(String senderAccountNumber);
}
