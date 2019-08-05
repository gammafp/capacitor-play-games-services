package gamma.plugins.playgame;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PlayGamesUtils {
    private PlayersClient mPlayersClient;
    private PluginCall call;
    private Activity activity_plugin;
    
    private PlayGames plugin;
    
    public PlayGamesUtils(PlayGames Plugin, PluginCall Call) {
        plugin = Plugin;
        call = Call;
        activity_plugin = (Activity) Plugin.getBridge().getContext();
    }
    
    // Nos ayudará a devolver el signIn cuando esté completo
    public void signInSuccess(final GoogleSignInAccount signedInAccount) {
        mPlayersClient = Games.getPlayersClient(activity_plugin, signedInAccount);

        mPlayersClient.getCurrentPlayer()
            .addOnCompleteListener(new OnCompleteListener<Player>() {
                @Override
                public void onComplete(@NonNull Task<Player> task) {
                    String displayName = "UserName";
                    String id = "id";
                    Uri icon = null;
                    String title = "title";
                    

                    if (task.isSuccessful()) {
                        id = task.getResult().getPlayerId();
                        displayName = task.getResult().getDisplayName();
                        icon = task.getResult().getIconImageUri();
                        title = task.getResult().getTitle();
                        
                    } else {
                        Exception e = task.getException();
                    }
                    
                    
                    Games.getGamesClient(activity_plugin, signedInAccount).setViewForPopups(
                        activity_plugin.getWindow().getDecorView().findViewById(android.R.id.content)
                    );

                    JSObject info = new JSObject();
                    
                    info.put("id", id);
                    info.put("display_name", displayName);
                    info.put("icon", icon);
                    info.put("title", title);
                    info.put("login", true);
                    
                    call.resolve(info);
                    
                }
            });
        
    }
}
