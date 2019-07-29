package gamma.plugins.playgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;

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
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

@NativePlugin(requestCodes = PlayGames.REQUEST_SIGN_IN)
public class PlayGames extends Plugin {

    static final int REQUEST_SIGN_IN = 10001;
    private PlayGamesUtils playGamesUtils;
    private PlayersClient mPlayersClient;
    
    @PluginMethod()
    public void signInSilently(final PluginCall call) {
        
        saveCall(call);
        playGamesUtils = new PlayGamesUtils(call);
        
        // Obtenemos el tipo de login google o google games
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());
        
        // Comprobamos si ya se ha hecho login anteriormente
        if(GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Ya se ha hecho login
            // Obtenemos el login de una cuenta guardada anteriormente
            GoogleSignInAccount signedInAccount = account;
            mPlayersClient = Games.getPlayersClient((Activity) this.getBridge().getContext(), signedInAccount);
           
            System.out.println("Bienvenido otra vez");
            // Mensaje de bienvenida si solo si el usuario ha pasado mucho tiempo desconectado
            Games.getGamesClient(this.bridge.getContext(), signedInAccount).setGravityForPopups(Gravity.TOP);
            playGamesUtils.signInSuccess(mPlayersClient);
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
                                
                            } else {
                                System.out.println("no se pudo ingresar silenciosamente");
                                // NO se pudo hacer login silencioso y se necesita hacer login normal
                                startSignInIntent();
                                // Player will need to sign-in explicitly using via UI.
                                // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                // Interactive Sign-in.
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
                mPlayersClient = Games.getPlayersClient((Activity) this.getBridge().getContext(), signedInAccount);
                Games.getGamesClient(this.bridge.getContext(), signedInAccount).setGravityForPopups(Gravity.TOP);
                playGamesUtils.signInSuccess(mPlayersClient);
               
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Game Services no disponible";
                }
                new AlertDialog.Builder(this.getBridge().getContext()).setMessage(message + " - " + result.getStatus().getStatusMessage())
                    .setNeutralButton(android.R.string.ok, null).show();
            }
        }
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
                    mPlayersClient = null;
                    System.out.println("El usuario ha hecho logout");
                }
            });
    }

}
