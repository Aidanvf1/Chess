# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.


```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Design
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZT9qBACu2AMTYPlDY3DD+cNJwcACiACwwAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAERdlGjAALYogxWDMIMANHO46gDu0BzTswtzKOPASAibcwC+mMLlMMWs7FyUVUMjUGOTR9uDy6prUBszc4uDu32h1+g1OOigKGAAGsYABZNKqJAOGCPZ4oRYfL4cRbQGCAg6YNicbiwApXc53GAAIWAHGSAEcfGowDEAB4qbAEbJnMqUS5XPLmKpxJxOPpDSbqYD2KZVQYxKDeSowPQcGAQxnM0FmTiEm4ky4lc6iKoQ1LpSgACmS5rAlAZTPSAEoeSIVIaZNolCp1FUZWAAKrdS2oiYoF2yeTetSqD3GKoAMSRaqDlEjwBVlhR3TRmHBkJh6b0BjxiugmHT0fU-KNZRNnqjyhjbI5XJyxvdZJKRNuysrTfULZQnKyOR7Bq7pWolIArKLXRdJ4KMFU52KBoNJappbK5gqlVVLRw1CAoMQ2zAIAAzUtKp3a9AcTBoCB2y8XClQeb9n2qKoKNAsx-GMYBAYAEAQGAkRgdgs0fbAICRZgwLQGAwAAC28FYYL6YDB3ZYc20WFZ0MIdC0PQwxuguCEwGCNBY30GCYDXPMIWhGBU1gFYkAw7NRjDGCEHYjg4NZNIwA0PDY0nDsUCqLj0wXUQayncp-2EyFRKaaF0CHEduU-VTlzAYUnAAZnFTcYx3aY9zLZVwJErN5ChdAH11aTVLkqo0B8CDlM7EppKqU9ITtLiQ26dMIy9AcZJKeMYAUDgU2i7RAsMK4QtA9i7QUHwMMtYBCvQmKKzi384x0f9UuS0qlLk1Tx0pG0JPtNR-KwFrSXJXllQeHMw1mOU-jmEqMKaCA3LQEa5hOBc+SXZAhRgAAmUUrNDF4YFGt4JvQqaZrmrZTkfTxvD8fxoHYGVwgTWIogUGAABkIFSHITI9T8qjqRpWg6Ax1FHLahpeEF-kxdZXlOIyrh6+5hjB2Utkh-RPmhkEwXYmF4WB5FtsMXEoe+PViSWvrp2VGk6RQTV0n0ttFt6koTLM9cJRsmU7PlByqlVdU6cdMAPKfHrvrrFQqgQd6kUtN6PodZkXSa7LKpjP0UEDYNCdixsqquJKk04Tj0vkTN+KeMM2ILU2+RfZgr18TgcVgEm1Q4CA1DQAByZg9jAEB0Iq-WQNkyX5JgPyII6O2oEaiPvP6hSxlK6AkAALxQDhGdHZnjJWlcYBFABGKyt1suV92gKofFTjD06zjYzs89Xq3Dt1I8UjLVeCtu-1y8KUAKoqDvKrzDZq5K6pHsqe8TycEde2W0GSVQurJ3sk6pxHCZOsbBgOo73N2+btThgVC9M9bNo3PfT9R8bSuP2aH4W87MC8XwAi8FB0HCZA9h0JgF-v-BWvgsBfUpupGo0gYgvRiE0GIbR2hA0RNkPoB1G6ZGyFUAAPEfaa6Aij53hvqSkhCZqbwnLWTu0t3oQPlgwwqStnSZQ9Dlf0s9irPyIWgPWih4rVUTMmeqGF0wW0oegG2HEpGoQdjAJ2dcnwT2gfWaOCB2EdxgQGeu6FG7Z1zoZfqBd8jX1LuXLmu5eYHhRHogxzcdQqP7tvKg9ZZ4J07hw-uVRjzcHyqVHhk0+ECKrAlIwU9kjjAgDQMRc95BaLIeTZU4CWGdQQN1chLM1KUn6LDExy0zFVA2hzFuT5P6XQCBCNUgQkQwhehJGAABxMMGgoG0JgdUJpiCUH2DDJgtOZ4M44LQPguRJC4bdiyVUOR1ClodPrMgdILTJTywkistQrCwAqwXn3UOvoYBcMCXI0JQjJ4iJNh47QkjeFUPzLI25-8FFKNbvsmSaipZR38po1W0DKS6KwUMwxBEDLtgKZfIpxcnBlw3BXbmVc+Z2MBZnbOosQ6CINh8yOVyEm9wbBijWMAllgA2aoS0pyDaJSnsbNU4FIIbKUqohZny4AQAgigcAo48EMu0BMheSTexVAacs1pa8N7i20bkwYfTJR2WqEMGVKAACS0g7IlzWuZOI-wSK8RQFxNEmw3g6AQKAKE+rhozDeIqgAchawYoIWj5KpqY1aJSrKKtUHKlw3rHVOIut-fwcRzIAA41oAE4QAAKQEAsAxqmTwDyoYDZuQr4Sx3jUBozRen9PsUMkZYzHloCKO6sMtrwanVIVM5JMzC2LAVaWu1C17m4wREiRwNrrYSr+dTWkWyjFgudYU1aIoObWSlPC+ytiBYamFmiiVzLI5hTkCgDZlo4CJo2VsnZXi1ZvM1mAbhJz0VhOETAGlcSJE6CzLM5tMFC1R1fIo52zi3muPUd8xJHT-m5pRTnEFTML6syvmZGFnNx3WOrsqOuyKm5oqZWpdxDV547r2QSg5S67Sro7ZMClYcqW1TVDy82lo0AoGwnASi6gNmLA0TAUIwBLCUFULh9uWK-RhhVZ+641aE1D03ekzJyTXFSsVSqtVGq4hOsXBC11t962TDE1UdVmqyn+quoxiCEBsL+EAcAjTMtsIAClEKoWTe0nJyo6gBgBu0RVAyG55rbAW4JM1i0bno4xqArKZZQE2AAdRYEqpB7QqQvQUHAAA0lajjqqlMSak-M7jgq70ufQHW94JrPPeegP5wLwXQvhai-8UTsWYDKckzIlt+N20xcvLAJEVBwJIDVB5yg2WoBzOyT9akvahbMn7aQmTRcR2WPAzzSD-NaSC3piLVT86EOfIAFYmdXcZpE-GZvbrcUFfFYT92HsLSx8JRtRE4ozFelLh0+GVcuy-B9jtn3HrOWxr5AVflfuVACwZv6BuAfgMBqFoGx3bgnTYmuSLvuwdU-BnyF7rmWlSDQWMustGob24crWWGYtHdPeeoj52swlZuxsowXtVC+2YCgcS6QnuYoXb5D972LNVBpn2-9ec-tsyhaOuFEHEXTr6+kODLjtH1nxzdrZ2dQJstxE5LSWZ0JMQzK1jrTPusKE0rSSwOkZq-fBUByFIpLKwqseNxFcutdoV0rNVTeKcp+C0JhsMCOYuLAd8ujg+OcfnM4tgR3SawwSJ4nxQmMFYwq640vNbaB+PrwyZ14TA0EvZK526gYqmKkBq8AxqNMbs9ZkQJCWAwBsChEICMlNZi02dLgQgpBKDjCVqSySQ8L4VQQFEi6ebsOQDcDwOS1Hu34qhT71ALiZLvf4aSLsGJhg6Vx08dtrKaPh+gVH7PCftO8MRKqFE2fQlIJncHzlXvRfSUD4nlPvfsT5-i6Z0vQveAxXx67R9+4yeXVFzT3kpxQA
