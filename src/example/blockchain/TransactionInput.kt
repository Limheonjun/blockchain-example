package example.blockchain

//TransactionOutput중에서 아직 사용되지 않은 것들을 기록하는 클래스
data class TransactionInput(
    val transactionOutputId: String?
) {
    var UTXO: TransactionOutput? = null
}