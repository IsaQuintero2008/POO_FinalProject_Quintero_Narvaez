package domain;

import java.io.Serializable;

/**
 * Represents a video game in the store.
 */
public class VideoGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Category category;
    private double price;

    public VideoGame(String id, String title, Category category, double price) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Category getCategory() { return category; }
    public double getPrice() { return price; }

    public void setTitle(String title) { this.title = title; }
    public void setCategory(Category category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return title + " (" + category.getName() + ") - $" + price;
    }
}