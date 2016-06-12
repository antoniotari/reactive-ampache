<h3>Usage:</h3><br>
add the library to your project :
```java
compile project(':reactive-ampache')
```

<h3>init the library in your application class:</h3>
```java
        AmpacheApi.INSTANCE.initSession(this);
```

<h3>if the user has already logged in before, and not logged out:</h3>
```java
if (AmpacheSession.INSTANCE.isUserAuthenticated()) {
    AmpacheApi.INSTANCE.initUser()
        .subscribe(aVoid -> {
                // user initialized successfully
            }, throwable -> {
                // there was a problem initializing the user, check the message in throwable
            });
}
```

<h3>initialize a new user:</h3>
```java
String url = "yourAmpacheInstanceURL";
String username = "yourAmpacheUsername";
String password = "yourAmpacheUserPassword";

AmpacheApi.INSTANCE.initUser(url,username,password)
    .subscribe(aVoid -> {
        // this is a valid user, it's possible to initiate the handshake
    }, throwable -> {
        // not a valid user
});

```

<h3>after initializing a user, do the handshake to login:</h3>
```java
AmpacheApi.INSTANCE.handshake()
    .subscribe(handshakeResponse -> {
        // handshake successful, user logged in
    }, throwable -> {
        // error handshake, check throwable
});
```

<h3>those 2 calls can be chained:</h3>
```java
String url = "yourAmpacheInstanceURL";
String username = "yourAmpacheUsername";
String password = "yourAmpacheUserPassword";

AmpacheApi.INSTANCE.initUser(url,username,password)
    .flatMap(aVoid -> AmpacheApi.INSTANCE.handshake())
    .subscribe(handshakeResponse -> {
            // handshake successful, user logged in
        }, throwable -> {
            // error handshake, check throwable
        });
});

```

<h3>request all the artists:</h3>
```java
AmpacheApi.INSTANCE.getArtists())
    .subscribe(List<Artists> artists -> ....)
```

<h3>request all the albums:</h3>
```java
AmpacheApi.INSTANCE.getAlbums())
    .subscribe(List<Album> albums -> ....)
```

<h3>request all the songs:</h3>
```java
AmpacheApi.INSTANCE.getSongs())
    .subscribe(List<Song> songs -> ....)
```

<h3>request all the albums from a given artist id:</h3>
```java
AmpacheApi.INSTANCE.getAlbumsFromArtist(artistId))
    .subscribe(List<Album> albums -> ....)
```

<h3>request all songs from a given album id:</h3>
```java
AmpacheApi.INSTANCE.getSongsFromAlbum(albumId))
    .subscribe(List<Song> songs -> ....)
```

<h3>the ampache session will expire, the expiration time is stored inside the handshake response, to avoid the expiration of the session
ping the server periodically: </h3>
```java
AmpacheApi.INSTANCE.ping())
    .subscribe(PingResponse pingResponse -> ....)
```

<h3>error handling:</h3><br>
for every API request, in the request fails check if the throwable instance returned is instance of AmpacheApiException, in that case you can
cast to it and get other info about the error
```java
public void onError(Throwable throwable) {
        String message;
        if (throwable instanceof AmpacheApiException) {
            message = "Ampache error\ncode:" + ((AmpacheApiException) throwable).getAmpacheError().getCode() + "\nerror: " +
                    ((AmpacheApiException) throwable).getAmpacheError().getError();
        } else if (throwable.getLocalizedMessage()!=null) {
            message = throwable.getLocalizedMessage();
        } else {
            message = "Undefined error";
        }
    }
```

