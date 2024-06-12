Nibss Pay.
NibssPay is a simulation of the Nibbs interface. This simulation demonstrates how transactions are received, processed and sent to the beneficiary Bank. For the purpose of this simulation, 
transactions received into nibss system are assumed to be automatically decrypted.

//doTransfer (POST)
The doTransfer endpoint accepts payment from the sender institution via the controller, and processes it, it first checks if the sender details are valid. This is a simulation of an api call to validate
the sender account. I mocked account details into an account database and called it from there. 

SENDER ACCOUNT DETAILS TO USE WHILE TESTING:
Account name : John Doe
Account number : ACC12345
Account balance : N197437.25

Account name : Dave Black
Account number : ACC12350
Account balance : N100.00 this is to test for insufficient funds

Account name : Frank Blue
Account number : ACC12352
Account balance : N250000.00 

you can also test for invalid account numbers

Once the sender account details aprovided are valid, the transaction is saved into the database to be processed with a status of PENDING. If the balance is less than the amount to send, 
the transaction fails and INSUFFICIENT FUNDS is returned as the status, else, the transaction fee is calculated, the transaction is encrypted using RSA encryption and send to the beneficiary institution. Here, i built a mock server to accept all transaction requests. This returns a response if successful.

If a successful response is received, it means that the beneficiary received the transaction. Then the transaction fee is removed from the sender account alongside the amount. the status
of the transaction is changed to SUCCESS, and the flag isTransactionProcessed is set to true, if not it is FAILED and false respectively. Responses at every stage of the transaction are
sent to the controller.

A schedule job runs in the background every 12am, finds successful transactions and removes the comission from the transaction fee, updates the comissionWorthiness, updates the new 
transaction and saves it back in the transaction table.

//getTransactions (GET)
This provides details of transactions with optional parameters.
so the repository can find transaction by all parameters; status, accountid(in my case senderAccountNumber), startDate and endDate
or by only the status
or by only accountId
or by status and accountId
or by startDate and enddate.
it provides a list of transactions

//getDailySummary (GET)
This endpoint runs with the help of a scheduler ro provide the summary of all transactions in a day. the scheduler runs every day at 12am and gets the daily summary transaction list of the
previous day. 
this is also returned as a list

I attached a copy of the postman collections, and the postman mock server collections to the code and also sent it via mail. All endpoints of the code implementation can be tested as it runs successfully locally. 





