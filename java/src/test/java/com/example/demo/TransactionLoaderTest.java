package  com.example.demo;
import com.example.demo.InputStreamTransactionLoader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @TestFactory
    List<DynamicTest> testLoadFromFiles() {
        var csvFiles = Map.of(
                "/input_test1.csv", 1,
                "/input_test2.csv", 2,
                "/input_test3.csv", 6
        );

        return csvFiles.keySet().stream()
                .map(key ->
                        dynamicTest("test#" + key, () -> {
                            var data = getClass().getResourceAsStream(key);
                            var loader = new InputStreamTransactionLoader(data);
                            var loadedData = loader.load();
                            assertThat(loadedData.size()).isEqualTo(csvFiles.get(key));
                        })
                )
                .collect(Collectors.toList());

    }

}
