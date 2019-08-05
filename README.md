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

import gamma.plugins.playgame.PlayGames;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ...
    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
        // ...
        add(PlayGames.class);
    }});
  }
}
```

4) Ingresar **games-ids.xml** a tu proyecto: 
    - Ve a tu consola de google play games services (esto lo deberías saber) y clica en logros o marcadores y abajo te saldrá la opción de obtener recursos:

    ![Recursos](https://i.gyazo.com/1c9b2be5c6563c30631fe0f49454e68e.png "Recursos")

    - En **Android Studio** ve al plugin **capacitor-play-games-services > res > values** y agrega el **games-ids.xml**

#### Felicidades ya tienes tu plugin agregado en tu proyecto de android

--- 

## Integración en TypeScript:

Para integrarlo en tu proyecto javascript, debes importar el modulo del capacitor y
lugo obtener el plugin.

```javascript
import { Plugins } from '@capacitor/core';
import { PlayGamesPlugin } from 'capacitor-play-games-services';
const playGames = Plugins.PlayGames as PlayGamesPlugin;
```

--- 
### Métodos

### Login - signin

Autenticar
```javascript
playGames.auth().then((response) => {
    /* response return: 
        id: string;
        display_name: string;
        icon: string; // URI Does not work yet.
        title: string;
        login: boolean; TRUE if is online FALSE if is offline
    */
});
```

Obtener el estado de la conexión
```javascript
playGames.signStatus().then((response) => {
    /* response return: 
        login: boolean; TRUE if is online FALSE if is offline
    */
});
```

Logout
```javascript
playGames.signOut().then((response) => {
    /* response return: 
        login: boolean; TRUE if is online FALSE if is offline
    */
});
```
---

### Leaderboard

Mostrar todos los tablero de puntuación
```javascript
playGames.showAllLeaderboard();
```

Mostrar un tablero de puntuación determinado
```javascript
playGames.showLeaderboard({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard
});
```

Meter puntos a un tablero de puntuaciones
```javascript
playGames.submitScore({
    id: 'XXXXXXXX-XXXXXXXX' // id of your leaderboard,
    points: 10 // int value
});
```
---
### Medallas

Mostrar todas las medallas
```javascript
playGames.showAchievements();
```

Desbloquear una medalla
```javascript
playGames.unlockAchievement({
    id: 'XXXXXXXX-XXXXXXXX' // id of your achievement
});
```

Incrementar una medalla:
Nota: Puedes poner una medalla que por medio de cierta cantidades de veces que es precionado o activado cierta actividad la medalla sea habilitada. 
```javascript
playGames.incrementAchievement({
    id: 'XXXXXXXX-XXXXXXXX' // id of your achievement
    step: 1
});
```
---
### Save game

Mostrar las partidas guardadas por medio del UI de google

```javascript
playGames.showSavedGames()
.then((response) => {
    /*
    response return: 
    login: boolean // show if you account is offline or online
    save_game: JSON // Si se ha seleccionado una partida guardada puedes recuperarla acá.
    */
})
.catch((error) => {
    // error: Show all possible errors
});
```

Guardar partida
```javascript
playGames.saveGame({
    save_name: string, // without space and stranger char. Please :D
    data: string, // json 
    description: string
}).then((response) => {
    /*
        response return: 
        status?: boolean
        save_status: boolean // true if you game is saved, false (need implementation then return null)
    */
});
```

Cargar partida guardada
```javascript
playGames.loadGame({
    load_name: string // Name of you save_name, without space and stranger char. Please :D
}).then((response) => {
    /*
        response return: 
        status?: boolean
        save_status: boolean // true if you game is saved, false (need implementation then return null)
    */
});
```