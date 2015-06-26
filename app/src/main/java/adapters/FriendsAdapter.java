package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bucur.licenta.R;
import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

import items.FeedFriendItem;
import items.FriendsItems;

/**
 * Created by bucur on 6/2/2015.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsItems> {

    private List<FeedFriendItem> feedItemsList;
    private Context context;
    private ProfilePictureView profilePictureView;
    private TextView name;

    public FriendsAdapter(Context context, List<FeedFriendItem> list) {
        this.feedItemsList = list;
        this.context = context;
    }

    @Override
    public FriendsItems onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_row, null);
        FriendsItems friendsItems = new FriendsItems(v);
        return friendsItems;
    }

    @Override
    public void onBindViewHolder(FriendsItems friendsItems, int i) {
        final FeedFriendItem feedItem = feedItemsList.get(i);
        if (feedItem != null) {
            friendsItems.profilePictureView.setProfileId(feedItem.getFbid());
            friendsItems.name.setText(feedItem.getName());
            friendsItems.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("com.bucur.licenta.intent.action.ChatActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Id", feedItem.getFbid());

                    intent.putExtra("Name", feedItem.getName());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemsList ? feedItemsList.size() : 0);
    }
}
