package com.bucur.licenta;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.RemoteInput;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import adapters.ChatAdapter;
import adapters.GroupAdapter;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import items.FeedChatItem;
import items.FeedGroupItem;
import notifications.NotificationWear;

/**
 * Created by bucur on 6/2/2015.
 */
public class ChatActivity extends Activity {

    private ProfilePictureView profilePictureView;
    private TextView receiveText;
    private EditText sendText;
    private Button reply;
    private Button initiate;
    private Button post;
    private Button deleteGroup;
    private Button createGroup;
    private Button deleteFromGroup;
    private Button postToGroup;
    private EditText postToGroupEditText;
    private EditText groupName;
    private Button postImageToGroup;
    private Button chooseImage;

    CallbackManager callbackManager;
    private Stack<NotificationWear> notificationsStack = new Stack<NotificationWear>();
    private ShareDialog shareDialog;
    private String name;

    String fbid;
    ArrayList<FeedGroupItem> groupList;
    ArrayList<FeedChatItem> chatList;
    AccessToken appAccessToken;
    String appToken = "1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis";
    String appId = "1558949984366781";
    RecyclerView recyclerView;
    GroupAdapter mAdapter;
    ChatAdapter chatAdapter;
    RecyclerView chatRecyclerView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.chat_activity);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        profilePictureView = (ProfilePictureView) findViewById(R.id.friend_picture_chat);
        //receiveText = (TextView) findViewById(R.id.notification);
        sendText = (EditText) findViewById(R.id.send_text);
        reply = (Button) findViewById(R.id.reply);
        initiate = (Button) findViewById(R.id.initiate);
        post = (Button) findViewById(R.id.add_to_group);
        createGroup = (Button) findViewById(R.id.create_group);
        deleteFromGroup = (Button) findViewById(R.id.delete_from_group);
        deleteGroup = (Button) findViewById(R.id.delete_group);
        //postToGroup = (Button) findViewById(R.id.post_to_group);
        groupList = new ArrayList<FeedGroupItem>();
        groupName = (EditText) findViewById(R.id.group_name);
        //postToGroupEditText = (EditText) findViewById(R.id.message_to_post);
        //postImageToGroup = (Button) findViewById(R.id.post_image_to_group);
        //chooseImage = (Button) findViewById(R.id.choose_image);


        final Intent intent = getIntent();

        if (intent != null) {
            recyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

            chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
            chatList = new ArrayList<FeedChatItem>();

            chatAdapter = new ChatAdapter(getApplicationContext(), chatList);

            chatRecyclerView.setAdapter(chatAdapter);

            chatRecyclerView.setItemAnimator(new DefaultItemAnimator());

            fbid = intent.getStringExtra("Id");
            profilePictureView.setProfileId(fbid);
            ButterKnife.inject(this);
            name = intent.getStringExtra("Name");

            initiate = (Button) findViewById(R.id.initiate);
            initiate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging"));
                    ChatActivity.this.startActivityForResult(intent, 0);
                }
            });

            List<String> perm = new ArrayList<String>();
            perm.add("publish_actions");
            perm.add("groups");

            appAccessToken = new AccessToken(appToken,
                    appId,
                    Profile.getCurrentProfile().getId(),
                    perm,
                    null,
                    null, null, null
            );

            getGroups();
        }

    }

