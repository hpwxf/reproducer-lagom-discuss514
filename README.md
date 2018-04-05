# Reproducer for [Missing Set-Cookie header for WebSocket connection discussion](https://discuss.lightbend.com/t/missing-set-cookie-header-for-websocket-connection/514)

``Set-Cookie`` header is filtered out when application is started using Lagom components.

## With Lagom components
Run with
```shell
LAGOM_MODE=true sbt runAll
```

Test with GET request
```shell
curl -v -H "Cookie:COOKIE=NAME" -H "Set-Cookie:SETCOOKIE=NAME" -H "X-Auth-Token: token" -H "MyHeader: dummy" http://localhost:9000/get
```
produces
```
> GET /get HTTP/1.1
> Host: localhost:9000
> User-Agent: curl/7.54.0
> Accept: */*
> Cookie:COOKIE=NAME
> Set-Cookie:SETCOOKIE=NAME
> X-Auth-Token: token
> MyHeader: dummy
> 
< HTTP/1.1 200 OK
< Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
< X-Frame-Options: DENY
< X-XSS-Protection: 1; mode=block
< X-Content-Type-Options: nosniff
< Content-Security-Policy: connect-src 'self' ws://localhost:9000 ws://192.168.1.30:9000
< X-Permitted-Cross-Domain-Policies: master-only
< Date: Wed, 04 Apr 2018 20:43:33 GMT
< Server: akka-http/10.0.11
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 349
< 
I received GET request with cookie: None, auth token: Some(token)
* Connection #0 to host localhost left intact
headers: List((Remote-Address,127.0.0.1:59219), (Raw-Request-URI,/get), (Tls-Session-Info,[Session-1, SSL_NULL_WITH_NULL_NULL]), (Host,localhost:9000), (User-Agent,curl/7.54.0), (Accept,*/*), (Cookie,COOKIE=NAME), (X-Auth-Token,token), (MyHeader,dummy), (Timeout-Access,<function1>))    
```

Test with Websocket request
```shell
wscat -H "Cookie:COOKIE=VALUE" -H "Set-Cookie:SETCOOKIE=VALUE" -H "X-Auth-Token: token" -H "MyHeader: dummy" -c ws://localhost:9000/ws
```
produces
```
>
< I received WS message:  with cookie: None, auth token: Some(token)
headers: List((Remote-Address,127.0.0.1:59630), (UpgradeToWebSocket,), (Raw-Request-URI,/ws), (Tls-Session-Info,[Session-1, SSL_NULL_WITH_NULL_NULL]), (Upgrade,websocket), (Connection,upgrade), (Sec-WebSocket-Key,S/KUF8S3yRrE4IWG2Afxzw==), (Sec-WebSocket-Version,13), (Cookie,COOKIE=VALUE), (X-Auth-Token,token), (MyHeader,dummy), (Host,localhost:9000), (User-Agent,akka-http/10.0.11), (Timeout-Access,<function1>))
```


## Without Lagom components
Run with
```shell
LAGOM_MODE=false sbt reproducer/run
```

Test with GET request
```shell
curl -v -H "Cookie:COOKIE=NAME" -H "Set-Cookie:SETCOOKIE=NAME" -H "X-Auth-Token: token" -H "MyHeader: dummy" http://localhost:9000/get
```
produces
```
> GET /get HTTP/1.1
> Host: localhost:9000
> User-Agent: curl/7.54.0
> Accept: */*
> Cookie:COOKIE=NAME
> Set-Cookie:SETCOOKIE=NAME
> X-Auth-Token: token
> MyHeader: dummy
> 
< HTTP/1.1 200 OK
< Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
< X-Frame-Options: DENY
< X-XSS-Protection: 1; mode=block
< X-Content-Type-Options: nosniff
< Content-Security-Policy: connect-src 'self' ws://localhost:9000 ws://192.168.1.30:9000
< X-Permitted-Cross-Domain-Policies: master-only
< Date: Wed, 04 Apr 2018 20:51:04 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 402
< 
I received GET request with cookie: Some(SETCOOKIE=NAME), auth token: Some(token)
* Connection #0 to host localhost left intact
headers: List((Remote-Address,0:0:0:0:0:0:0:1:60672), (Raw-Request-URI,/get), (Tls-Session-Info,[Session-1, SSL_NULL_WITH_NULL_NULL]), (Host,localhost:9000), (User-Agent,curl/7.54.0), (Accept,*/*), (Cookie,COOKIE=NAME), (Set-Cookie,SETCOOKIE=NAME), (X-Auth-Token,token), (MyHeader,dummy), (Timeout-Access,<function1>))
```

Test with Websocket request
```shell
wscat -H "Cookie:COOKIE=VALUE" -H "Set-Cookie:SETCOOKIE=VALUE" -H "X-Auth-Token: token" -H "MyHeader: dummy" -c ws://localhost:9000/ws
```
produces
```
>
< I received WS message:  with cookie: Some(SETCOOKIE=VALUE), auth token: Some(token)
headers: List((Remote-Address,127.0.0.1:60543), (UpgradeToWebSocket,), (Raw-Request-URI,/ws), (Tls-Session-Info,[Session-1, SSL_NULL_WITH_NULL_NULL]), (Sec-WebSocket-Version,13), (Sec-WebSocket-Key,Ke8E0A6u1dgb5nr8Gpd1uA==), (Connection,Upgrade), (Upgrade,websocket), (Cookie,COOKIE=VALUE), (Set-Cookie,SETCOOKIE=VALUE), (X-Auth-Token,token), (MyHeader,dummy), (Sec-WebSocket-Extensions,permessage-deflate; client_max_window_bits), (Host,localhost:9000), (Timeout-Access,<function1>))
```
