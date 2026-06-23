package ui;

import data.StoreRepository;
import domain.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Improved Swing-based UI for the Video Game Store.
 * Provides a menu, search/filter, tabbed details for customers and sales,
 * and dialogs for adding/editing entities.
 */
public class MainWindow extends JFrame {
    private Store store;
    private StoreRepository repo;

    private DefaultListModel<VideoGame> gameListModel = new DefaultListModel<>();
    private JList<VideoGame> gameJList = new JList<>(gameListModel);
    private JTextField searchField = new JTextField(20);

    private DefaultListModel<Customer> customerListModel = new DefaultListModel<>();
    private JList<Customer> customerJList = new JList<>(customerListModel);

    private DefaultListModel<Sale> saleListModel = new DefaultListModel<>();
    private JList<Sale> saleJList = new JList<>(saleListModel);

    public MainWindow(Store store, StoreRepository repo) {
        this.store = store;
        this.repo = repo;
        setTitle("Video Game Store - Admin");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try { repo.save(store); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        createMenuBar();

        JPanel leftPanel = new JPanel(new BorderLayout(6,6));
        JPanel topSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topSearch.add(new JLabel("Search:"));
        topSearch.add(searchField);
        leftPanel.add(topSearch, BorderLayout.NORTH);

        gameJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(gameJList), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel();
        leftButtons.setLayout(new GridLayout(2,2,6,6));
        leftButtons.add(createButton("Add Game", e -> openAddGameDialog()));
        leftButtons.add(createButton("Edit Game", e -> openEditGameDialog()));
        leftButtons.add(createButton("Remove Game", e -> removeSelectedGame()));
        leftButtons.add(createButton("Recommend", e -> showRecommendations()));
        leftPanel.add(leftButtons, BorderLayout.SOUTH);

        // Right: tabs for customers, sales, details
        JTabbedPane tabs = new JTabbedPane();

        // Customers tab
        JPanel customersPanel = new JPanel(new BorderLayout());
        customerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersPanel.add(new JScrollPane(customerJList), BorderLayout.CENTER);
        JPanel custBtns = new JPanel();
        custBtns.add(createButton("Add Customer", e -> openAddCustomerDialog()));
        custBtns.add(createButton("List Purchases", e -> listCustomerPurchases()));
        customersPanel.add(custBtns, BorderLayout.SOUTH);
        tabs.addTab("Customers", customersPanel);

        // Sales tab
        JPanel salesPanel = new JPanel(new BorderLayout());
        saleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesPanel.add(new JScrollPane(saleJList), BorderLayout.CENTER);
        tabs.addTab("Sales", salesPanel);

        // Details tab
        JPanel detailsPanel = new JPanel(new BorderLayout());
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        tabs.addTab("Details", detailsPanel);

        // Reports tab
        JPanel reportsPanel = new JPanel(new BorderLayout(6,6));
        JTextArea reportsArea = new JTextArea();
        reportsArea.setEditable(false);
        reportsPanel.add(new JScrollPane(reportsArea), BorderLayout.CENTER);
        JPanel reportBtns = new JPanel();
        reportBtns.add(createButton("Business Report", e -> {
            reportsArea.setText(store.generateBusinessReport());
        }));
        reportBtns.add(createButton("Sales by Category", e -> {
            reportsArea.setText(store.generateSalesByCategoryReport());
        }));
        reportBtns.add(createButton("Sales by Customer", e -> {
            reportsArea.setText(store.generateSalesByCustomerReport());
        }));
        reportBtns.add(createButton("Inventory", e -> {
            reportsArea.setText(store.generateInventoryReport());
        }));
        reportsPanel.add(reportBtns, BorderLayout.SOUTH);
        tabs.addTab("Reports", reportsPanel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabs);
        split.setDividerLocation(360);
        add(split, BorderLayout.CENTER);

        // Search listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterGames(); }
            @Override public void removeUpdate(DocumentEvent e) { filterGames(); }
            @Override public void changedUpdate(DocumentEvent e) { filterGames(); }
        });