//    @OnClick(R.id.post_image_to_group)
//    void postImageToGroup() {
//
//        final InputStream imageStream;
//
//
//        FeedGroupItem grp = getSelectedGroup();
//        GraphRequest request2 = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
//                grp.getId() + "/feed?fields=message",
//                null, new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse graphResponse) {
//                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
//                        Log.d("Response to post", graphResponse.toString());
//                    }
//                });
//
//        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
//                grp.getId() + "/feed",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse graphResponse) {
//                        try {
//                            JSONArray jsonArray = graphResponse.getJSONObject().getJSONArray("data");
//                            JSONObject nextPage = graphResponse.getJSONObject().getJSONObject("paging");
//
//                            Log.d("Response to post", jsonArray.toString());
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//
//        Bundle postParams = request.getParameters();
//        postParams.putString("fields", "message,id");
//            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            Log.d("real path", cursor.getString(idx));
//            //Log.d("Uri",Uri.parse(imageUri.toString()).toString() );
//            Bitmap bi = BitmapFactory.decodeFile(cursor.getString(idx));
//            byte[] data = null;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            data = baos.toByteArray();
//            postParams.putString("name", "Test");
//            postParams.putString("description", "descriere");
//            postParams.putString("caption", "my_picture");
//            postParams.putString("message", "My message");
//            postParams.putByteArray("picture", data);
//
//        request.setParameters(postParams);
//        request.executeAsync();
//
//
//        SharePhoto sharePhoto = new SharePhoto.Builder().
//                setBitmap(bi).
//
//                setCaption("naspa").build();
//        Bundle bundle = sharePhoto.getParameters();
//        bundle.putString(grp.getId() + "/photos", "meh");
//        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
//
//        ShareApi.share(sharePhotoContent, null);
//
//
//    }
//
//    @OnClick(R.id.choose_image)
//    void chooseImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 101);
//    }


    @OnClick(R.id.delete_group)
    void deleteGroup() {
        FeedGroupItem grp = getSelectedGroup();
        deleteGroupMetode(grp);
    }

    @OnClick(R.id.create_group)
    void createGroup() {
        createGroupMetode(groupName.getText().toString());
    }

    @OnClick(R.id.add_to_group)
    void addToGroup() {
        FeedGroupItem grp = getSelectedGroup();
        addToGroupMetode(grp.getId(), fbid);
    }

    @OnClick(R.id.delete_from_group)
    void deleteFromGroup() {
        FeedGroupItem grp = getSelectedGroup();
        deleteFromGroupMetode(grp, fbid);

    }

    public void postToGroupMetode(String id, String message) {
        GraphRequest request2 = GraphRequest.newPostRequest(appAccessToken,
                id + "/feed",
                null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response to post", graphResponse.toString());
                    }
                });

        Bundle postParams = request2.getParameters();
        postParams.putString("message", message);

        request2.setParameters(postParams);
        request2.executeAsync();
    }

    public void deleteGroupMetode(final FeedGroupItem group) {

        GraphRequest request = new GraphRequest(appAccessToken,
                group.getId() + "/members",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d("Response delete member", graphResponse.toString());
                        try {
                            JSONArray jsonArray = graphResponse.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                deleteFromGroupMetode(group, jsonArray.getJSONObject(i).get("id").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();

        GraphRequest request2 = new GraphRequest(appAccessToken,
                appId + "/groups" + "/" + group.getId(),
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        mAdapter.remove(group);
                        Log.d(" delete group", graphResponse.toString());
                    }
                });

        request2.executeAsync();
    }


    public void deleteFromGroupMetode(FeedGroupItem groupItem, String memberId) {

        GraphRequest request2 = new GraphRequest(appAccessToken,
                groupItem.getId() + "/members", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                Log.d(" delete from group", graphResponse.toString());
            }
        });

        Bundle postParams = request2.getParameters();
        postParams.putString("member", memberId);
        request2.setParameters(postParams);
        request2.executeAsync();
    }

    public FeedGroupItem createGroupMetode(final String groupName) {

        final FeedGroupItem[] grp = {null};
        Log.d("create", groupName);
        GraphRequest request2 = GraphRequest.newPostRequest(appAccessToken,
                "1558949984366781/groups", null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        try {
                            Log.d("Response to create", graphResponse.getJSONObject().getString("id"));
                            grp[0] = new FeedGroupItem(graphResponse.getJSONObject().getString("id"), groupName, false);
                            mAdapter.add(grp[0], mAdapter.getItemCount());
                            addToGroupMetode(graphResponse.getJSONObject().getString("id"), Profile.getCurrentProfile().getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle postParams = request2.getParameters();
        postParams.putString("name", groupName);
        postParams.putString("admin", Profile.getCurrentProfile().getId());
        postParams.putString("privacy", "open");
        request2.setParameters(postParams);
        request2.executeAsync();
        return grp[0];
    }

    public void addToGroupMetode(String groupId, String memberId) {
        GraphRequest request2 = GraphRequest.newPostRequest(appAccessToken,
                groupId + "/members", null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        Log.d("Response to Add", graphResponse.toString());
                    }
                });

        Bundle postParams = request2.getParameters();
        postParams.putString("member", memberId);
        request2.setParameters(postParams);
        request2.executeAsync();
    }


    public void getGroups() {
        GraphRequest request2 = new GraphRequest(appAccessToken,
                appId + "/groups",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        try {
                            JSONArray jsonArray = graphResponse.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                FeedGroupItem gr = new FeedGroupItem(jsonArray.getJSONObject(i).get("id").toString(),
                                        jsonArray.getJSONObject(i).get("name").toString(),
                                        false);
                                groupList.add(gr);
                                Log.d("Grup", groupList.get(i).toString());
                            }


                            mAdapter = new GroupAdapter(getApplicationContext(), groupList);

                            recyclerView.setAdapter(mAdapter);

                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle postParams = request2.getParameters();
        request2.setParameters(postParams);
        request2.executeAsync();
    }

    public FeedGroupItem getSelectedGroup() {
        FeedGroupItem grp = null;
        List<FeedGroupItem> groupsListAux = mAdapter.feedItemsList;

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            FeedGroupItem gr = groupsListAux.get(i);
            //Log.d("Selected Group", String.valueOf(gr.isSelected)+" "+gr.getName());
            if (gr.isSelected()) {
                grp = gr;

            }
        }
        return grp;
    }

    AES aes;

    public void onEvent(final NotificationWear notificationWear) {
        String namenot = notificationWear.bundle.getString("android.title");
        System.out.println("Name " + namenot + " - " + name);

        if (notificationWear.packageName.equals("com.facebook.orca") &&
                namenot.equals(name)) {
            notificationsStack.push(notificationWear);

            final NotificationWear not = notificationWear;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onEvent", not.bundle.toString());

                    String from = not.bundle.getString("android.title");
                    String encryptMessage = not.bundle.getString("android.text");

                    String message = "";

                    try {
                        aes = new AES();
                        if (!encryptMessage.equals("Hello"))
                            message = aes.decrypt(encryptMessage, aes.encryptionKey);
                        else
                            message = "Hello";
                        Log.d("Criptat - Necriptat", message + " - " + encryptMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    FeedChatItem feedChatItem = new FeedChatItem(from + ": ", message);
                    chatAdapter.add(feedChatItem, chatAdapter.getItemCount());
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    //TextView textView = (TextView) findViewById(R.id.notification);
                    //textView.setText(from + ": " + message);


                }
            });
        }
    }

    NotificationWear aux;

    @OnClick(R.id.reply)
    void replyToLastNotification() {

        NotificationWear notificationWear = null;

        if (!notificationsStack.isEmpty()) {
            Log.d("Notificatios", "Not Empty");
            notificationWear = notificationsStack.pop();
            aux = notificationWear;
        } else {
            Log.d("Notificatios", "Empty");
            notificationWear = aux;
        }

        if (notificationWear == null) {
            Toast.makeText(ChatActivity.this, "No notification!", Toast.LENGTH_LONG).show();
            return;
        }

        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.remoteInputs.size()];

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle localBundle = notificationWear.bundle;
        int i = 0;
        for (RemoteInput remoteIn : notificationWear.remoteInputs) {
            remoteInputs[i] = remoteIn;
            EditText send_text = (EditText) findViewById(R.id.send_text);
            if (!send_text.getText().toString().matches("")) {

                String msg = String.valueOf(send_text.getText());

                String message = "";

                try {

                    message = AES.encrypt(msg, AES.encryptionKey);

                    Log.d("Criptat - Necriptat", message + " - " + msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                FeedChatItem feedChatItem = new FeedChatItem("Me: ", msg);
                chatAdapter.add(feedChatItem, chatAdapter.getItemCount());
                chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                localBundle.putCharSequence(remoteInputs[i].getResultKey(), message);
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
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    Log.d("Image URI", imageUri.toString());
                }
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }


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

        if (!EventBus.getDefault().isRegistered(this)) {
            //As we unregister only onDestroy
            EventBus.getDefault().register(this);
        }
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners") != null) {
            if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
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
