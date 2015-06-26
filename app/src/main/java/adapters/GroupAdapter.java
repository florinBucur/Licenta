package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bucur.licenta.R;

import java.util.List;

import items.FeedGroupItem;
import items.GroupsItems;

/**
 * Created by bucur on 6/12/2015.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupsItems> {

    public List<FeedGroupItem> feedItemsList;
    private Context context;
    private CheckBox checkBox;
    private TextView name;

    public GroupAdapter(Context context, List<FeedGroupItem> list) {
        this.feedItemsList = list;
        this.context = context;
    }

    @Override
    public GroupsItems onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_row, null);
        GroupsItems groupsItems = new GroupsItems(v);
        return groupsItems;
    }


    @Override
    public void onBindViewHolder(final GroupsItems groupsItems, int i) {
        final FeedGroupItem feedItem = feedItemsList.get(i);
        if (feedItem != null) {
            groupsItems.checkBox.setChecked(feedItem.isSelected());
            groupsItems.name.setText(feedItem.getName());
            groupsItems.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    groupsItems.checkBox.setChecked(isChecked);
                    feedItem.setSelected(isChecked);
                }
            });
            groupsItems.openGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("com.bucur.licenta.intent.action.GroupsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Id", feedItem.getId());

                    intent.putExtra("Name", feedItem.getName());
                    context.startActivity(intent);
                }
            });

        }
    }

    public void add(FeedGroupItem item, int position) {
        feedItemsList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(FeedGroupItem item) {
        int position = feedItemsList.indexOf(item);
        feedItemsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemsList ? feedItemsList.size() : 0);
    }
}
