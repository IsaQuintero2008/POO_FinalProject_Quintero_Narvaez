package domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a sale transaction.
 */
public class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Customer customer;
    private VideoGame game;
    private Employee employee;
    private LocalDateTime dateTime;
    private double finalPrice;

    public Sale(String id, Customer customer, VideoGame game, Employee employee, double finalPrice) {
        this.id = id;
        this.customer = customer;
        this.game = game;
        this.employee = employee;
        this.finalPrice = finalPrice;
        this.dateTime = LocalDateTime.now();
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public VideoGame getGame() { return game; }
    public Employee getEmployee() { return employee; }
    public LocalDateTime getDateTime() { return dateTime; }
    public double getFinalPrice() { return finalPrice; }

    @Override
    public String toString() {
        return dateTime + " - " + customer.getName() + " bought " + game.getTitle() + " for $" + finalPrice;
    }
}
