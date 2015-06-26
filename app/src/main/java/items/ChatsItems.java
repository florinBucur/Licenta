package items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bucur.licenta.R;

/**
 * Created by bucur on 6/22/2015.
 */
public class ChatsItems extends RecyclerView.ViewHolder{

    public TextView name;
    public TextView message;

    public ChatsItems(View itemView) {
        super(itemView);
        this.name = (TextView)itemView.findViewById(R.id.friend_name);
        this.message = (TextView)itemView.findViewById(R.id.chat_message);
    }
}
