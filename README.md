# capacitor-play-games-services
Capacitor Play Games Services is a native Google Play Games Services implementation for Android. 

Este proyecto esta realizado con la libreria AndroidX, por lo que debes de migrar
tu proyecto si no lo haz hecho. Para hacerlo, debe ir a la barra de android studio e ir
a Refactor->Migrate to AndroidX

// queda pendiente colocar la ruta de npm

Para integrar la aplicacion con con google play games services, debes generar el arhivo 
games_ids.xml en tu cuenta de google play y pegarlo en main/res/vales de tu proyecto
android.

Para integrarlo en tu proyecto javascript, debes importar el modulo del capacitor y
lugo obtener el plugin.

import 'capacitor-play-games-services';
import { Plugins } from '@capacitor/core';

const { PlayGames } = Plugins;

Luego vas al metodo y lo llamas

loginGooglePlay() {
    PlayGames.signInSilently();
}

Este metodo hace dos procesos, en la parte primera, el va a hacer un login silencioso,
en caso de no tener exito, el realiza un login con una interface. Cuando el proceso 
este completo, el devuelve un objeto con la siguiente estructura:

id: string;
display_name: string;
family_name: string;
given_name: string;
email: string;
id_token: string;
photo_url: string;
server_auth_code: string;