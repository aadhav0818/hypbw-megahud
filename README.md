
# hypbw-megahud 

## About

- Made using the 1.8.9 Minecraft Forge MDK in Gradle 2.7 and Java 8.
- Provides users on the Hypixel Network (IP: mc.hypixel.net) with an advanced display featuring player FKDRs and levels.
- Does not impact frame rates due to API calls and runs all requests asynchronously. 
- Requires a developer key to access player statistics on the server.

## Data Storage and Caching
- All user JSON files that are fetched are cached as `playerData` objects for the entirety of application's session to reduce the amount of API calls that are made.

```java
private static ArrayList<playerData> playerDataCache = new ArrayList<playerData>();
```
```java
public playerData(String name, int stars, String rank, boolean isNicked, double FKDR) { }
```

## Hypixel API
- Your API key can be entered into the `apiAuth.java` file as shown below:

```java
public class apiAuth {
    private final String hypixelAPIKey = "<INSERT API KEY HERE>";
    public exampleApiAuth() {}
    public String getKey() {
        return hypixelAPIKey;
    }
}
```
- HTTP GET requests to the Hypixel API should be made as shown below:
`https://api.hypixel.net/player?key="insert key"&name="insert name"`
>

## Dependencies
- Google's GSON library is used to parse player JSON files after an HTTP request.
- You can add the following to your `build.gradle` file and import the necessary libraries.
```groovy
dependencies {
    compile 'com.google.code.gson:gson:2.7'
}
```

## Useful Information
- 1.8.9 forge modifications are made in Java 8 and Gradle 2.7, which unfortunately means that various IDE's are incompatible with these softwares. You may need to use a downgraded version of your IDE (compatible IntelliJ version is provided below) to get things working.
- Documentation is rather sparse for this version of the game, so feel free to use this repository as a guide for any 1.8.9 Forge HUDs you wish to implement.


## Links 
- [Forge-Gradle 1.8.9 MDK Download](https://adfoc.us/serve/sitelinks/?id=271228&url=https://maven.minecraftforge.net/net/minecraftforge/forge/1.8.9-11.15.1.2318-1.8.9/forge-1.8.9-11.15.1.2318-1.8.9-mdk.zip)
- [Hypixel Developer Portal](https://developer.hypixel.net/)
- [Player JSON Format and HTTP Response Codes](https://api.hypixel.net/)
- [IntelliJ 2021.1 Community Edition](https://www.jetbrains.com/idea/download/)
- [Free JDK Installations](https://github.com/hmsjy2017/get-jdk)
