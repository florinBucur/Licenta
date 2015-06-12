package com.bucur.licenta;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.widget.CreateAppGroupDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import notifications.NotificationWear;

/**
 * Created by bucur on 6/2/2015.
 */
public class ChatActivity extends Activity {

    private ProfilePictureView profilePictureView;
    private TextView  receiveText;
    private EditText sendText;
    private Button reply;
    private Button initiate;
    private Button post;
    private Button deleteGroup;
    private Button createGroup;
    private Button deleteFromGroup;
    private Button postToGroup;
    private Button groupName;
    CreateAppGroupDialog createAppGroupDialog;
    CallbackManager callbackManager;
    private Stack<NotificationWear> notificationsStack = new Stack<NotificationWear>();
    private ShareDialog shareDialog;
    private String name;
    String fbid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        profilePictureView = (ProfilePictureView)findViewById(R.id.friend_picture_chat);
        receiveText = (TextView)findViewById(R.id.notification);
        sendText = (EditText)findViewById(R.id.send_text);
        reply = (Button)findViewById(R.id.reply);
        initiate = (Button)findViewById(R.id.initiate);
        post = (Button)findViewById(R.id.add_to_group);
        createGroup = (Button)findViewById(R.id.create_group);
        deleteFromGroup = (Button)findViewById(R.id.delete_from_group);
        deleteGroup = (Button)findViewById(R.id.delete_group);
        postToGroup = (Button)findViewById(R.id.post_to_group);

        final Intent intent = getIntent();

