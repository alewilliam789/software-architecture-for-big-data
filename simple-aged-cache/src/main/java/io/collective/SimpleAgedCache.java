package io.collective;

import java.time.Clock;

import io.collective.SimpleAgedCache.Queue.QueueNode;


public class SimpleAgedCache {

    private Clock clock;

    private int count = 0;

    private int MAX_SIZE = 1000000;

    private Object[] store;

    private Queue queue;

    public class Queue {
        private QueueNode head;
        private QueueNode tail;
        public int count = 0;

        public class QueueNode {
            QueueNode previous = null;
            QueueNode next = null;
            ExpireableEntry entry;

            public QueueNode(ExpireableEntry entry){
                this.entry = entry;
            }

        } 

        public boolean Add(ExpireableEntry value){
            QueueNode newNode = new QueueNode(value);
            if(this.count == 0){
                this.head = newNode;
                this.tail = newNode;
            }
            else if (this.count == 1){
                this.head.next = newNode;
                this.tail = newNode;
            }
            else {
                this.tail.next = newNode;
                this.tail = newNode;
            }

            this.count++;
            return true;
        }

        public ExpireableEntry Pop(){
            QueueNode returnedObject = this.head;

            if(count -1 > 0) {
                this.head = this.head.next;
            }

            this.count--;
            return returnedObject.entry;
        }
    }

    private class ExpireableEntry {
        int hash;
        long expirationTime;

        public ExpireableEntry(int hash, long expirationTime){
            this.hash = hash;
            this.expirationTime = expirationTime;
        }
    }




    public SimpleAgedCache(Clock clock) {

        this.clock = clock;
        this.store = new Object[this.MAX_SIZE];
        this.queue = new Queue();
    }

    public SimpleAgedCache() {
        this.clock = Clock.systemUTC();
        this.store = new Object[this.MAX_SIZE];
        this.queue = new Queue();
    }

    public void put(Object key, Object value, int retentionInMillis) {
        this.cleanup();

        int hash = Math.abs(key.hashCode()) % this.MAX_SIZE;

        this.store[hash] = value;

        this.count++;

        ExpireableEntry entry = new ExpireableEntry(hash, this.clock.millis()+retentionInMillis);

        this.queue.Add(entry);
    }

    public boolean isEmpty() {
        this.cleanup();


        return this.count > 0 ? false : true;
    }

    public int size() {
        this.cleanup();

        return this.count;
    }

    public Object get(Object key) {
        this.cleanup();

        int hash = Math.abs(key.hashCode()) % this.MAX_SIZE;

        Object returnedObject = this.store[hash];

        return returnedObject;
    }

    private void cleanup(){

        QueueNode currentNode = this.queue.head;
        
        while(currentNode != null){

            ExpireableEntry currenEntry = currentNode.entry;

            if(currenEntry.expirationTime < this.clock.millis()){
                this.store[currenEntry.hash] = null;
                this.queue.Pop();
                this.count--;
            }
            else {
                break;
            }

            currentNode = currentNode.next;
        }
    }

}