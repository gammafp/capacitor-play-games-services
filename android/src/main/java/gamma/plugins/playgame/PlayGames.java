package gamma.plugins.playgame;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

@NativePlugin(requestCodes = PlayGames.REQUEST_SIGN_IN)
public class PlayGames extends Plugin {

    static final int REQUEST_SIGN_IN = 10001;
    private PlayGamesUtils playGamesUtils;
    
    // Leaderboard
    private static final int RC_LEADERBOARD_UI = 9004;
    
    
    @PluginMethod()
    public void signInSilently(final PluginCall call) {
        
        saveCall(call);
        playGamesUtils = new PlayGamesUtils((Activity) this.getBridge().getContext(), call);
        
        // Obtenemos el tipo de login google o google games
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());

        // Comprobamos si ya se ha hecho login anteriormente
        if(GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Si ya se ha logeado antes entra acá
            GoogleSignInAccount signedInAccount = account;
            playGamesUtils.signInSuccess(signedInAccount);
            
        } else {
            // Tratamos de hacer login silencioso primero
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this.bridge.getContext(), signInOptions);
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    (Activity) this.getBridge().getContext(),
                    new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                // Se hace login y se guarda
                                GoogleSignInAccount signedInAccount = task.getResult();

                                // Obtenemos los datos del cliente y lo enviamos
                                playGamesUtils.signInSuccess(signedInAccount);
                                
                            } else {
                                // No se pudo hacer login silencioso y se necesita hacer login normal
                                startSignInIntent();
                            }
                        }
                    }
                );
        }
    }
    
    private void startSignInIntent() {
        PluginCall saveCall = getSavedCall();
        
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this.getBridge().getContext(), 
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(saveCall, intent, REQUEST_SIGN_IN);
    }
    
    // Evento generado cuando se hace login
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
          
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            
                // Obtenemos los datos del cliente y lo enviamos
                playGamesUtils.signInSuccess(signedInAccount);
               
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Puede que hayas puesto mal los datos del manifest.xml o el games-ids.xml\n" +
                        "O simplemente no se ha hecho login";
                }
                Log.e("ERROR", message);
            }
        }
    }
    
    @PluginMethod()
    public void signStatus(final PluginCall call) {
        
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());
        
        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
        
        JSObject info = new JSObject();
        info.put("login", status);

        call.resolve(info);
    }

    @PluginMethod()
    public void signOut(final PluginCall call) {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this.getBridge().getContext(),
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        
        signInClient.signOut().addOnCompleteListener((Activity) this.getBridge().getContext(),
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // at this point, the user is signed out.
                    JSObject info = new JSObject();
                    info.put("login", false);

                    call.resolve(info);
                }
            });
    }
    
    // Leaderboard
    @PluginMethod()
    public void showAllLeaderboard(final PluginCall call) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());

        if(status) {
            Games.getLeaderboardsClient((Activity) this.getBridge().getContext(), 
                GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext()))
                    .getAllLeaderboardsIntent()                    
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(call, intent, RC_LEADERBOARD_UI);
                    }
                });
        }
    }
    
    @PluginMethod()
    public void showLeaderboard(final PluginCall call) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
        
        // Show leaderboard
        String leaderboard_id = call.getString("leaderboard_id");
        
        if(leaderboard_id != null) {
            if(status) {
                
                Games.getLeaderboardsClient((Activity) this.getBridge().getContext(), account)
                    .getLeaderboardIntent(leaderboard_id)
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(call, intent, RC_LEADERBOARD_UI);
                        }
                    });
                
            }
        } else {
            System.out.println("No se ha ingresado el id de la tabla");
        }
    }
    
    // TODO: Hacer que se acceda correctamente
    @PluginMethod()
    public void submitScore(final PluginCall call) {
        System.out.println("Se ingresan datos");
        // Show leaderboard
        String leaderboard_id = call.getString("leaderboard_id");
        int points = call.getInt("points", 0);
        
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());

        Boolean status = GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray());
        
        if(leaderboard_id != null) {
            if(status) {
                System.out.println("------> Leaderboard_id " + leaderboard_id);
                Games.getLeaderboardsClient((Activity) this.getBridge().getContext(), account)
                    .submitScore(leaderboard_id, 54321);
            }
        }
        
    }
    


}
