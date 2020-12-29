import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
class TransactionTest {

    Transaction instance;

    @BeforeEach
    void setUp() {
        instance = new Transaction("test",
                LocalDateTime.now(),
                BigDecimal.valueOf(5.0),
                "testMerchant",
                TransactionType.PAYMENT,
                "");
    }

    @Test
    void verifyInstance() {
        assertThat(instance.id()).isEqualTo("test");
        assertThat(instance.type()).isEqualTo(TransactionType.PAYMENT);
        assertThat(instance.transactedAt()).isBefore(LocalDateTime.now());
        assertThat(instance.relatedTransactionId()).isEmpty();
        assertThat(instance.amount()).isCloseTo(BigDecimal.valueOf(5.00), Offset.offset(BigDecimal.valueOf(0.01)));
        assertThat(instance.merchantName()).isEqualTo("testMerchant");
    }
}
