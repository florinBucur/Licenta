package com.bucur.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by bucur on 5/14/2015.
 */
public class FacebookLoginFragment extends Fragment {
    private TextView welcome;
    private String userId;
    private ProfilePictureView profilePictureView;
    private CallbackManager callbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private FragmentManager mFragmentManager;
    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();
            setupTokenTracker();
            setupProfileTracker();

            mTokenTracker.startTracking();
            mProfileTracker.startTracking();
            Profile profile = Profile.getCurrentProfile();

            if (profile != null) {

                Log.d("Autentificare", loginResult.toString());
                userId = profile.getId();
                welcome.setText(constructWelcomeMessage(profile));
                Log.d("ProfileId", profile.getId());
                profilePictureView.setProfileId(userId);

                Intent intent = new Intent("com.bucur.licenta.intent.action.FriendsActivity");
                intent.putExtra("Username", profile.getName());
                startActivity(intent);

            }
        }

        @Override
        public void onCancel() {

            welcome.setVisibility(View.GONE);
            profilePictureView.setVisibility(View.GONE);
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("Login", "onError " + e);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("Login", "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("Login", "" + currentProfile);
                if (currentProfile != null) {
                    userId = currentProfile.getId();
                    welcome.setText(constructWelcomeMessage(currentProfile));
                    profilePictureView.setProfileId(userId);

                    Intent intent = new Intent("com.bucur.licenta.intent.action.FriendsActivity");
                    intent.putExtra("Username", currentProfile.getName());
                    startActivity(intent);
                }
            }
        };
    }

    private void setupTextDetails(View view) {
        welcome = (TextView) view.findViewById(R.id.welcome);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile_picture);
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions("user_friends, publish_actions, user_groups, user_managed_groups, user_photos");
        mButtonLogin.registerCallback(callbackManager, facebookCallback);
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
        }
        return stringBuffer.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupTextDetails(view);
        setupLoginButton(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Profile profile = Profile.getCurrentProfile();
        //welcome.setText(profile.getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPause() {
        super.onPause();


    }
}
