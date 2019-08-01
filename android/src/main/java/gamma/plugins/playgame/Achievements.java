package gamma.plugins.playgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getcapacitor.Plugin;
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

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
                
        if(status) {
       
            Games.getAchievementsClient(plugin.getBridge().getContext(), GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext()))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        plugin.startActivityForResult(call, intent, RC_ACHIEVEMENT_UI);
                    }
                });
     

        }
    }
    
    public void unlockAchievement(final PluginCall call) {

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
        
        final String achievement_id = call.getString("id");
        
        Activity activity = (Activity) plugin.getBridge().getContext();
        
        if(status) {
            if(achievement_id != null && call.getData().has("id")) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Games.getAchievementsClient(plugin.getBridge().getContext(), GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext() ))
                            .unlock(achievement_id);
                    }
                });
            } else {
                Log.w("WARNING", "The provider achievements id is required");
                call.reject("The provider achievements id is required");
            }
        }
    }

    public void incrementAchievement(final PluginCall call) {

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        final String achievement_id = call.getString("id");
        final Integer step = call.getInt("step", 1);

        Activity activity = (Activity) plugin.getContext();
        
        if(status) {
            if(achievement_id != null && call.getData().has("id")) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Games.getAchievementsClient(plugin.getBridge().getContext(), GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext()))
                            .increment(achievement_id, step);        
                    }
                });
                
            } else {
                Log.w("WARNING", "The provider achievements id is required");
                call.reject("The provider achievements id is required");
            }
        }
    }

}
