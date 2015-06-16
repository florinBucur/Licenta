package items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bucur.licenta.R;

/**
 * Created by bucur on 6/14/2015.
 */
public class GroupsItems extends RecyclerView.ViewHolder{

    public CheckBox checkBox;
    public TextView name;
    public GroupsItems(View itemView) {
        super(itemView);
        this.checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        this.name = (TextView)itemView.findViewById(R.id.group_name);
    }
}
