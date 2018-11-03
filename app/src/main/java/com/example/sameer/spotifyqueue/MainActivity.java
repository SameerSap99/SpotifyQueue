package com.example.sameer.spotifyqueue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;
//import com.spotify.sdk.android.authentication;

public class MainActivity extends AppCompatActivity {
    private String CLIENT_ID = "ea271345845f4705975a6811e189220f";
    int REQUEST_CODE = 1337;
    String REDIRECT_URI = "testschema://callback";
    String AccessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Started App", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // We will start writing our code here.

        //System.out.println("opened login");
        Toast.makeText(this, "opened login", Toast.LENGTH_SHORT).show();
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        //System.out.println("BUILDER");
        Toast.makeText(this, "BUILDER", Toast.LENGTH_SHORT).show();

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    private void connected() {
        // Then we will write some more code here.
        System.out.println(AccessToken);
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(AccessToken);
        SpotifyService spotify = api.getService();
        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Toast.makeText(MainActivity.this, album.name, Toast.LENGTH_SHORT).show();
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Toast.makeText(this, "OnActivityResult", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        System.out.println("request code: " + requestCode);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Toast.makeText(this, response.getError(), Toast.LENGTH_SHORT).show();
            System.out.println("ERROR: ");
            System.out.println(response.getError());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    AccessToken = response.getAccessToken();
                    Toast.makeText(this, "Toast", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, AccessToken, Toast.LENGTH_SHORT).show();
                    connected();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    System.out.println("ERROR: ");
                    System.out.println(response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

}
