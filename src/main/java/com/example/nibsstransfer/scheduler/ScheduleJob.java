package com.example.nibsstransfer.scheduler;

import com.example.nibsstransfer.constants.NibssConstants;
import com.example.nibsstransfer.entity.TransactionEntity;
import com.example.nibsstransfer.repository.TransferRepository;
import com.example.nibsstransfer.service.TransferService;
import com.example.nibsstransfer.util.NibssUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.example.nibsstransfer.constants.NibssConstants.COMMISSION_PERCENTAGE;
/**
 * @author Peace Obute
 * @since 06/06/2024
 */

@Component
public class ScheduleJob {

    private final TransferService transferService;

    private final TransferRepository transferRepository;


    public ScheduleJob(TransferService transferService, TransferRepository transferRepository) {
        this.transferService = transferService;
        this.transferRepository = transferRepository;
    }

    /**
     * cronjob that runs daily at 12:00 am to process txn commissions and subtract from transaction fee
     */
    @Scheduled(cron = "0 0 0 * * ?") //runs daily at 12:00 AM
    public void processCommissions(){
        List<TransactionEntity> transactionsList = transferRepository.findByStatus(NibssConstants.SUCCESS);

        if(transactionsList != null) {
            for (TransactionEntity transactionEntity : transactionsList) {
                transactionEntity.setIsCommissionWorthy(true);
                transactionEntity.setCommission(COMMISSION_PERCENTAGE.multiply(transactionEntity.getTransactionFee()));

                //here the commission is subtracted from the transaction fee and the transaction fee amount is updated.
                transactionEntity.setTransactionFee(transactionEntity.getTransactionFee().subtract(transactionEntity.getCommission()));
                transferRepository.save(transactionEntity);
            }
        }
    }

    /**
     * cronjob that runs daily at 12:00 am to process daily transaction fee
     */
    @Scheduled(cron = "0 0 0 * * ?") //runs daily at 12:00 AM
    public void getDailySummary(){
        Date today = NibssUtils.getStartOfDay(new Date());
          transferService.getDailySummary(today);
    }
}
