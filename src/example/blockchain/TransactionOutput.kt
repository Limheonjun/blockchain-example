package example.blockchain

import java.security.Key
import java.security.PublicKey

/**
 * 트랜잭션으로부터 각 구성원들에게 보내진 최종 금액을 보여주는 클래스
 * 이 정보들은 새로운 트랜잭션에서는 Input으로써 기록되게 될 것이고, 당신이 보낼 코인이 남아있다는 것을 확인하는 용도로 사용
 * 즉, 나의 트랜잭션 Output은 다른 누군가의 트랜잭션 Input
 */
data class TransactionOutput(
    var reciepient: PublicKey? = null,
    var value: Float = 0f,
    var parentTransactionId: String? = null,
) {
    var id: String? = null

    //Constructor
    fun TransactionOutput(reciepient: PublicKey?, value: Float, parentTransactionId: String) {
        this.reciepient = reciepient
        this.value = value
        this.parentTransactionId = parentTransactionId
        id = Util.Companion.applySha256(Util.Companion.getStringFromKey(reciepient as Key) + java.lang.Float.toString(value) + parentTransactionId)
    }

    //Check if coin belongs to you
    fun isMine(publicKey: PublicKey): Boolean {
        return publicKey === reciepient
    }
}