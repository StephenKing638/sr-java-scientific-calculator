package sr.calculator; 

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * @author StephenKing638 (https://github.com/StephenKing638)
 * @since v1.1.0-pre 10-15-24 from sr.control
 */
public class Compressor {

    public static void sendCompressedObject(Object o, File f) throws IOException {
        sendCompressedObject(o, f.toPath());
    }

    public static void sendCompressedObject(Object o, Path p) throws IOException {
        try(OutputStream os = Files.newOutputStream(p, StandardOpenOption.WRITE)) {
            sendCompressedObject(o, os);
        }
    }

    public static void sendCompressedObject(Object o, OutputStream os) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new DeflaterOutputStream(os))) {
            oos.writeObject(o);
        }
    }

    public static <T> T receive(File f) throws IOException {
        return receiveCompressedObject(f.toPath());
    }

    public static <T> T receiveCompressedObject(Path p) throws IOException {
        try(InputStream is = Files.newInputStream(p, StandardOpenOption.READ)) {
            return receiveCompressedObject(is);
        }
    }

    @SuppressWarnings("all")
    public static <T> T receiveCompressedObject(InputStream is) throws IOException {
        try(ObjectInputStream oos = new ObjectInputStream(new InflaterInputStream(is))) {
            return (T) oos.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
