package items;

/**
 * Created by bucur on 6/22/2015.
 */
public class FeedChatItem {
    private String name;
    private String message;

    public FeedChatItem(String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