        // Update details when selection changes
        gameJList.addListSelectionListener(e -> {
            VideoGame sel = gameJList.getSelectedValue();
            if (sel == null) detailsArea.setText(""); else {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(sel.getId()).append("\n");
                sb.append("Title: ").append(sel.getTitle()).append("\n");
                sb.append("Category: ").append(sel.getCategory().getName()).append("\n");
                sb.append("Price: $").append(sel.getPrice()).append("\n");
                detailsArea.setText(sb.toString());
            }
        });

        refreshAll();
    }

    private JButton createButton(String text, java.util.function.Consumer<ActionEvent> action) {
        JButton b = new JButton(text);
        b.addActionListener(a -> action.accept(a));
        return b;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> { try { repo.save(store); JOptionPane.showMessageDialog(this, "Saved"); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage()); } });
        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(e -> { try { Store s = repo.load(); if (s != null) { this.store = s; refreshAll(); JOptionPane.showMessageDialog(this, "Loaded"); } } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage()); } });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> { try { repo.save(store); } catch (Exception ex) {} System.exit(0); });
        file.add(save); file.add(load); file.addSeparator(); file.add(exit);
        menuBar.add(file);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, "Video Game Store - Admin UI\nPOO Final Project"));
        help.add(about);
        menuBar.add(help);

        setJMenuBar(menuBar);
    }

    private void refreshAll() {
        refreshGames();
        refreshCustomers();
        refreshSales();
    }

    private void refreshGames() {
        gameListModel.clear();
        for (VideoGame g : store.listGames()) gameListModel.addElement(g);
    }

    private void filterGames() {
        String q = searchField.getText();
        gameListModel.clear();
        for (VideoGame g : store.listGames()) {
            if (q == null || q.isBlank() || g.getTitle().toLowerCase().contains(q.toLowerCase()) || g.getCategory().getName().toLowerCase().contains(q.toLowerCase())) {
                gameListModel.addElement(g);
            }
        }
    }

    private void refreshCustomers() {
        customerListModel.clear();
        for (Customer c : store.listCustomers()) customerListModel.addElement(c);
    }

    private void refreshSales() {
        saleListModel.clear();
        for (Sale s : store.listSales()) saleListModel.addElement(s);
    }

    private void openAddGameDialog() {
        GameDialog dlg = new GameDialog(this, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            store.addGame(dlg.getGame());
            refreshAll();
        }
    }

    private void openEditGameDialog() {
        VideoGame sel = gameJList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Select a game to edit"); return; }
        GameDialog dlg = new GameDialog(this, sel);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            // changes are applied directly to selected game
            refreshAll();
        }
    }

    private void removeSelectedGame() {
        VideoGame sel = gameJList.getSelectedValue();
        if (sel == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "Remove game " + sel.getTitle() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            store.removeGame(sel.getId());
            refreshAll();
        }
    }

    private void openAddCustomerDialog() {
        CustomerDialog dlg = new CustomerDialog(this);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            store.addCustomer(dlg.getCustomer());
            refreshAll();
        }
    }

    private void listCustomerPurchases() {
        Customer c = customerJList.getSelectedValue();
        if (c == null) { JOptionPane.showMessageDialog(this, "Select a customer"); return; }
        StringBuilder sb = new StringBuilder();
        for (VideoGame g : c.getPurchaseHistory()) sb.append(g).append("\n");
        JOptionPane.showMessageDialog(this, sb.length() == 0 ? "No purchases" : sb.toString(), "Purchases of " + c.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    public void registerSale() {
        VideoGame sel = gameJList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Select a game first"); return; }

        String customerId = JOptionPane.showInputDialog(this, "Customer ID (existing or new):");
        if (customerId == null || customerId.isBlank()) return;
        Customer c = store.findCustomerById(customerId);
        if (c == null) {
            String name = JOptionPane.showInputDialog(this, "Customer Name:");
            if (name == null || name.isBlank()) return;
            c = new Customer(customerId, name);
            store.addCustomer(c);
        }

        Employee emp = store.listEmployees().isEmpty() ? new Employee("E1","Default") : store.listEmployees().get(0);
        if (store.listEmployees().isEmpty()) store.addEmployee(emp);

        String saleId = "S" + (store.listSales().size() + 1);
        Sale sale = new Sale(saleId, c, sel, emp, sel.getPrice());
        store.addSale(sale);
        refreshAll();
        JOptionPane.showMessageDialog(this, "Sale registered: " + sale);
    }

    private void showRecommendations() {
        VideoGame sel = gameJList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Select a game first"); return; }
        String customerId = JOptionPane.showInputDialog(this, "Customer ID (optional):");
        Customer c = null;
        if (customerId != null && !customerId.isBlank()) c = store.findCustomerById(customerId);

        List<VideoGame> recs = store.recommendGames(c, sel, 5);
        if (recs.isEmpty()) JOptionPane.showMessageDialog(this, "No recommendations available");
        else {
            StringBuilder sb = new StringBuilder();
            for (VideoGame g : recs) sb.append(g).append("\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Recommendations", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Dialogs
    private static class GameDialog extends JDialog {
        private JTextField idField = new JTextField(20);
        private JTextField titleField = new JTextField(20);
        private JTextField categoryField = new JTextField(20);
        private JTextField priceField = new JTextField(10);
        private boolean saved = false;
        private VideoGame game;

        public GameDialog(Frame owner, VideoGame existing) {
            super(owner, true);
            setTitle(existing == null ? "Add Game" : "Edit Game");
            setLayout(new BorderLayout(6,6));
            JPanel form = new JPanel(new GridLayout(4,2,6,6));
            form.add(new JLabel("ID:")); form.add(idField);
            form.add(new JLabel("Title:")); form.add(titleField);
            form.add(new JLabel("Category:")); form.add(categoryField);
            form.add(new JLabel("Price:")); form.add(priceField);
            add(form, BorderLayout.CENTER);
            JPanel btns = new JPanel();
            JButton saveBtn = new JButton("Save");
            saveBtn.addActionListener(e -> onSave());
            JButton cancelBtn = new JButton("Cancel"); cancelBtn.addActionListener(e -> dispose());
            btns.add(saveBtn); btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);

            if (existing != null) {
                this.game = existing;
                idField.setText(existing.getId()); idField.setEditable(false);
                titleField.setText(existing.getTitle()); categoryField.setText(existing.getCategory().getName()); priceField.setText(String.valueOf(existing.getPrice()));
            }
        }

        private void onSave() {
            String id = idField.getText(); String title = titleField.getText(); String cat = categoryField.getText(); String p = priceField.getText();
            if (id.isBlank() || title.isBlank() || cat.isBlank() || p.isBlank()) { JOptionPane.showMessageDialog(this, "All fields required"); return; }
            double price = 0;
            try { price = Double.parseDouble(p); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid price"); return; }
            if (game == null) game = new VideoGame(id, title, new Category(cat, ""), price); else { game.setTitle(title); game.setCategory(new Category(cat, "")); game.setPrice(price); }
            saved = true; dispose();
        }

        public boolean isSaved() { return saved; }
        public VideoGame getGame() { return game; }
    }

    private static class CustomerDialog extends JDialog {
        private JTextField idField = new JTextField(20);
        private JTextField nameField = new JTextField(20);
        private boolean saved = false;
        private Customer customer;

        public CustomerDialog(Frame owner) {
            super(owner, true);
            setTitle("Add Customer");
            setLayout(new BorderLayout(6,6));
            JPanel form = new JPanel(new GridLayout(2,2,6,6));
            form.add(new JLabel("ID:")); form.add(idField);
            form.add(new JLabel("Name:")); form.add(nameField);
            add(form, BorderLayout.CENTER);
            JPanel btns = new JPanel();
            JButton saveBtn = new JButton("Save"); saveBtn.addActionListener(e -> onSave());
            JButton cancelBtn = new JButton("Cancel"); cancelBtn.addActionListener(e -> dispose());
            btns.add(saveBtn); btns.add(cancelBtn);
            add(btns, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }

        private void onSave() {
            String id = idField.getText(); String name = nameField.getText();
            if (id.isBlank() || name.isBlank()) { JOptionPane.showMessageDialog(this, "All fields required"); return; }
            customer = new Customer(id, name); saved = true; dispose();
        }

        public boolean isSaved() { return saved; }
        public Customer getCustomer() { return customer; }
    }
}
