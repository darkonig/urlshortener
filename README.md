# Assignment: URL Shortener

## Description

Most of us are familiar with seeing URLs like bit.ly or t.co on our Twitter or Facebook feeds. 
These are examples of shortened URLs, which are a short alias or pointer to a longer page link. 
For example, I can send you the shortened URL http://bit.ly/SaaYw5 that will forward you to a 
very long Google URL with search results on how to iron a shirt.

## Mandatory Requirements

- Design and implement an API for short URL creation
- Implement forwarding of short URLs to the original ones
- There should be some form of persistent storage
- The application should be distributed as one or more Docker images

## Additional Requirements

- Design and implement an API for gathering different statistics

## Assessment

Treat this as a real project. It should be readable, maintainable, and extensible where appropriate.

The implementation should preferably be in Java, however any language can be used.

If you will transfer it to another team - it should be clear how to work with it and what is going on.

You should send us a link to a Git repository that we will be able to clone.

--- 
# Solution

The solution architecture is the following:

[Application Architecture](/ApplicationArchitecture.png)

You can find two projects:
- **urlshortener**: the core part of the project, ![read more](/urlshortener/).
- **urlshortener-status**: the statistics project, ![read more](/urlshortener-status/).

This will be the target architecture:

[Application Architecture - Complete](/ApplicationArchitectureComplete.png)

## Running the project

    The project was built using docker, so to run this solution docker and docker-compose are needed.

First start building the projects:

```cmd
./build.sh
```

After the build, just start the dockers:

```cmd
./start.sh
```

Stoppign the services:
```cmd
./stop.sh
```

## Endpoints

Registering a url:

`POST` **http://localhost:8082/**

Payload: 
```json
{"url": "https://google.com"}
```

Response:
```json
{
    "hash": "http://localhost:8082/6aa160b4",
    "origin": "https://google.com",
    "created": "2019-01-20T05:11:09.68"
}
```

Redirect to the target url:

`GET` **http://localhost:8082/{hash}/**

Ex.: http://localhost:8082/6aa160b4


Getting the data of the short url:

`GET` **http://localhost:8083/api/v1/url/{hash}/**

Ex.: http://localhost:8083/api/v1/url/6aa160b4/

Response:
```json
{
    "hash": "9abcc4f3",
    "origin": "https://github.com/docker-library/redis/issues/45",
    "created": "2019-01-21T04:32:31.72"
}
```

Getting the last 10 events from a short url:

`GET` **http://localhost:8083/api/v1/url/{hash}/events/**

Ex.: http://localhost:8083/api/v1/url/9abcc4f3/events/

Response:
```json
[{
"id": "5c455422daa4d40001441e17",
"entity": {
"hash": "9abcc4f3",
"origin": "https://github.com/docker-library/redis/issues/45",
"created": "2019-01-21T04:32:31.72"
},
"ip": "172.28.0.1",
"status": "CREATED",
"date": "2019-01-21T05:09:54.667",
"browser": "Unknown",
"browserVersion": "",
"os": "Unknown"
}, {
"id": "5c455419daa4d40001441e16",
"entity": {
"hash": "9abcc4f3",
"origin": "https://github.com/docker-library/redis/issues/45",
"created": "2019-01-21T04:32:31.72"
},
"ip": "172.28.0.1",
"status": "CREATED",
"date": "2019-01-21T05:09:45.162",
"browser": "Unknown",
"browserVersion": "",
"os": "Unknown"
}, {
"id": "5c45537fdaa4d40001441e15",
"entity": {
"hash": "9abcc4f3",
"origin": "https://github.com/docker-library/redis/issues/45",
"created": "2019-01-21T04:32:31.72"
},
"ip": "172.28.0.1",
"status": "ACCESS",
"date": "2019-01-21T05:07:10.734",
"browser": "Unknown",
"browserVersion": "",
"os": "Unknown"
}, {
"id": "5c454b7adaa4d40001441e11",
"entity": {
"hash": "9abcc4f3",
"origin": "https://github.com/docker-library/redis/issues/45",
"created": "2019-01-21T04:32:31.72"
},
"ip": "172.28.0.1",
"status": "CREATED",
"date": "2019-01-21T04:32:58.684",
"browser": "Unknown",
"browserVersion": "",
"os": "Unknown"
}, {
"id": "5c454b61daa4d40001441e0f",
"entity": {
"hash": "9abcc4f3",
"origin": "https://github.com/docker-library/redis/issues/45",
"created": "2019-01-21T04:32:31.72"
},
"ip": "172.28.0.1",
"status": "ACCESS",
"date": "2019-01-21T04:32:31.72",
"browser": "Unknown",
"browserVersion": "",
"os": "Unknown"
}]
```

Getting access status:

`GET` **http://localhost:8083/api/v1/url/{hash}/status/**

Ex.: http://localhost:8083/api/v1/url/9abcc4f3/status/

Response:
```json
{
"totalAccess": 5,
"topBrowsers": [
    "Not implemented."
    ]
}
```
