package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bucur.licenta.R;

import java.util.List;

import items.ChatsItems;
import items.FeedChatItem;

/**
 * Created by bucur on 6/22/2015.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatsItems>{


    private List<FeedChatItem> feedItemsList;
    private Context context;
    private TextView message;
    private TextView name;

    public ChatAdapter(Context context, List<FeedChatItem> list){
        this.feedItemsList = list;
        this.context = context;
    }

    @Override
    public ChatsItems onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, null);
        ChatsItems chatsItems = new ChatsItems(v);
        return chatsItems;
    }

    @Override
    public void onBindViewHolder(ChatsItems holder, int position) {
        final FeedChatItem feedItem = feedItemsList.get(position);
        if (feedItem != null) {
            holder.name.setText(feedItem.getName());
            holder.message.setText(feedItem.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemsList ? feedItemsList.size() : 0);
    }

    public void add(FeedChatItem item, int position) {
        feedItemsList.add(position, item);
        notifyItemInserted(position);

    }

    public void remove(FeedChatItem item) {
        int position = feedItemsList.indexOf(item);
        feedItemsList.remove(position);
        notifyItemRemoved(position);
    }
}
