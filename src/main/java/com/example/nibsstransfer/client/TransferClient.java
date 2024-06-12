package com.example.nibsstransfer.client;

import com.example.nibsstransfer.converter.TransferConvert;
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

import static com.example.nibsstransfer.constants.NibssConstants.*;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Slf4j
@Service
public class TransferClient {
    /*
      This is a demo mock api call to send the amount to the beneficiary bank.
    */
    public HttpResponse<String> sendTransactionToBeneficiaryBank(TransactionEntity transaction) throws URISyntaxException {

        String clientReq = TransferConvert.beneficiaryClientRequestMapper(transaction);
        HttpClient httpClient = HttpClient.newHttpClient();

        //I built a mock server in postman to demo the process of sending to the beneficiary bank.
        try {
            // Create an HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(DEMO_POSTMAN_MOCK_SERVER_URL))
                    .timeout(Duration.ofMinutes(1))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
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
}
