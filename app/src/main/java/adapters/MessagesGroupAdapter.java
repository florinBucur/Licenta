package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bucur.licenta.R;

import java.util.List;

import items.FeedMessageGroupItem;
import items.MessagesGroupItems;

/**
 * Created by bucur on 6/18/2015.
 */
public class MessagesGroupAdapter extends RecyclerView.Adapter<MessagesGroupItems>{
    private List<FeedMessageGroupItem> feedItemsList;
    private Context context;

    public MessagesGroupAdapter(Context context, List<FeedMessageGroupItem> list){
        this.feedItemsList = list;
        this.context = context;
    }

    @Override
    public MessagesGroupItems onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, null);
        MessagesGroupItems messagesGroupItems = new MessagesGroupItems(v);
        return messagesGroupItems;
    }

    @Override
    public void onBindViewHolder(MessagesGroupItems holder, int position) {
        final FeedMessageGroupItem feedItem = feedItemsList.get(position);
        if (feedItem != null) {

            holder.message.setText(feedItem.getName());

        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemsList ? feedItemsList.size() : 0);
    }
}
