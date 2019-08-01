package gamma.plugins.playgame;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class Leaderboard {
    private static final int RC_LEADERBOARD_UI = 9004;
    
    PlayGames plugin;
    public Leaderboard(PlayGames Plugin) {
        plugin = Plugin;
    }

    
    public void showAllLeaderboard(final PluginCall call) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        if(status) {
            Games.getLeaderboardsClient((Activity) plugin.getBridge().getContext(),
                GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext()))
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        plugin.startActivityForResult(call, intent, RC_LEADERBOARD_UI);
                    }
                });
        }
    }
    
    public void showLeaderboard(final PluginCall call) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        // Show leaderboard
        String leaderboard_id = call.getString("id");

        if(leaderboard_id != null && call.getData().has("id")) {
            if(status) {

                Games.getLeaderboardsClient((Activity) plugin.getBridge().getContext(), account)
                    .getLeaderboardIntent(leaderboard_id)
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            plugin.startActivityForResult(call, intent, RC_LEADERBOARD_UI);
                        }
                    });

            }
        } else {
            Log.w("WARNING", "The provider leaderboard id is required");
            call.reject("The provider id is required");
        }
    }
    
    public void submitScore(final PluginCall call) {

        String leaderboard_id = call.getString("id");
        int points = call.getInt("points", 0);

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        if(leaderboard_id != null && call.getData().has("id")) {
            if(status) {
                Games.getLeaderboardsClient((Activity) plugin.getBridge().getContext(), account)
                    .submitScore(leaderboard_id, points);
            }
        } else {
            Log.w("WARNING", "The provider leaderboard id is required");
            call.reject("The provider id is required");
        }

    }

}
