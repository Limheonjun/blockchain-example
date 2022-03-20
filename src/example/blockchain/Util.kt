package example.blockchain

import com.google.gson.GsonBuilder
import java.security.MessageDigest


class Util {
    companion object {
        fun applySha256(input: String): String {
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                //Applies sha256 to our input,
                val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
                val hexString = StringBuffer()
                for (i in hash.indices) {
                    val hex = Integer.toHexString(0xff and hash[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                hexString.toString()
            } catch (e: Exception) {
                throw RuntimeException(e);
            }
        }

        //Short hand helper to turn Object into a json string
        fun getJson(o: Any?): String {
            return GsonBuilder().setPrettyPrinting().create().toJson(o)
        }

        //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
        fun getDificultyString(difficulty: Int): String? {
            return String(CharArray(difficulty)).replace('\u0000', '0')
        }

        //블록체인의 무결성 검사 메소드
        fun isChainValid(blockchain: MutableList<Block>, difficulty: Int): Boolean? {
            var currentBlock: Block
            var previousBlock: Block
            val hashTarget = String(CharArray(difficulty)).replace('\u0000', '0')

            //loop through blockchain to check hashes:
            for (i in 1 until blockchain.size) {
                currentBlock = blockchain[i]
                previousBlock = blockchain[i - 1]
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
                if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                    System.out.println("This block hasn't been mined");
                    return false;
                }
            }
            return true
        }
    }
}