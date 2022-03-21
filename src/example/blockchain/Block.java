package example.blockchain;

import java.util.ArrayList;
import java.util.Date;

import static example.blockchain.Util.Companion;

public class Block {
    public String hash;
    public String previousHash;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public String merkleRoot;

    //Block Constructor.
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String calculatedhash = Util.Companion.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        merkleRoot = Companion.getMerkleRoot(transactions);
        String target = Util.Companion.getDificultyString(difficulty); //Create a string with difficulty * "0"
        long start = System.currentTimeMillis();
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        long end = System.currentTimeMillis();
        System.out.println("Block Mined!!! : " + hash + " / Time : " + (end-start)/1000);
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((!"0".equals(previousHash))) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
