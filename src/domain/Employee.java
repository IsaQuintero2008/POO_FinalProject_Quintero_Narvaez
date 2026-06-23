package domain;

import java.io.Serializable;

/**
 * Represents an employee of the store.
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    public Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() { return name + " (" + id + ")"; }
}