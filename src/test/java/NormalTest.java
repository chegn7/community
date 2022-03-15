import org.junit.jupiter.api.Test;

public class NormalTest {
    @Test
    public void test1() {
        String fileName = "asfjdlwe.jpg.png";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
