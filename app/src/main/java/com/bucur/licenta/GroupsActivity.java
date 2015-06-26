package com.bucur.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.MessagesGroupAdapter;
import butterknife.ButterKnife;
import butterknife.OnClick;
import items.FeedMessageGroupItem;


public class GroupsActivity extends Activity {
    String groupId;
    ArrayList<FeedMessageGroupItem> messagesList;
    RecyclerView recyclerView;
    MessagesGroupAdapter mAdapter;
    EditText postToGroupEditText;
    Button postToGroupButton;
    AccessToken appAccessToken;
    TextView groupName;
    String appToken = "1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis";
    String appId = "1558949984366781";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_groups);
        Intent intent = getIntent();
        messagesList = new ArrayList<FeedMessageGroupItem>();
        postToGroupButton = (Button) findViewById(R.id.post_to_group);
        postToGroupEditText = (EditText) findViewById(R.id.message_to_post);
        groupName = (TextView)findViewById(R.id.group_name_text_view);
        ButterKnife.inject(this);
        if (intent != null) {

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
            groupName.setText(intent.getStringExtra("Name"));
            groupId = intent.getStringExtra("Id");
            recyclerView = (RecyclerView) findViewById(R.id.feed_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(GroupsActivity.this));

            getPosts();

        }
    }

    public void getPosts() {
        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                groupId + "/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        try {
                            JSONArray jsonArray = graphResponse.getJSONObject().getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);


                                if (data.toString().contains("\"message\"")) {
                                    String message = data.getString("message");
                                    String finalMsg = " ";
                                    try {
                                        finalMsg = AES.decrypt(message, AES.encryptionKey);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String id = data.getString("id");
                                    FeedMessageGroupItem msg = new FeedMessageGroupItem(id, finalMsg);
                                    Log.d("msg", msg.toString());
                                    messagesList.add(msg);
                                }

                            }

                            mAdapter = new MessagesGroupAdapter(getApplicationContext(), messagesList);

                            recyclerView.setAdapter(mAdapter);

                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            JSONObject nextPage = graphResponse.getJSONObject().getJSONObject("paging");

                            Log.d("Response to post", jsonArray.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle postParams = request.getParameters();
        postParams.putString("fields", "message,id,from");

        request.setParameters(postParams);
        request.executeAsync();
    }

    @OnClick(R.id.post_to_group)
    void postToGroup() {
        String finalMsg = null;
        try {
            finalMsg = AES.encrypt(postToGroupEditText.getText().toString(), AES.encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GraphRequest request2 = GraphRequest.newPostRequest(appAccessToken,
                groupId + "/feed",
                null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response to post", graphResponse.toString());
                        FeedMessageGroupItem feedMessageGroupItem;
                        try {
                            feedMessageGroupItem = new FeedMessageGroupItem(graphResponse.getJSONObject().getString("id"),
                                    postToGroupEditText.getText().toString());
                            mAdapter.add(feedMessageGroupItem, mAdapter.getItemCount());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle postParams = request2.getParameters();


        postParams.putString("message", finalMsg);

        request2.setParameters(postParams);
        request2.executeAsync();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
