package POO_FinalProject_Quintero_Narvaez;

import data.StoreRepository;
import domain.Store;
import ui.MainWindow;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 */
public class Main {
    public static void main(String[] args) {
        String dataPath = "data/store.dat";
        StoreRepository repo = new StoreRepository(dataPath);
        Store store = null;
        try {
            store = repo.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (store == null) {
            store = new Store();
            // seed some data
            seed(store);
            try { repo.save(store); } catch (Exception e) { e.printStackTrace(); }
        }

        final Store finalStore = store;
        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow(finalStore, repo);
            w.setVisible(true);
        });
    }

    private static void seed(Store store) {
        domain.Category action = new domain.Category("Action", "Action games");
        domain.Category rpg = new domain.Category("RPG", "Role playing games");
        store.addGame(new domain.VideoGame("G1","Fast Fury", action, 39.99));
        store.addGame(new domain.VideoGame("G2","Hero Saga", rpg, 59.99));
        store.addGame(new domain.VideoGame("G3","Space Runner", action, 29.99));
        store.addGame(new domain.VideoGame("G4","Mystic Quest", rpg, 49.99));

        store.addEmployee(new domain.Employee("E1","Alice"));
    }
}
