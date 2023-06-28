package gammafp.playgames;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.games.AuthenticationResult;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.Player;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

@CapacitorPlugin(name = "PlayGames")
public class PlayGamesPlugin extends Plugin {
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private Player aLocalPlayer;
    private boolean aIsInitialized = false;
    private PluginCall savedCall;
    private Activity aActivity;

    @Override
    public void load() {
        aActivity = this.bridge.getActivity();
        PlayGamesSdk.initialize(this.getContext());
        aIsInitialized = true;
        checkAuthenticated(PlayGames.getGamesSignInClient(this.getActivity()).isAuthenticated());
    }

    @PluginMethod()
    public void echo(PluginCall call) {
        JSObject info = new JSObject();
        info.put("isLogin", "COJONES");
        call.resolve(info);
    }

    // Event login
    @PluginMethod()
    public void login(PluginCall call) {
        this.savedCall = call;
        checkAuthenticated(PlayGames.getGamesSignInClient(this.getActivity()).signIn());
    }

    @PluginMethod()
    public void status(PluginCall call) {
        savedCall = call;
        checkAuthenticated(PlayGames.getGamesSignInClient(this.getActivity()).isAuthenticated());
    }

    private void checkAuthenticated(Task<AuthenticationResult> pTask) {
        pTask
                .addOnCompleteListener(isAuthenticatedTask -> {

                    boolean isAuthenticated = (isAuthenticatedTask.isSuccessful() && isAuthenticatedTask.getResult().isAuthenticated());

                    if (!isAuthenticated) {
                        // Disable your integration with Play Games Services or show a
                        // login button to ask  players to sign-in. Clicking it should
                        // call GamesSignInClient.signIn().
                        JSObject info = new JSObject();
                        info.put("message", "isnotauthenticated");
                        info.put("isLogin", false);
                        notifyListeners("onSignInStatus", info);
                        if (savedCall != null) {
                            savedCall.resolve(info);
                        }
                        return;
                    }

                    // Continue with Play Games Services
                    PlayGames.getPlayersClient(this.getActivity()).getCurrentPlayer().addOnCompleteListener(mTask -> {
                        aLocalPlayer = mTask.getResult();
                        JSObject info = new JSObject();

                        info.put("id", aLocalPlayer.getPlayerId());
                        info.put("display_name", aLocalPlayer.getDisplayName());
                        info.put("icon", aLocalPlayer.getIconImageUri());
                        info.put("title", aLocalPlayer.getTitle());
                        info.put("login", true);

                        info.put("message", "correct");
                        info.put("isLogin", true);

                        notifyListeners("onSignInStatus", info);

                        if (savedCall != null) {
                            savedCall.resolve(info);
                        }

                    });
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        JSObject info = new JSObject();
                        info.put("message", "error");
                        info.put("isLogin", false);
                        notifyListeners("onSignInStatus", info);
                        if (savedCall != null) {
                            savedCall.resolve(info);
                        }
                    }
                });
    }

    @PluginMethod()
    public void submitScore(PluginCall call) {
        String id = call.getString("id");

        int score = call.getInt("score");

        PlayGames.getLeaderboardsClient(this.getActivity())
                .submitScore(id, score);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void showAchievements(PluginCall call) {
        PlayGames.getAchievementsClient(this.getActivity())
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        getActivity().startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }

    @PluginMethod()
    public void unLockAchievement(PluginCall call) {
        String id = call.getString("id");
        PlayGames.getAchievementsClient(this.getActivity()).unlock(id);
    }

    @PluginMethod()
    public void showAllLeaderboard(PluginCall call) {
        PlayGames
                .getLeaderboardsClient(getActivity())
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        getActivity().startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });

    }


    @PluginMethod()
    public void showLeaderboard(PluginCall call) {

        String leaderBoardId = call.getString("id");
        PlayGames.getLeaderboardsClient(getActivity())
                .getLeaderboardIntent(leaderBoardId)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        getActivity().startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }
}
