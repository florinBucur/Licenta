package items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bucur.licenta.R;

/**
 * Created by bucur on 6/18/2015.
 */
public class MessagesGroupItems extends RecyclerView.ViewHolder {

    public TextView message;

    public MessagesGroupItems(View itemView) {
        super(itemView);
        this.message = (TextView)itemView.findViewById(R.id.message_from_group);
    }
}
