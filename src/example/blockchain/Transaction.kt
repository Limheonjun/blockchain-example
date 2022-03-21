package example.blockchain

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey


/**
 * Trasaction의 역할
 * 1. 코인의 오너만이 해당 코인을 사용할 수 있게 해줌
 * 2. 새로운 블럭 생성 전 이미 접수된 트랜잭션에 대해 다른 사람들이 수정하지 못하도록 방지하는 기능
 */
data class Transaction(
    var sender: PublicKey? = null,
    var reciepient: PublicKey? = null,
    var value: Float = 0f,
    var signature: ByteArray? = byteArrayOf()
) {
    var transactionId: String? = null
    var inputs: ArrayList<TransactionInput> = ArrayList<TransactionInput>()
    var outputs: ArrayList<TransactionOutput> = ArrayList<TransactionOutput>()

    private var sequence = 0 // a rough count of how many transactions have been generated.

    // This Calculates the transaction hash (which will be used as its Id)
    private fun calulateHash(): String? {
        sequence++ //increase the sequence to avoid 2 identical transactions having the same hash
        return Util.Companion.applySha256(
            Util.Companion.getStringFromKey(sender as Key) +
                    Util.Companion.getStringFromKey(reciepient as Key).toString() +
                    value.toString() + sequence
        )
    }

    fun generateSignature(privateKey: PrivateKey?) {
        val data: String = Util.Companion.getStringFromKey(sender as Key) + Util.Companion.getStringFromKey(reciepient as Key)
            .toString() + value.toString()
        signature = Util.Companion.applyECDSASig(privateKey, data)!!
    }

    fun verifySignature(): Boolean {
        val data: String = Util.Companion.getStringFromKey(sender as Key) + Util.Companion.getStringFromKey(reciepient as Key)
            .toString() + value.toString()
        return Util.Companion.verifyECDSASig(sender, data, signature)
    }

    fun processTransaction(): Boolean {
        if (verifySignature() == false) {
            println("#Transaction Signature failed to verify")
            return false
        }

        //Gathers transaction inputs (Making sure they are unspent):
        for (i in inputs) {
            i.UTXO = UTXOs.get(i.transactionOutputId)
        }

        //Checks if transaction is valid:
        if (getInputsValue() < minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue())
            System.out.println("Please enter the amount greater than " + minimumTransaction)
            return false
        }

        //Generate transaction outputs:
        val leftOver: Float = getInputsValue() - value //get value of inputs then the left over change:
        transactionId = calulateHash()
        outputs.add(TransactionOutput(reciepient, value, transactionId)) //send value to recipient
        outputs.add(TransactionOutput(sender, leftOver, transactionId)) //send the left over 'change' back to sender

        //Add outputs to Unspent list
        for (o in outputs) {
            UTXOs.put(o.id!!, o)
        }

        //Remove transaction inputs from UTXO lists as spent:
        for (i in inputs) {
            if (i.UTXO == null) continue  //if Transaction can't be found skip it
            UTXOs.remove(i.UTXO!!.id)
        }
        return true
    }

    fun getInputsValue(): Float {
        var total = 0f
        for (i in inputs) {
            if (i.UTXO == null) continue  //if Transaction can't be found skip it, This behavior may not be optimal.
            total += i.UTXO!!.value
        }
        return total
    }

    fun getOutputsValue(): Float {
        var total = 0f
        for (o in outputs) {
            total += o.value
        }
        return total
    }
}