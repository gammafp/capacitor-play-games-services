# capacitor-play-games-services
Capacitor Play Games Services is a native Google Play Games Services implementation for Android. 

1) To be able to use this plugin it is necessary to download it to the project with npm:

```bash
> npm i capacitor-play-games-services
```

2) Once downloaded you have to update the project to recognize the plugin: 

```bash
> npx cap update && npx cap sync
```

3) Open Android Studio and register the plugin in your Android project.
    - Go to **android > app > src > main > java > domain name of your project > MainActivity.java**
    - Add the plugin to your MainActivity, example:
```java
package com.xx.xx;

...

import gammafp.playgames.PlayGamesPlugin;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(PlayGamesPlugin.class);

        // Important, super.onCreate(...) should go down
        super.onCreate(savedInstanceState);
    }
}
```
4) Add **games id** to your project: 
    - Go to your google play games services console (you should know this) and click on achievements or markers and below you will see the option to obtain resources:

    ![Recursos](https://i.gyazo.com/1c9b2be5c6563c30631fe0f49454e68e.png "Recursos")

    - When you get the resources get the id number and copy it.
    ![Recursos](https://i.gyazo.com/d2ad93555aa0db1550752b50bf53b687.png "app_id")
    - In **Android Studio** go to plugin **capacitor-play-games-services > res > values > string.xml** and add the app_id.
    ![app_id](https://i.gyazo.com/167075c1a5ef219e967ea04d9a8b9e57.png "app_id")

#### Congratulations, you already have your plugin added to your android project

--- 

## TypeScript integration:

To integrate it into your javascript project, you must import the capacitor module and
then get the plugin.

```javascript
import { PlayGames } from 'capacitor-play-games-services';
```

--- 
## Methods

### Login - signin

Listen for a change event:
(this is interesting to listen the first login event)
```typescript
PlayGames.addListener("onSignInStatus", (res) => {
    setLoginData(JSON.stringify(res, null, 4));
})
```

### Login
```typescript
playGames.login().then((response) => {
    /* response return: 
        id: string;
        display_name: string;
        icon: string; // URI Does not work yet.
        title: string;
        message: string; // A messate that say what happen with the plugin correct / error (usded for tests) 
        isLogin: boolean; TRUE if is online FALSE if is offline
    */
});
```

### Get connection status
```typescript
PlayGames.status().then((response) => {
    /* response return:
        message: string; // A messate that say what happen with the plugin correct / error (usded for tests)  
        isLogin: boolean; TRUE if is online FALSE if is offline
    */
});
```

### Logout

this method has been removed by google.

---

## Leaderboard

### Show all leaderboard
```typescript
PlayGames.showAllLeaderboard();
```

### Show one leaderboard by id
```typescript
PlayGames.showLeaderboard({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard
});
```

### Add points to leaderboard
```typescript
PlayGames.submitScore({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard,
    score: 10 // int value only
});
```
---

### Medals
Show all medals
```typescript
PlayGames.showAchievements();
```

### Unlock one medal
```typescript
PlayGames.unlockAchievement({
    id: 'XXXXXXXX-XXXXXXXX' // id of your achievement
});
```

## IMPORTANT: For testings you need sign your app.