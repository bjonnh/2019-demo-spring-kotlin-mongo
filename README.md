# A demo of mongoDB in Kotlin using Spring Boot
Author: Jonathan Bisson

It is a really simple app based on <https://spring.io/guides/gs/accessing-mongodb-data-rest/#scratch>.
It is really dirty and just meant to show how easy it is to use Spring Boot in [Kotlin](https://www.kotlinlang.org) to talk to Mongo. 

## Building
To build it (not needed if you just want to run the stack as I published the image)
```shell
$ ./gradlew build docker
```

This will produce *./build/libs/2019-demo-spring-mongo-{VERSION}.jar*
## Running the demo stack
Make sure you have docker and docker-compose. If you don't have them, follow directions from your distribution, 
OS vendor or the [Official Docker website](https://www.docker.io).

Technically, you only need the file stack.yml if you don't want to work on the code itself or rebuild it.

If you need, the stack expose a mongo-express instance on <http://localhost:8081>

and run:
```shell
$ docker-compose -f stack.yml .
```

## Testing the stack
You can then access the API:
```shell
$ curl -i --header "Accept:application/json" -X GET http://localhost:8080
```

And access public spectrum:
```shell
$ curl -i --header "Accept:application/json" -X GET http://localhost:8080/spectra/AS2
HTTP/1.1 200…
```

But you cannot access a private spectrum:
```shell
$ curl -i --header "Accept:application/json" -X GET http://localhost:8080/spectra/AS1
HTTP/1.1 404…
```

You now get the cookie for the authorization

```shell 
$ curl -i -X POST -d username=admin -d password=admin -c cookie.txt http://localhost:8080/login
```

Now use your cookie file, so you can access the people endpoint

```shell
$ curl -i --header "Accept:application/json" -X GET -b ./cookie.txt http://localhost:8080/people/
HTTP/1.1 200…
```

Now use your cookie file, so you can access your private spectrum

```shell
$ curl -i --header "Accept:application/json" -X GET -b ./cookie.txt http://localhost:8080/spectra/AS1
HTTP/1.1 200…
```



