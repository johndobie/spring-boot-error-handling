# spring-boot-error-handling
This is the code for the following blog.

https://johndobie.com/blog/errors-in-spring-boot-microservices

# Running the Example
Checkout the code, build and run the application.
```bash
git clone https://github.com/johndobie/spring-boot-error-handling.git
mvn clean install spring-boot:run
```

Test The Application.
```bash 
$ curl -X POST http://localhost:8080/echo/model -H 'Content-Type: application/json' -d '{"name":null,"message":"This message is more than 30 characters long ..................................."}'

{
  "type": "VALIDATION",
  "errors": [
    {
      "code": "Size",
      "detail": "message size must be between 1 and 30",
      "source": "echoModel/message"
    },
    {
      "code": "NotNull",
      "detail": "name may not be null",
      "source": "echoModel/name"
    }
  ]
}
```