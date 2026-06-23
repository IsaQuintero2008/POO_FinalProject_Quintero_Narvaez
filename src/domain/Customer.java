package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer of the video game store.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<VideoGame> purchaseHistory = new ArrayList<>();

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<VideoGame> getPurchaseHistory() { return purchaseHistory; }

    public void addPurchase(VideoGame game) { purchaseHistory.add(game); }

    @Override
    public String toString() { return name + " (" + id + ")"; }
}
