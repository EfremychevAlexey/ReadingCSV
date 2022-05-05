import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static final String path = "src\\main\\resources\\movementList.csv";

    public static void main(String[] args) throws IOException {
        new Movements(path);
    }
}
