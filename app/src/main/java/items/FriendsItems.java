package items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bucur.licenta.R;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by bucur on 6/2/2015.
 */
public class FriendsItems extends RecyclerView.ViewHolder{
    public ProfilePictureView profilePictureView;
    public TextView name;

    public FriendsItems(View itemView) {
        super(itemView);
        this.profilePictureView = (ProfilePictureView)itemView.findViewById(R.id.friend_picture_row);
        this.name = (TextView)itemView.findViewById(R.id.friend_name_row);
    }
}
