import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
class TransactionTypeTest {

    @Test
    void verifyTransactionTypePayment() {
        assertThat(TransactionType.valueOf("PAYMENT")).isEqualTo(TransactionType.PAYMENT);
    }

    @Test
    void verifyTransactionTypeReversal() {
        assertThat(TransactionType.valueOf("REVERSAL")).isEqualTo(TransactionType.REVERSAL);
    }
}
