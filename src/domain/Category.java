package domain;

import java.io.Serializable;

/**
 * Represents a category or genre for video games.
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return name; }
}