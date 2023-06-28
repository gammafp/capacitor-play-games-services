# capacitor-play-games-services
Capacitor Play Games Services is a native Google Play Games Services implementation for Android. 

1) Para poder usar este plugin es necesario descargarlo al proyecto con npm:

```bash
> npm i capacitor-play-games-services
```

2) Una vez descargado se tiene que actualizar el proyecto para que reconozca el plugin: 

```bash
> npx cap update && npx cap sync
```

3) Abre Android Studio y registra el plugin en tu proyecto de Android.
    - Ve a **android > app > src > main > java > nombre de dominio de tu proyecto > MainActivity.java**
    - Agrega el plugin a tu MainActivity, ejemplo:
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
4) Ingresar **games-ids.xml** a tu proyecto: 
    - Ve a tu consola de google play games services (esto lo deberías saber) y clica en logros o marcadores y abajo te saldrá la opción de obtener recursos:

    ![Recursos](https://i.gyazo.com/1c9b2be5c6563c30631fe0f49454e68e.png "Recursos")

    - Cuando obtengas los recursos obten el número id y copialo.
    ![Recursos](https://i.gyazo.com/d2ad93555aa0db1550752b50bf53b687.png "app_id")
    - En **Android Studio** ve al plugin **capacitor-play-games-services > res > values > string.xml** agrega el app_id.
    ![app_id](https://i.gyazo.com/167075c1a5ef219e967ea04d9a8b9e57.png "app_id")

#### Felicidades ya tienes tu plugin agregado en tu proyecto de android

--- 

## Integración en TypeScript:

Para integrarlo en tu proyecto javascript, debes importar el modulo del capacitor y
lugo obtener el plugin.

```javascript
import { PlayGames } from 'capacitor-play-games-services/src';
```

--- 
## Métodos

### Login - signin

Poner en escucha un evento de cambios
```typescript
PlayGames.addListener("onSignInStatus", (res) => {
    setLoginData(JSON.stringify(res, null, 4));
})
```

### Hacer Login
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

### Obtener el estado de la conexión
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

### Mostrar todos los tablero de puntuación
```typescript
PlayGames.showAllLeaderboard();
```

### Mostrar un tablero de puntuación determinado
```typescript
PlayGames.showLeaderboard({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard
});
```

### Meter puntos a un tablero de puntuaciones
```typescript
PlayGames.submitScore({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard,
    score: 10 // int value only
});
```
---
### Medallas

Mostrar todas las medallas
```typescript
PlayGames.showAchievements();
```

### Desbloquear una medalla
```typescript
PlayGames.unlockAchievement({
    id: 'XXXXXXXX-XXXXXXXX' // id of your achievement
});
```
