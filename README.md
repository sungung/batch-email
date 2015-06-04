# batch-email

Sending report file by email in batch mode. One of use case will be sending invoice by email while iterating customer table.

## Code Example

See unit tests

## Motivation

Batch email report is as usual business practice. Scheduler will trigger report job then job will distribute the report file.

## Installation
```
git clone https://github.com/sungung/batch-email.git
mvn clean package
```
## Tests

Default smtp host is localhost, you can change the server from applicaiton.properties
```
mail.sender=report@sungung.com
spring.mail.host=localhost
spring.mail.port=25
```
I found [Fake SMTP server](https://nilhcem.github.io/FakeSMTP/) is useful for unit/integration testing