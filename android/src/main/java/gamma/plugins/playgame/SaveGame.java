package gamma.plugins.playgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class SaveGame {

    static final int RC_SAVED_GAMES = 9009;
    private SnapshotsClient snapshotsClient;
    private boolean savingFile = false;
    private int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;
    public String mCurrentSaveName;
    

    PlayGames plugin;
    public SaveGame(PlayGames Plugin) {
        plugin = Plugin;

    }

    public void handleOnActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA)) {
                // Load a snapshot.
                SnapshotMetadata snapshotMetadata =
                    intent.getParcelableExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA);
                mCurrentSaveName = snapshotMetadata.getUniqueName();

                this.requestLoadSnapshot(mCurrentSaveName);
                
                
            } else if (intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_NEW)) {
                // Create a new snapshot named with a unique string
                String unique = new BigInteger(281, new Random()).toString(13);
                mCurrentSaveName = "snapshotTemp-" + unique;
                // Create new snapshot
            }
        }
        
    }
    
    public void showSavedGamesUI(final PluginCall call) {
        
        plugin.saveCall(call);
        
        if(plugin.signStatusLocal()) {
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

    public void requestLoadSnapshot(String snapshotName){

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        snapshotsClient = Games.getSnapshotsClient((Activity) plugin.getBridge().getContext(), account);

        snapshotsClient.open(snapshotName, true, conflictResolutionPolicy)
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR", "ERROR while opening snapshot for loading: ", e);
                    plugin.getSavedCall().reject("ERROR while opening snapshot for loading: ", e);
                }
            })
            .continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, String>() {
                @Override
                public String then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                    Snapshot snapshot = task.getResult().getData();

                    try{
                        return new String(snapshot.getSnapshotContents().readFully(),"UTF-8");
                    } catch (IOException e){
                        Log.e("ERROR", "ERROR while opening snapshot for loading: ", e);
                        plugin.getSavedCall().reject("ERROR while opening snapshot for loading: ", e);
                    }

                    return null;
                }
            })
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    JSObject info = new JSObject();

                    info.put("save_game", task.getResult());
                    plugin.getSavedCall().resolve(info);
                }
            });
            
    }
    
    public void saveSnapshot(String snapshotName, final String data, final String description, final String imageFileName) {
        
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(plugin.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
        
        if(status) {
            snapshotsClient = Games.getSnapshotsClient((Activity) plugin.getBridge().getContext(), account);
            
            snapshotsClient.open(snapshotName, true, conflictResolutionPolicy)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", "ERROR while opening snapshot for saving: ", e);
                    }
                })
                .continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, Object>() {
                    @Override
                    public Object then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                        Snapshot snapshot = task.getResult().getData();

                        snapshot.getSnapshotContents().writeBytes(data.getBytes("UTF-8"));

                        // Bitmap coverImage = imageCache.getBitmap(imageFileName);
                        Bitmap coverImage = null;

                        SnapshotMetadataChange metadata;
                        if (coverImage != null){
                            metadata = new SnapshotMetadataChange.Builder()
                                .setCoverImage(coverImage)
                                .setDescription(description)
                                .build();
                        } else{
                            metadata = new SnapshotMetadataChange.Builder()
                                .setDescription(description)
                                .build();
                        }

                        snapshotsClient.commitAndClose(snapshot, metadata);
                        
                        JSObject info = new JSObject();
                        info.put("save_status", true);
                        plugin.getSavedCall().resolve(info);
                        return null;
                    }
                });
        } else {
            JSObject info = new JSObject();
            info.put("login", false);
            plugin.getSavedCall().resolve(info);
        }
        
    }
    public void setConflictResolutionPolicy(int value){
        conflictResolutionPolicy = value;
    }
    
}
