trait TransactionLoader {
  final val defaultDatetimePattern = "dd/MM/yyyy HH:mm:ss"
  def load(): List[Transaction]
}
