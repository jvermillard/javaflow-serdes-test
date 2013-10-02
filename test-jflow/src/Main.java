import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.javaflow.Continuation;
import org.apache.commons.javaflow.ContinuationClassLoader;

public class Main {

    public static void main(String[] args) throws Exception {
        System.err.println("bleh");
        URLClassLoader parent = (URLClassLoader) Thread.currentThread().getContextClassLoader();

        final ContinuationClassLoader cl = new ContinuationClassLoader(new URL[] { new URL(
                "file:///home/jvermillar/workspace-indigo/test-jflow/task.jar") }, parent);

        Continuation c;
        File f = new File("task.ser");
        if (!f.exists()) {
            // create the continuation
            @SuppressWarnings("rawtypes")
            Class clazz = cl.loadClass("MyLongTask");
            Runnable task = (Runnable) clazz.newInstance();

            c = Continuation.startSuspendedWith(task);

        } else {
            System.err.println("loading the task from disk");

            // un-serialize the continuation

            try (FileInputStream fis = new FileInputStream(f)) {
                ObjectInputStream ois = new ObjectInputStream(fis) {
                    @Override
                    protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc)
                            throws java.io.IOException, ClassNotFoundException {
                        return cl.loadClass(desc.getName());

                    }
                };
                c = (Continuation) ois.readObject();
            }
        }

        c = Continuation.continueWith(c, new Event());

        if (c == null) {
            System.err.println("we are done!");
            f.delete();
        } else {
            // serialize

            try (FileOutputStream fos = new FileOutputStream(f)) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(c);
                oos.close();
            }
        }
        System.err.println("done!");
    }
}
