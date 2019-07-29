package gamma.plugins.playgame;

import android.support.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PlayGamesUtils {

    private PluginCall call;
    
    public PlayGamesUtils(PluginCall Call) {
        call = Call;
    }
    
    // Nos ayudará a devolver el signIn cuando esté completo
    public void signInSuccess(final PlayersClient mPlayersClient) {

        mPlayersClient.getCurrentPlayer()
            .addOnCompleteListener(new OnCompleteListener<Player>() {
                @Override
                public void onComplete(@NonNull Task<Player> task) {
                    String displayName = "UserName";

                    if (task.isSuccessful()) {
                        displayName = task.getResult().getDisplayName();
                    } else {
                        Exception e = task.getException();
                    }
    
                    JSObject info = new JSObject();
                    info.put("id", mPlayersClient.getCurrentPlayerId());
                    info.put("display_name", displayName);
                    
                    call.resolve(info);
                    
                }
            });
        
    }
}
