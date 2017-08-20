package com.kssivakumar.spotifypersonaltracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_ID = "MainActivity";

    private static final String CLIENT_ID = "c8833792da3d4200bd22ce88d5c1da81";
    private static final String REDIRECT_URI = "kssivakumar-spotify-personal-tracker://callback";
    private static final int AUTH_REQUEST_CODE = 1337;

    private SpotifyApi webApi;
    private SpotifyService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webApi = new SpotifyApi();

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI
        )
                .setScopes(new String[]{"user-read-private"})
                .build();

        AuthenticationClient.openLoginActivity(this, AUTH_REQUEST_CODE, authRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AUTH_REQUEST_CODE: {
                AuthenticationResponse authResponse
                        = AuthenticationClient.getResponse(resultCode, data);
                if (authResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                    webApi.setAccessToken(authResponse.getAccessToken());
                    webService = webApi.getService();
                }
                break;
            }
            default:
                break;
        }
    }
}
