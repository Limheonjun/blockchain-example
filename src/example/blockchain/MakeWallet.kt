package example.blockchain

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


var walletA: Wallet? = null
var walletB: Wallet? = null
var UTXOs: MutableMap<String, TransactionOutput> = mutableMapOf()
var minimumTransaction = 0.1f

fun main() {
    //Security Provider로 BouncyCastle설정
    Security.addProvider(BouncyCastleProvider())

    //지갑 생성
    walletA = Wallet()
    walletB = Wallet()

    println("Private and Public Keys : ")
    println(Util.Companion.getStringFromKey(walletA!!.privateKey))
    println(Util.Companion.getStringFromKey(walletB!!.privateKey))

    //지갑A에서 지갑B로의 트랜잭션 생성
    val tx: Transaction = Transaction(walletA!!.publicKey, walletB!!.publicKey, 5f, null)
    tx.generateSignature(walletA!!.privateKey)

    //시그니처가 동작하는지 검증하고, public key로 검증
    println("Is Signature Verified")
    println(tx.verifySignature())
}