case class FakeTransactionLoader() extends TransactionLoader with  TransactionFixtures {
  override def load(): List[Transaction] = transactionData
}
