package data;

import domain.Store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Simple repository that saves and loads the Store via Java serialization.
 */
public class StoreRepository {
    private final File file;

    public StoreRepository(String path) {
        this.file = new File(path);
        // ensure parent exists
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
    }

    public void save(Store store) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(store);
        }
    }

    public Store load() throws Exception {
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Store) ois.readObject();
        }
    }
}
