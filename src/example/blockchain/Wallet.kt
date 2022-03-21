package example.blockchain

import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec


class Wallet {
    lateinit var privateKey: PrivateKey
    lateinit var publicKey: PublicKey

    init {
        generateKeyPair()
    }

    fun generateKeyPair() {
        try {
            val keyGen = KeyPairGenerator.getInstance("ECDSA", "BC")
            val random: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
            val ecSpec = ECGenParameterSpec("prime192v1")
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random) //256 bytes provides an acceptable security level
            val keyPair = keyGen.generateKeyPair()
            // Set the public and private keys from the keyPair
            privateKey = keyPair.private
            publicKey = keyPair.public
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getBalance(): Float {
        var total = 0f
        for (item in UTXOs2) {
            val UTXO = item.value
            if (UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs[UTXO.id!!] = UTXO //add it to our list of unspent transactions.
                total += UTXO.value
            }
        }
        return total
    }

    fun sendFunds(_recipient: PublicKey?, value: Float): Transaction? {
        if (getBalance() < value) {
            println("#Not Enough funds to send transaction. Transaction Discarded.")
            return null
        }
        val inputs = ArrayList<TransactionInput>()
        var total = 0f
        for (item in UTXOs2) {
            val UTXO: TransactionOutput = item.value
            total += UTXO.value
            inputs.add(TransactionInput(UTXO.id))
            if (total > value) break
        }
        val newTransaction = Transaction(publicKey, _recipient, value)
        newTransaction.inputs = inputs
        newTransaction.generateSignature(privateKey)
        for (input in inputs) {
            UTXOs2.remove(input.transactionOutputId)
        }
        return newTransaction
    }
}