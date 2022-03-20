package example.blockchain;

import java.util.Date;

import static example.blockchain.Util.Companion;

public class Block {
    public String hash;
    public String previousHash;
    private String data; //our data will be a simple message.
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;

    //Block Constructor.
    public Block(String data,String previousHash ) {
        this.data = data;
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
                        data
        );
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        String target = Util.Companion.getDificultyString(difficulty); //Create a string with difficulty * "0"
        long start = System.currentTimeMillis();
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        long end = System.currentTimeMillis();
        System.out.println("Block Mined!!! : " + hash + " / Time : " + (end-start)/1000);
    }
}
