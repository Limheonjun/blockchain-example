package example.blockchain

import com.google.gson.GsonBuilder
import java.security.*
import java.util.*


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

        //암호화된 문자열을 어떤 키로부터 리턴
        fun getStringFromKey(key: Key): String? {
            return Base64.getEncoder().encodeToString(key.getEncoded())
        }

        //Private key와 input을 받은뒤 서명 후 바이트 배열 리턴
        fun applyECDSASig(privateKey: PrivateKey?, input: String): ByteArray? {
            val dsa: Signature
            var output: ByteArray? = ByteArray(0)
            try {
                dsa = Signature.getInstance("ECDSA", "BC")
                dsa.initSign(privateKey)
                val strByte = input.toByteArray()
                dsa.update(strByte)
                val realSig: ByteArray = dsa.sign()
                output = realSig
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
            return output
        }

        //Public key와 data를 받고 signature가 유효한지에 대해 true/false를 리턴
        fun verifyECDSASig(publicKey: PublicKey?, data: String, signature: ByteArray?): Boolean {
            return try {
                val ecdsaVerify: Signature = Signature.getInstance("ECDSA", "BC")
                ecdsaVerify.initVerify(publicKey)
                ecdsaVerify.update(data.toByteArray())
                ecdsaVerify.verify(signature)
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }

        fun getMerkleRoot(transactions: ArrayList<Transaction>): String? {
            var count = transactions.size
            var previousTreeLayer: MutableList<String?> =
                ArrayList()
            for (transaction in transactions) {
                previousTreeLayer.add(transaction.transactionId)
            }
            var treeLayer = previousTreeLayer
            while (count > 1) {
                treeLayer = ArrayList()
                var i = 1
                while (i < previousTreeLayer.size) {
                    treeLayer.add(applySha256(previousTreeLayer[i - 1] + previousTreeLayer[i]))
                    i += 2
                }
                count = treeLayer.size
                previousTreeLayer = treeLayer
            }
            return if (treeLayer.size == 1) treeLayer[0] else ""
        }

    }
}