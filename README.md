# Nuthorizr

A Authorizer app with json in stdin

## Come on, do your tests

#### But what will you need?

- [docker](https://docs.docker.com/install/) ~ 18.04.0-ce
- [docker-compose](https://docs.docker.com/compose/) ~ 1.21.1

or

- [leiningen](https://leiningen.org/) ~ 2.9.5


#### Okay, how to put it to up?

First clone of the project
```bash
docker-compose build
```
#### After image build

now you can execute any program inside the server with command like it.
```bash
docker-compose run nuthorizr lein eastword
```

## How to do tests
```bash
docker-compose run nuthorizr lein test

or

lein test
```

## How to do exec
```bash
cat operations_demo
{"account": {"active-card": true, "available-limit": 2000}}
{"transaction": {"merchant": "Habbibs", "amount": 20, "time":"2020-01-01T01:01:00.000Z"}}
{"transaction": {"merchant": "Habbibs", "amount": 20, "time":"2020-01-01T01:02:00.000Z"}}
{"transaction": {"merchant": "Habbibs", "amount": 20, "time":"2020-01-01T01:04:00.000Z"}}
{"account": {"active-card": true, "available-limit": 2000}}
{"transaction": {"merchant": "kalunga", "amount": 20, "time":"2020-01-01T01:04:00.000Z"}}
{"transaction": {"merchant": "outback", "amount": 220, "time":"2020-01-01T01:04:00.000Z"}}
{"transaction": {"merchant": "mc", "amount": 3000, "time":"2020-01-01T01:04:00.000Z"}}


docker-compose run nuthorizr lein run < operations_demo
lein run < operations_demo
```

## You can buil
```bash
lein uberjar 
java -jar ./target/uberjar/nuthorizr-0.0.1-SNAPSHOT-standalone.jar < operations_demo 
```

### License  
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
