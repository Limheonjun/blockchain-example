package example.blockchain

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


var walletC: Wallet? = null
var walletD: Wallet? = null
var UTXOs2: MutableMap<String, TransactionOutput> = mutableMapOf()
fun main() {
    Security.addProvider(BouncyCastleProvider())

    //지갑 생성
    walletC = Wallet()
    walletD = Wallet()
    val coinbase = Wallet()

    //지갑C로 100코인을 보내는 트랜잭션 생성
    val genesisTransaction = Transaction(coinbase.publicKey, walletC!!.publicKey, 100f, null)
    genesisTransaction.generateSignature(coinbase.privateKey) //manually sign the genesis transaction

    genesisTransaction.transactionId = "0" //manually set the transaction id
    val transactionOutput: TransactionOutput = TransactionOutput(
        genesisTransaction.reciepient,
        genesisTransaction.value,
        genesisTransaction.transactionId
    )
    transactionOutput.id = "1"

    //최초의 트랜잭션의 output에 트랜잭션 output생성
    //트랜잭션 output의 내용엔 받는사람, 받는 코인 개수, 트랜잭션 아이디가 들어감
    genesisTransaction.outputs.add(
        transactionOutput
    ) //manually add the Transactions Output

    UTXOs2[genesisTransaction.outputs.get(0).id!!] = genesisTransaction.outputs.get(0) //its important to store our first transaction in the UTXOs list.

    println("Creating and Mining Genesis block... ")
    val genesis = Block("0")
    genesis.addTransaction(genesisTransaction)
    addBlock(genesis)

    //testing
    val block1 = Block(genesis.hash)
    System.out.println("WalletA's balance is: ${walletA!!.getBalance()}")
    println("\nWalletA is Attempting to send funds (40) to WalletB...")
    block1.addTransaction(walletA!!.sendFunds(walletB!!.publicKey, 40f))
    addBlock(block1)
    System.out.println(" WalletA's balance is: ${walletA!!.getBalance()}")
    System.out.println("WalletB's balance is: " + walletB!!.getBalance())

    val block2 = Block(block1.hash)
    println("\nWalletA Attempting to send more funds (1000) than it has...")
    block2.addTransaction(walletA!!.sendFunds(walletB!!.publicKey, 1000f))
    addBlock(block2)
    System.out.println("WalletA's balance is: ${walletA!!.getBalance()}")
    System.out.println("WalletB's balance is: " + walletB!!.getBalance())

    val block3 = Block(block2.hash)
    println("\nWalletB is Attempting to send funds (20) to WalletA...")
    block3.addTransaction(walletB!!.sendFunds(walletA!!.publicKey, 20f))
    System.out.println("WalletA's balance is: ${walletA!!.getBalance()}")
    System.out.println("WalletB's balance is: " + walletB!!.getBalance())

    isChainValid()
}