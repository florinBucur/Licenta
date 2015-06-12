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
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.FriendsAdapter;
import items.FeedFriendItem;


public class FriendsActivity extends Activity {
    TextView currentuser;
    TextView friend;
    private List<FeedFriendItem> feedItemList = new ArrayList<FeedFriendItem>();

    private RecyclerView mRecyclerView;

    private FriendsAdapter adapter;




    public FriendsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_friends);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        Intent intent = getIntent();
        if (intent != null){
//
//            List<String> perm = new ArrayList<String>();
//            perm.add("publish_actions");
//            perm.add("groups");
//            AccessToken acc = new AccessToken("1558949984366781|UmJ1BCrCYS5A8Kw-SedEm8aOAis",
//                    "1558949984366781",
//                    Profile.getCurrentProfile().getId(),
//                    perm,
//                    null,
//                    null, null, null
//                    );
//            GraphRequest request2 = GraphRequest.newPostRequest(acc,
//                    "1639242966291082/members", null, new GraphRequest.Callback() {
//                        @Override
//                        public void onCompleted(GraphResponse graphResponse) {
//                            Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
//                            Log.d("Response", graphResponse.toString());
//                        }
//                    });
//
//            Bundle postParams = request2.getParameters();
//
//            postParams.putString("member", "887614657968770");
//
//            request2.setParameters(postParams);
//
//            request2.executeAsync();

//
//            GraphRequest request2 = GraphRequest.newPostRequest(acc,
//                    "1558949984366781/groups", null, new GraphRequest.Callback() {
//                        @Override
//                        public void onCompleted(GraphResponse graphResponse) {
//                            Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
//                            Log.d("Response", graphResponse.toString());
//                        }
//                    });
//
//            Bundle postParams = request2.getParameters();
//
//            postParams.putString("name", "TestGroup");
//
//            request2.setParameters(postParams);
//
//            request2.executeAsync();

//            GraphRequest request2 = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
//                    "me/feed", null, new GraphRequest.Callback() {
//                        @Override
//                        public void onCompleted(GraphResponse graphResponse) {
//                            //Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_LONG).show();
//                            Log.d("Response", graphResponse.toString());
//                        }
//                    });
//
//            Bundle postParams = request2.getParameters();
//
//            postParams.putString("message", "Test message");
//
//            request2.setParameters(postParams);
//
//            request2.executeAsync();

            //setProgressBarIndeterminateVisibility(true);
            GraphRequestBatch batch = new GraphRequestBatch(
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {
                                    // Application code for users friends
                                    System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                    System.out.println("getFriendsData onCompleted : response " + response);

                                    if (null == feedItemList) {
                                        feedItemList = new ArrayList<FeedFriendItem>();
                                    }

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            FeedFriendItem item = new FeedFriendItem();

                                            item.setFbid(jsonArray.getJSONObject(i).getString("id"));

                                            item.setName(jsonArray.getJSONObject(i).getString("name"));
                                            feedItemList.add(item);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    try {
                                        JSONObject jsonObject = response.getJSONObject();
                                        System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);

                                        JSONObject summary = jsonObject.getJSONObject("summary");
                                        System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })

            );
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                    FriendsAdapter mAdapter = new FriendsAdapter(getApplicationContext(),feedItemList);

                    recyclerView.setAdapter(mAdapter);

                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                }
            });

            batch.executeAsync();

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d("Response", object.toString());
//                            FeedFriendItem item = new FeedFriendItem();
//
//                            try {
//                                item.setFbid(object.getString("id"));
//                                item.setName(object.getString("first_name")+" "+object.getString("last_name"));
//                                feedItemList.add(item);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }


                        }
                    });

            request.executeAsync();



            /*
            currentuser = (TextView)findViewById(R.id.current_user);
            currentuser.setText(intent.getStringExtra("Username").toString());
            GraphRequestBatch batch = new GraphRequestBatch(
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {
                                    // Application code for users friends
                                    System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                    System.out.println("getFriendsData onCompleted : response " + response);
                                    Log.d("Me: ", Profile.getCurrentProfile().getName());
                                    String fr = "";
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        try {

                                            fr = fr + jsonArray.getJSONObject(i).getString("name") + " ";

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    friend = (TextView) findViewById(R.id.friend);
                                    friend.setText("Friend: " + fr);
                                    Log.d("Friend: ", fr);

                                    try {
                                        JSONObject jsonObject = response.getJSONObject();
                                        System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);
                                        friend = (TextView)findViewById(R.id.friend);
                                        friend.setText("Friend: "+jsonObject.getString("name"));
                                        JSONObject summary = jsonObject.getJSONObject("summary");
                                        System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })

            );
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    // Application code for when the batch finishes
                }
            });
            batch.executeAsync();

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d("Response", object.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("chats", "Ion Vasile");
            request.setParameters(parameters);
            request.executeAsync();

            */
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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
