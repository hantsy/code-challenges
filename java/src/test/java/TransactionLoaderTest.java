import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TransactionLoaderTest {

    @TestFactory
    List<DynamicTest> testLoad() throws IOException {
        return List.of(
                dynamicTest("test1", () -> {
                    var data = """
                            ID, Date, Amount, Merchant, Type, Related Transaction
                            WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
                            YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
                            """;
                    var loader = new InputStreamTransactionLoader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
                    var loadedData = loader.load();
                    assertThat(loadedData.size()).isEqualTo(2);

                }),
                dynamicTest("test2", () -> {
                    var data = """
                            ID, Date, Amount, Merchant, Type, Related Transaction
                            WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
                            """;
                    var loader = new InputStreamTransactionLoader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
                    var loadedData = loader.load();
                    assertThat(loadedData.size()).isEqualTo(1);
                })
        );

    }

}
