import java.text.DecimalFormat

fun main(args: Array<String>) {
    println("fromDate (dd/MM/yyyy HH:mm:ss):")
    val fromDate = readLine()
    println("toDate (dd/MM/yyyy HH:mm:ss):")
    val toDate = readLine()
    println("merchant:")
    val merchant = readLine()
    val input = object {}.javaClass.getResourceAsStream("input.csv")
    val result = TransactionRepository(InputStreamTransactionLoader(input))
        .queryByMerchantAndDateRange(
            merchant!!,
            fromDate!!.toLocalDateTime(),
            toDate!!.toLocalDateTime()
        )

    if (result.isEmpty()) {
        println("No transactions found.")
    } else {
        println("Number of transactions = ${result.size}")
        val sum = result.sumOf { it.amount }
        val avg = sum / result.size.toBigDecimal()
        val formattedAvg = DecimalFormat("#0.##").format(avg)
        println("Average Transaction Value = $formattedAvg")
    }
}


