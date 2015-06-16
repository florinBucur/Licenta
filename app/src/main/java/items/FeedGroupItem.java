package items;

/**
 * Created by bucur on 6/14/2015.
 */
public class FeedGroupItem {
    public String id;
    public String name;
    public boolean isSelected;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected(){
        return this.isSelected;
    }

    public FeedGroupItem(String id, String name, boolean isSelected){
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    @Override
    public String toString(){
        return id + " - " + name;
    }
}
