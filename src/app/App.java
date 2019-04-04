package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * App
 */
public class App {
    public static void main(String[] args) throws IOException {
        String[] chars = getChars("src/app/chars.txt");
        ASCIIGenerator generator = new ASCIIGenerator(chars, "src/app/east.jpg", 20);
        generator.convert();
    }

    public static String[] getChars(String path) throws IOException {
        return Files.lines(Paths.get(path)).filter(n -> !n.matches("//.+")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).chars().mapToObj(n -> String.valueOf((char) n)).collect(Collectors.toList()).toArray(new String[0]);
    }
}