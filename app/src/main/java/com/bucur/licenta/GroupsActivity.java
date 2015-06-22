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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.MessagesGroupAdapter;
import items.FeedMessageGroupItem;


public class GroupsActivity extends Activity {
    String groupId;
    ArrayList<FeedMessageGroupItem> messagesList;
    RecyclerView recyclerView;
    MessagesGroupAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_groups);
        Intent intent = getIntent();
        messagesList = new ArrayList<FeedMessageGroupItem>();
        if(intent != null){
            groupId = intent.getStringExtra("Id");
            recyclerView = (RecyclerView) findViewById(R.id.feed_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(GroupsActivity.this));
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    groupId + "/feed",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            try {
                                JSONArray jsonArray = graphResponse.getJSONObject().getJSONArray("data");

                                for(int i = 0; i < jsonArray.length();i++){
                                    JSONObject data = jsonArray.getJSONObject(i);


                                    if(data.toString().contains("\"message\"")){
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
            postParams.putString("fields", "message,id");

            request.setParameters(postParams);
            request.executeAsync();

        }
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
