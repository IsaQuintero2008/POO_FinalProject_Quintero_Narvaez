package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main domain class that holds collections of games, customers, employees and sales.
 */
public class Store implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<VideoGame> games = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Sale> sales = new ArrayList<>();

    public Store() {}

    // Game management
    public void addGame(VideoGame game) { games.add(game); }
    public List<VideoGame> listGames() { return games; }
    public VideoGame findGameById(String id) {
        return games.stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
    }
    public boolean removeGame(String id) {
        return games.removeIf(g -> g.getId().equals(id));
    }

    // Customer management
    public void addCustomer(Customer c) { customers.add(c); }
    public List<Customer> listCustomers() { return customers; }
    public Customer findCustomerById(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    // Employee management
    public void addEmployee(Employee e) { employees.add(e); }
    public List<Employee> listEmployees() { return employees; }
    public Employee findEmployeeById(String id) {
        return employees.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    // Sales management
    public void addSale(Sale s) {
        sales.add(s);
        // update customer purchase history
        s.getCustomer().addPurchase(s.getGame());
    }
    public List<Sale> listSales() { return sales; }

    /**
     * Basic recommender: recommends games similar to the viewed game based on category and price range,
     * and also leverages customer's purchase history (simple content-based + popularity).
     */
    public List<VideoGame> recommendGames(Customer customer, VideoGame viewed, int limit) {
        if (viewed == null) return new ArrayList<>();

        List<VideoGame> byCategory = games.stream()
            .filter(g -> !g.getId().equals(viewed.getId()))
            .filter(g -> g.getCategory().getName().equalsIgnoreCase(viewed.getCategory().getName()))
            .collect(Collectors.toList());

        double lower = viewed.getPrice() * 0.7;
        double upper = viewed.getPrice() * 1.3;

        List<VideoGame> byPrice = games.stream()
            .filter(g -> !g.getId().equals(viewed.getId()))
            .filter(g -> g.getPrice() >= lower && g.getPrice() <= upper)
            .collect(Collectors.toList());

        // Merge prioritizing category, then price, then others
        List<VideoGame> results = new ArrayList<>();
        for (VideoGame g : byCategory) if (!results.contains(g)) results.add(g);
        for (VideoGame g : byPrice) if (!results.contains(g)) results.add(g);

        // Add some popular games (most purchased) from customer data (simple frequency)
        List<VideoGame> popular = customers.stream()
            .flatMap(c -> c.getPurchaseHistory().stream())
            .collect(Collectors.groupingBy(g -> g, Collectors.counting())).entrySet().stream()
            .sorted((e1,e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .map(e -> e.getKey()).collect(Collectors.toList());

        for (VideoGame g : popular) if (!results.contains(g) && !g.getId().equals(viewed.getId())) results.add(g);

        return results.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Calculate total revenue from all sales.
     */
    public double calculateTotalRevenue() {
        return sales.stream().mapToDouble(Sale::getFinalPrice).sum();
    }

    /**
     * Calculate revenue by category.
     */
    public java.util.Map<String, Double> calculateRevenueByCategory() {
        return sales.stream()
            .collect(Collectors.groupingBy(
                s -> s.getGame().getCategory().getName(),
                Collectors.summingDouble(Sale::getFinalPrice)
            ));
    }

    /**
     * Calculate revenue by customer.
     */
    public java.util.Map<String, Double> calculateRevenueByCustomer() {
        return sales.stream()
            .collect(Collectors.groupingBy(
                s -> s.getCustomer().getName(),
                Collectors.summingDouble(Sale::getFinalPrice)
            ));
    }

    /**
     * Generate inventory report: games, quantity sold, category.
     */
    public String generateInventoryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY REPORT ===\n");
        if (games.isEmpty()) { sb.append("No games in inventory.\n"); return sb.toString(); }
        for (VideoGame g : games) {
            long sold = sales.stream().filter(s -> s.getGame().getId().equals(g.getId())).count();
            sb.append("ID: ").append(g.getId()).append(" | Title: ").append(g.getTitle())
                .append(" | Category: ").append(g.getCategory().getName())
                .append(" | Price: $").append(g.getPrice()).append(" | Sold: ").append(sold).append("\n");
        }
        return sb.toString();
    }

    /**
     * Generate sales report by category with total revenue.
     */
    public String generateSalesByCategoryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SALES BY CATEGORY ===\n");
        java.util.Map<String, Double> byCategory = calculateRevenueByCategory();
        if (byCategory.isEmpty()) { sb.append("No sales yet.\n"); return sb.toString(); }
        for (String cat : byCategory.keySet()) {
            sb.append(cat).append(": $").append(String.format("%.2f", byCategory.get(cat))).append("\n");
        }
        return sb.toString();
    }

    /**
     * Generate sales report by customer with total spent.
     */
    public String generateSalesByCustomerReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SALES BY CUSTOMER ===\n");
        java.util.Map<String, Double> byCustomer = calculateRevenueByCustomer();
        if (byCustomer.isEmpty()) { sb.append("No sales yet.\n"); return sb.toString(); }
        for (String cust : byCustomer.keySet()) {
            sb.append(cust).append(": $").append(String.format("%.2f", byCustomer.get(cust))).append("\n");
        }
        return sb.toString();
    }

    /**
     * Generate general business report.
     */
    public String generateBusinessReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BUSINESS REPORT ===\n");
        sb.append("Total Sales: ").append(sales.size()).append("\n");
        sb.append("Total Revenue: $").append(String.format("%.2f", calculateTotalRevenue())).append("\n");
        sb.append("Unique Customers: ").append(customers.size()).append("\n");
        sb.append("Games in Inventory: ").append(games.size()).append("\n");
        return sb.toString();
    }
}