        if(intent != null){

            fbid = intent.getStringExtra("Id");
            profilePictureView.setProfileId(fbid);
            ButterKnife.inject(this);
            name = intent.getStringExtra("Name");

            initiate = (Button)findViewById(R.id.initiate);
            initiate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), fbid, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging"));
                    ChatActivity.this.startActivityForResult(intent, 0);
                }
            });
        }

    }

    public void onEvent(final NotificationWear notificationWear) {

        notificationsStack.push(notificationWear);

        final NotificationWear not = notificationWear;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("onEvent", not.bundle.toString());
                TextView textView = (TextView)findViewById(R.id.notification);
                textView.setText("From: " + not.bundle.getString("android.title") + " Message: " +
                        not.bundle.getString("android.text"));
            }
        });
    }

    NotificationWear aux;
    String groupId = "1639242966291082";

    @OnClick(R.id.post_to_group) void postToGroup(){
        List<String> perm = new ArrayList<String>();
        perm.add("publish_actions");
        perm.add("groups");
        AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
                getResources().getString(R.string.facebook_app_id),
                Profile.getCurrentProfile().getId(),
                perm,
                null,
                null, null, null
        );
        //getResources().getString(R.string.facebook_app_id)+
        GraphRequest request2 = GraphRequest.newPostRequest(acc,
                groupId+"/photos",
                null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response", graphResponse.toString());
                    }
                });

        Bundle postParams = request2.getParameters();

        byte[] data = null;

        Bitmap bi = BitmapFactory.decodeFile("/sdcard/Download/poza.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();
        postParams.putByteArray("picture", data);
        postParams.putString("message", "my picture");

        request2.setParameters(postParams);
        request2.executeAsync();
    }

    @OnClick(R.id.delete_group) void deleteGroup(){
        List<String> perm = new ArrayList<String>();
        perm.add("publish_actions");
        perm.add("groups");
        AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
                getResources().getString(R.string.facebook_app_id),
                fbid,
                perm,
                null,
                null, null, null
        );
        final ArrayList<String> arrayList = new ArrayList<String>();
        GraphRequest request = new GraphRequest(acc,
                "/457164787776875/members",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d("Response", graphResponse.toString());
                        try {
                            JSONArray jsonArray =  graphResponse.getJSONObject().getJSONArray("data");
                            for(int i = 0; i < jsonArray.length();i++){
                                arrayList.add(jsonArray.getJSONObject(i).get("id").toString());

                                deleteFromGroupAux(jsonArray.getJSONObject(i).get("id").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
        GraphRequest request2 = new GraphRequest(acc,
                getResources().getString(R.string.facebook_app_id)+"/groups"+"/"+"457164787776875",
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d("Response", graphResponse.toString());
            }
        });

        request2.executeAsync();
    }

    @OnClick(R.id.create_group) void createGroup(){
        List<String> perm = new ArrayList<String>();
        perm.add("publish_actions");
        perm.add("groups");
        AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
                getResources().getString(R.string.facebook_app_id),
                Profile.getCurrentProfile().getId(),
                perm,
                null,
                null, null, null
        );
        GraphRequest request2 = GraphRequest.newPostRequest(acc,
                "1558949984366781/groups", null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response", graphResponse.toString());
                    }
                });

        Bundle postParams = request2.getParameters();
        postParams.putString("name", "TestGroup");
        request2.setParameters(postParams);
        request2.executeAsync();
    }

    @OnClick(R.id.add_to_group) void addToGroup(){
        List<String> perm = new ArrayList<String>();
        perm.add("publish_actions");
        perm.add("groups");
        AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
                getResources().getString(R.string.facebook_app_id),
                Profile.getCurrentProfile().getId(),
                perm,
                null,
                null, null, null
        );
        GraphRequest request2 = GraphRequest.newPostRequest(acc,
                groupId+"/members", null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response", graphResponse.toString());
                    }
                });

        Bundle postParams = request2.getParameters();
        postParams.putString("member", fbid);
        request2.setParameters(postParams);
        request2.executeAsync();
    }

    @OnClick(R.id.delete_from_group) void deleteFromGroup(){
        deleteFromGroupAux(fbid);

    }

    public void deleteFromGroupAux(String id){
        List<String> perm = new ArrayList<String>();
        perm.add("publish_actions");
        perm.add("groups");
        AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
                getResources().getString(R.string.facebook_app_id),
                fbid,
                perm,
                null,
                null, null, null
        );

        GraphRequest request2 = new GraphRequest(acc,
                groupId+"/members",null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d("Response", graphResponse.toString());
            }
        });

        Bundle postParams = request2.getParameters();
        postParams.putString("member", id);
        request2.setParameters(postParams);
        request2.executeAsync();
    }

    @OnClick(R.id.reply) void replyToLastNotification(){

//        if(notificationsStack.isEmpty()){
//            Toast.makeText(ChatActivity.this, "No notification!", Toast.LENGTH_LONG).show();
//            return;
//        }
        NotificationWear notificationWear = null;


        if(!notificationsStack.isEmpty()) {
            notificationWear = notificationsStack.pop();
            aux = notificationWear;
        }else{
            notificationWear = aux;
        }

        if(notificationWear == null) {
            Toast.makeText(ChatActivity.this, "No notification!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!notificationWear.packageName.equals("com.facebook.orca") ||
                !notificationWear.bundle.getString("android.title").equals(name)){
            return;
        }
        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.remoteInputs.size()];

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle localBundle = notificationWear.bundle;
        int i = 0;
        for(RemoteInput remoteIn : notificationWear.remoteInputs){
            remoteInputs[i] = remoteIn;
            EditText send_text = (EditText)findViewById(R.id.send_text);
            if(  !send_text.getText().toString().matches("") ) {
                localBundle.putCharSequence(remoteInputs[i].getResultKey(), send_text.getText().toString());
                send_text.setText("");
                i++;
            }
        }

        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
        try {
            notificationWear.pendingIntent.send(ChatActivity.this, 0, localIntent);

        } catch (PendingIntent.CanceledException e) {
            Log.e("ChatActivity", "replyToLastNotification error: " + e.getLocalizedMessage());
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)){
            //As we unregister only onDestroy
            EventBus.getDefault().register(this);
        }
        if(Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners") != null) {
            if (Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
                //service is enabled do nothing
            } else {
                //service is not enabled try to enabled
                getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else {
            Log.d("onResume", "onResume no Google Play Services");
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
