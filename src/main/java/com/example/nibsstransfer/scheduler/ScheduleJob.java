package com.example.nibsstransfer.scheduler;

import com.example.nibsstransfer.entity.TransactionEntity;
import com.example.nibsstransfer.repository.TransferRepository;
import com.example.nibsstransfer.service.TransferService;
import com.example.nibsstransfer.util.NibssUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.example.nibsstransfer.constants.NibssConstants.COMMISSION_PERCENTAGE;

@Component
public class ScheduleJob {

    private final TransferService transferService;

    private final TransferRepository transferRepository;


    public ScheduleJob(TransferService transferService, TransferRepository transferRepository) {
        this.transferService = transferService;
        this.transferRepository = transferRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") //runs daily at 12:00 AM
    public void processCommissions(){
        List<TransactionEntity> transactionsList = transferService.getAllSuccessfulTransactions("SUCCESS");

        for(TransactionEntity transactionEntity : transactionsList){
            if(transactionEntity.getIsCommissionWorthy().equals(Boolean.TRUE)){
                transactionEntity.setCommission(COMMISSION_PERCENTAGE.multiply(transactionEntity.getTransactionFee()));

                transferRepository.save(transactionEntity);
            }
        }
    }


    @Scheduled(cron = "0 0 2 * * ?") //runs daily at 2:00 AM
    public void getDailySummary(){
        Date today = NibssUtils.getStartOfDay(new Date());
          transferService.getDailySummary(today);
    }
}
