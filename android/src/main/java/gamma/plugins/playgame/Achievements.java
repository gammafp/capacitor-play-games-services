package gamma.plugins.playgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class Achievements {
    
    private static final int RC_ACHIEVEMENT_UI = 9003;
    
    PlayGames plugin;
    public Achievements(PlayGames Plugin) {
        plugin = Plugin;
    }
    
    
    public void showAchievements(final PluginCall call) {
        Context context = plugin.getBridge().getContext();

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        if(status) {
            Games.getAchievementsClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        plugin.startActivityForResult(call, intent, RC_ACHIEVEMENT_UI);
                    }
                });
        }
    }
}
