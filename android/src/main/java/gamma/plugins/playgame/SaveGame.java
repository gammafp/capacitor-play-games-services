package gamma.plugins.playgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SaveGame {

    private final int RC_SAVED_GAMES = 9009;
    private SnapshotsClient snapshotsClient;
    private boolean savingFile = false;
    private int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    PlayGames plugin;
    public SaveGame(PlayGames Plugin) {
        plugin = Plugin;

    }

    public void showSavedGamesUI(final PluginCall call) {

        if(plugin.signStatus(call)) {
            SnapshotsClient snapshotsClient =
                Games.getSnapshotsClient(plugin.getBridge().getContext(),
                    GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext()));
            int maxNumberOfSavedGamesToShow = 5;

            Task<Intent> intentTask = snapshotsClient.getSelectSnapshotIntent(
                "My save games", true, true, maxNumberOfSavedGamesToShow);

            intentTask.addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    plugin.startActivityForResult(call, intent, RC_SAVED_GAMES);
                }
            });
        } else {
            JSObject info = new JSObject();
            info.put("login", false);
            call.resolve(info);
        }
    }

    public void saveSnapshot() {
        // https://github.com/Kopfenheim/godot-gpgs/blob/master/gpgs/android/org/godotengine/godot/gpgs/SavedGames.java
        
        
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());


        snapshotsClient = Games.getSnapshotsClient((Activity) plugin.getBridge().getContext(), account);

        snapshotsClient.open("Pepe", true, conflictResolutionPolicy)
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR", "ERROR while opening snapshot for saving: ", e);
                    // GodotLib.calldeferred(instance_id, GODOT_CALLBACK_FUNCTIONS[3], new Object[] { false });
                }
            })
            .continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, Object>() {
                @Override
                public Object then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                    Snapshot snapshot = task.getResult().getData();

                    snapshot.getSnapshotContents().writeBytes(data.getBytes("UTF-8"));

                    Bitmap coverImage = imageCache.getBitmap(imageFileName);
                    SnapshotMetadataChange metadata;
                    if (coverImage != null){
                        metadata = new SnapshotMetadataChange.Builder()
                            .setCoverImage(coverImage)
                            .setDescription(description)
                            .build();
                    }else{
                        metadata = new SnapshotMetadataChange.Builder()
                            .setDescription(description)
                            .build();
                    }

                    snapshotsClient.commitAndClose(snapshot, metadata);
                    return null;
                }
            });
    }
    
}
