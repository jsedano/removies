# Removies

Removies is an api that lets you search movies and tv shows information from a database of movies from several streaming services.

![Screen Shot 2022-08-29 at 1 02 21](https://user-images.githubusercontent.com/4958726/187133254-d462f8c6-06f8-457a-b475-98108ee4ba85.png)

## How it works

### How the data is stored:

The data is stored as JSON documents using [RedisJSON](https://redis.io/docs/stack/json/)

```java
public void insert(String key, MediaDTO mediaDTO) {
  jedisPooled.jsonSet(key, gson.toJson(mediaDTO));
}
```

`MediaDTO` is the object to be inserted but first it needs to be represented as JSON so we use `gson` for that.

### How the data is accessed:

To access data several [indexes need to be created](https://redis.io/commands/ft.create/).
Then [RediSearch](https://redis.io/docs/stack/search/) is used to make queries.


```java
jedisPooled.ftSearch(
    "titleIdx", new Query("@title:(" + cleanTitle + ")").returnFields("title"));
}
```


## How to run it locally?


### Prerequisites

- Have an instance of [redis-stack](khttps://redis.io/docs/stack/) running.
- Apache Maven 3.8.6
- Java 17

### Local installation

- Download the following data sets:
   - [Disney+ Movies and TV Shows](https://www.kaggle.com/datasets/shivamb/disney-movies-and-tv-shows)
   - [Netflix Movies and TV Shows](https://www.kaggle.com/datasets/shivamb/netflix-shows)
   - [Amazon Prime Movies and TV Shows](https://www.kaggle.com/datasets/shivamb/amazon-prime-movies-and-tv-shows)
   - [Hulu Movies and TV Shows](https://www.kaggle.com/datasets/shivamb/hulu-movies-and-tv-shows)
- clone this repository and copy the datasets on src/main/resources
- create the following indexes:
```
FT.CREATE genreIdx ON JSON PREFIX 1 media: SCHEMA $.genre.* AS genres TAG
FT.CREATE titleIdx ON JSON PREFIX 1 media: SCHEMA $.title AS title TEXT
FT.CREATE providerIdx ON JSON PREFIX 1 media: SCHEMA $.provider.* AS providers TAG
FT.CREATE castIdx ON JSON PREFIX 1 media: SCHEMA $.cast.* AS cast TAG
```
- run it with `mvn spring-boot:run` the database will fill up on startup.

## More Information about Redis Stack

Here some resources to help you quickly get started using Redis Stack. If you still have questions, feel free to ask them in the [Redis Discord](https://discord.gg/redis) or on [Twitter](https://twitter.com/redisinc).

### Getting Started

1. Sign up for a [free Redis Cloud account using this link](https://redis.info/try-free-dev-to) and use the [Redis Stack database in the cloud](https://developer.redis.com/create/rediscloud).
1. Based on the language/framework you want to use, you will find the following client libraries:
    - [Redis OM .NET (C#)](https://github.com/redis/redis-om-dotnet)
        - Watch this [getting started video](https://www.youtube.com/watch?v=ZHPXKrJCYNA)
        - Follow this [getting started guide](https://redis.io/docs/stack/get-started/tutorials/stack-dotnet/)
    - [Redis OM Node (JS)](https://github.com/redis/redis-om-node)
        - Watch this [getting started video](https://www.youtube.com/watch?v=KUfufrwpBkM)
        - Follow this [getting started guide](https://redis.io/docs/stack/get-started/tutorials/stack-node/)
    - [Redis OM Python](https://github.com/redis/redis-om-python)
        - Watch this [getting started video](https://www.youtube.com/watch?v=PPT1FElAS84)
        - Follow this [getting started guide](https://redis.io/docs/stack/get-started/tutorials/stack-python/)
    - [Redis OM Spring (Java)](https://github.com/redis/redis-om-spring)
        - Watch this [getting started video](https://www.youtube.com/watch?v=YhQX8pHy3hk)
        - Follow this [getting started guide](https://redis.io/docs/stack/get-started/tutorials/stack-spring/)

The above videos and guides should be enough to get you started in your desired language/framework. From there you can expand and develop your app. Use the resources below to help guide you further:

1. [Developer Hub](https://redis.info/devhub) - The main developer page for Redis, where you can find information on building using Redis with sample projects, guides, and tutorials.
1. [Redis Stack getting started page](https://redis.io/docs/stack/) - Lists all the Redis Stack features. From there you can find relevant docs and tutorials for all the capabilities of Redis Stack.
1. [Redis Rediscover](https://redis.com/rediscover/) - Provides use-cases for Redis as well as real-world examples and educational material
1. [RedisInsight - Desktop GUI tool](https://redis.info/redisinsight) - Use this to connect to Redis to visually see the data. It also has a CLI inside it that lets you send Redis CLI commands. It also has a profiler so you can see commands that are run on your Redis instance in real-time
1. Youtube Videos
    - [Official Redis Youtube channel](https://redis.info/youtube)
    - [Redis Stack videos](https://www.youtube.com/watch?v=LaiQFZ5bXaM&list=PL83Wfqi-zYZFIQyTMUU6X7rPW2kVV-Ppb) - Help you get started modeling data, using Redis OM, and exploring Redis Stack
    - [Redis Stack Real-Time Stock App](https://www.youtube.com/watch?v=mUNFvyrsl8Q) from Ahmad Bazzi
    - [Build a Fullstack Next.js app](https://www.youtube.com/watch?v=DOIWQddRD5M) with Fireship.io
    - [Microservices with Redis Course](https://www.youtube.com/watch?v=Cy9fAvsXGZA) by Scalable Scripts on freeCodeCamp
