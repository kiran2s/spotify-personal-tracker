package com.kssivakumar.spotifypersonaltracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class MainActivity
        extends AppCompatActivity
        implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    private static final String LOG_ID = "MainActivity";

    private static final String CLIENT_ID = "c8833792da3d4200bd22ce88d5c1da81";
    private static final String REDIRECT_URI = "kssivakumar-spotify-personal-tracker://callback";
    private static final int AUTH_REQUEST_CODE = 1337;

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI
        )
                .setScopes(new String[]{"user-read-private", "streaming"})
                .build();

        AuthenticationClient.openLoginActivity(this, AUTH_REQUEST_CODE, authRequest);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AUTH_REQUEST_CODE: {
                AuthenticationResponse authResponse
                        = AuthenticationClient.getResponse(resultCode, data);
                if (authResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                    Config playerConfig = new Config(this, authResponse.getAccessToken(), CLIENT_ID);
                    Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                        @Override
                        public void onInitialized(SpotifyPlayer spotifyPlayer) {
                            player = spotifyPlayer;
                            player.addConnectionStateCallback(MainActivity.this);
                            player.addNotificationCallback(MainActivity.this);
                        }

                        @Override
                        public void onError(Throwable error) {
                            Log.e(LOG_ID, "Could not initialize player: " + error.getMessage());
                        }
                    });
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d(LOG_ID, "User logged in");

        player.playUri(null, "spotify:track:0JGuSgtmWyfuxLGBs2wX0C", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d(LOG_ID, "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.e(LOG_ID, "Login failed: " + error.name());
    }

    @Override
    public void onTemporaryError() {
        Log.e(LOG_ID, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d(LOG_ID, "Received connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d(LOG_ID, "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.e(LOG_ID, "Playback error received: " + error.name());
        switch (error) {
            // Handle as necessary
            default:
                break;
        }
    }
}
