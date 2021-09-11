object TransactionType extends Enumeration {
  type TransactionType = Value
  final val PAYMENT = Value("PAYMENT")
  final val REVERSAL = Value("REVERSAL")
}
