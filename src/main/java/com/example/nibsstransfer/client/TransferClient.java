package com.example.nibsstransfer.client;

import com.alibaba.fastjson.JSON;
import com.example.nibsstransfer.client.req.TransferClientRequest;
import com.example.nibsstransfer.entity.TransactionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
public class TransferClient {
    /*
      This is a demo mock api call to send the amount to the beneficiary bank.
    */
    public HttpResponse<String> sendTransactionToBeneficiaryBank(TransactionEntity transaction) throws URISyntaxException {

        String clientReq = beneficiaryRequestMapper(transaction);
        HttpClient httpClient = HttpClient.newHttpClient();

        //I built a mock url from post man to process the transaction and send to the beneficiary bank.
        try {
            // Create an HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://ea388149-c762-42f2-94be-7ea764de8b7e.mock.pstmn.io/doTransfer"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(clientReq))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the response status code
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to send transaction: " + response.body());
            }
           log.info("Sending to beneficiary bank {}", response);
            return response;
        }catch(IOException | InterruptedException e){
            log.error("Exception occurred during client call ", e);
            throw new RuntimeException("Exception occurred during client call", e);
        }
    }

    private static String beneficiaryRequestMapper(TransactionEntity transaction) {

        TransferClientRequest req = new TransferClientRequest();
        req.setTransactionId(transaction.getTransactionReference());
        req.setAmount(transaction.getAmountToSend());
        req.setBeneficiaryAccountName(transaction.getBeneficiaryAccountName());
        req.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
        req.setSenderAccountName(transaction.getSenderAccountName());
        req.setSenderAccountNumber(transaction.getSenderAccountNumber());

        return JSON.toJSONString(req);
    }

}
