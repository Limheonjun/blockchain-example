package example.blockchain

import example.blockchain.Util.Companion.getJson


val blockchain: MutableList<Block> = mutableListOf()
val difficulty = 30

fun main() {
    //add our blocks to the blockchain ArrayList:
    println("Trying to Mine block 1... ")
//    addBlock(Block("Hi im the first block", "0"))
//
//    println("Trying to Mine block 2... ")
//    addBlock(Block("Yo im the second block", blockchain[blockchain.size - 1].hash))
//
//    println("Trying to Mine block 3... ")
//    addBlock(Block("Hey im the third block", blockchain[blockchain.size - 1].hash))

    println("Blockchain is Valid: ${isChainValid()}")

    val blockchainJson: String = getJson(blockchain)
    println("\nThe block chain: ")
    println(blockchainJson)
}

fun isChainValid(): Boolean? {
    var currentBlock: Block
    var previousBlock: Block
    val hashTarget = String(CharArray(difficulty)).replace('\u0000', '0')

    //loop through blockchain to check hashes:
    for (i in 1 until blockchain.size) {
        currentBlock = blockchain.get(i)
        previousBlock = blockchain.get(i - 1)
        //compare registered hash and calculated hash:
        if (currentBlock.hash != currentBlock.calculateHash()) {
            println("Current Hashes not equal")
            return false
        }
        //compare previous hash and registered previous hash
        if (previousBlock.hash != currentBlock.previousHash) {
            println("Previous Hashes not equal")
            return false
        }
        //check if hash is solved
        if (currentBlock.hash.substring(0, difficulty) != hashTarget) {
            println("This block hasn't been mined")
            return false
        }
    }
    return true
}

fun addBlock(newBlock: Block) {
    newBlock.mineBlock(difficulty)
    blockchain.add(newBlock)
}



