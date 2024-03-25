package io.collective.basic;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Blockchain {

    int count = 0;

    Block latestBlock;

    HashMap<String, Block> ledger = new HashMap<String,Block>();

    public boolean isEmpty() {
        return this.count > 0 ? false : true;
    }

    public void add(Block block) {

        if(this.count == 0){
            this.ledger.put(block.getPreviousHash(), null);
        }

        
        this.ledger.put(block.getHash(), block);
        this.latestBlock = block;
        this.count++;
    }

    public int size() {
        return this.count;
    }

    public boolean isValid() throws NoSuchAlgorithmException {

        Block currentBlock = latestBlock;
        int blocks = this.count;

        
        while(blocks > 0) {

            // todo - check mined
            if(!isMined(currentBlock)){
                return false;
            }
            // todo - check previous hash matches
            else if(!ledger.containsKey(currentBlock.getPreviousHash())){
                return false;
            }
            // todo - check hash is correctly calculated
            else if(!Block.calculateHash(String.format("%s%d%d", currentBlock.getPreviousHash(),currentBlock.getTimestamp(),currentBlock.getNonce())).equals(currentBlock.getHash())){
                return false;
            }

            currentBlock = this.ledger.get(currentBlock.getPreviousHash());
            blocks--;
        }

        return true;
    }

    /// Supporting functions that you'll need.

    public static Block mine(Block block) throws NoSuchAlgorithmException {
        Block mined = new Block(block.getPreviousHash(), block.getTimestamp(), block.getNonce());

        while (!isMined(mined)) {
            mined = new Block(mined.getPreviousHash(), mined.getTimestamp(), mined.getNonce() + 1);
        }
        return mined;
    }

    public static boolean isMined(Block minedBlock) {
        return minedBlock.getHash().startsWith("00");
    }
}