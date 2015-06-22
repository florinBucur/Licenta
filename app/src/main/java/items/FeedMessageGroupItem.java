package items;

/**
 * Created by bucur on 6/18/2015.
 */
public class FeedMessageGroupItem {
    public String id;
    public String name;

    public FeedMessageGroupItem(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return id + " - " + name;
    }
}
