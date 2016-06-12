<h2>An Android Library for Ampache<h2>
<h3>Usage:</h3><br>

<h4>init the library in your application class:</h4>
```java
AmpacheApi.INSTANCE.initSession(this);
```

<h4>if the user has already logged in before, and not logged out:</h4>
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

<h4>initialize a new user:</h4>
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

<h4>after initializing a user, do the handshake to login:</h4>
```java
AmpacheApi.INSTANCE.handshake()
    .subscribe(handshakeResponse -> {
        // handshake successful, user logged in
    }, throwable -> {
        // error handshake, check throwable
});
```

<h4>those 2 calls can be chained:</h4>
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

<h4>request all the artists:</h4>
```java
AmpacheApi.INSTANCE.getArtists())
    .subscribe(List<Artists> artists -> ....)
```

<h4>request all the albums:</h4>
```java
AmpacheApi.INSTANCE.getAlbums())
    .subscribe(List<Album> albums -> ....)
```

<h4>request all the songs:</h4>
```java
AmpacheApi.INSTANCE.getSongs())
    .subscribe(List<Song> songs -> ....)
```

<h4>request all the albums from a given artist id:</h4>
```java
AmpacheApi.INSTANCE.getAlbumsFromArtist(artistId))
    .subscribe(List<Album> albums -> ....)
```

<h4>request all songs from a given album id:</h4>
```java
AmpacheApi.INSTANCE.getSongsFromAlbum(albumId))
    .subscribe(List<Song> songs -> ....)
```

<h4>ping</h4><br>
the ampache session will expire, the expiration time is stored inside the handshake response, to avoid the expiration of the session
ping the server periodically: 
```java
AmpacheApi.INSTANCE.ping())
    .subscribe(PingResponse pingResponse -> ....)
```

<h4>error handling:</h4><br>
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

