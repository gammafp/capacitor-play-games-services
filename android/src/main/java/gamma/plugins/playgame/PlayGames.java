package gamma.plugins.playgame;

import android.app.AlertDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

@NativePlugin(requestCodes = PlayGames.REQUEST_SING_IN)
public class PlayGames extends Plugin {

    static final int REQUEST_SING_IN = 10001;

    @PluginMethod()
    public void signInSilently(final PluginCall call) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getBridge().getContext());
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            GoogleSignInAccount signedInAccount = account;
            JSObject info = new JSObject();
            info.put("id", signedInAccount.getId());
            info.put("display_name", signedInAccount.getDisplayName());
            info.put("family_name", signedInAccount.getFamilyName());
            info.put("given_name", signedInAccount.getGivenName());
            info.put("email", signedInAccount.getEmail());
            info.put("id_token", signedInAccount.getIdToken());
            info.put("photo_url", signedInAccount.getPhotoUrl());
            info.put("server_auth_code", signedInAccount.getServerAuthCode());
            call.resolve(info);
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this.getBridge().getActivity(), signInOptions);
            signInClient
            .silentSignIn()
            .addOnCompleteListener(
                this.getBridge().getActivity(),
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            GoogleSignInAccount signedInAccount = task.getResult();
                        } else {
                            System.out.println("no se pudo ingresar silenciosamente");
                            startSignInIntent(call);
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

    private void startSignInIntent(PluginCall call) {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this.getBridge().getActivity(),
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(call, intent, REQUEST_SING_IN);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }
        if (requestCode == REQUEST_SING_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                JSObject info = new JSObject();
                info.put("id", signedInAccount.getId());
                info.put("display_name", signedInAccount.getDisplayName());
                info.put("family_name", signedInAccount.getFamilyName());
                info.put("given_name", signedInAccount.getGivenName());
                info.put("email", signedInAccount.getEmail());
                info.put("id_token", signedInAccount.getIdToken());
                info.put("photo_url", signedInAccount.getPhotoUrl());
                info.put("server_auth_code", signedInAccount.getServerAuthCode());
                savedCall.resolve(info);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Mensaje";
                }
                new AlertDialog.Builder(this.getBridge().getActivity()).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }
}
