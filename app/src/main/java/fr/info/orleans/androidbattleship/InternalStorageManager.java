package fr.info.orleans.androidbattleship;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorageManager {

    private InternalStorageManager() {

    }

    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fileOutputStream = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static Object readObject(Context context, String key)
            throws IOException, ClassNotFoundException {
        FileInputStream fileOutputStream = context.openFileInput(key);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileOutputStream);
        Object object = objectInputStream.readObject();
        return object;
    }

    public static boolean containsObject(Context context, String key) {
        String[] keys = context.fileList();
        for (int i = 0; i < keys.length; i++)
            if (keys[i].equals(key))
                return true;
        return false;
    }

}
