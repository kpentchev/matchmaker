package com.heavenhr.meetingroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SelectionTest {
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);
    
    public static void main(String[] args) throws InterruptedException {
        
        BlockingQueue<Request> requests = new ArrayBlockingQueue<>(1000);
        Random random = new Random();
        
        executor.execute(new Consummer(requests));
        
        for(int i = 0; i < 1000; i++) {
            final int id = i;
            executor.execute(() -> {
                try {
                    Thread.sleep((long) random.nextInt(2000));
                    requests.put(new Request(id, random.nextInt(24)));
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
            
        }
        
        executor.awaitTermination(5, TimeUnit.MINUTES);
        executor.shutdownNow();
    }
    
    @ToString
    @Data
    static class Request {
        
        long id;
        
        int score;
        
        Request(long id, int score) {
            this.id = id;
            this.score = score;
        }
    }
    
    @AllArgsConstructor
    static class Consummer implements Runnable {
        
        private BlockingQueue<Request> requests;
        
        private final TreeMap<Double, Bucket> buckets = new TreeMap<>();

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis());
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Request r = requests.poll(100, TimeUnit.MILLISECONDS);
                    if(r != null) {
                        Bucket b = null;
                        
                        SortedMap<Double, Bucket> subMap = buckets.subMap(r.getScore() - 3d, r.getScore() + 3d);
                        if(subMap.isEmpty()) {
                            b = new Bucket();
                            b.add(r);
                            buckets.put(b.score(), b);
                        }
                        else {
                            double min = Double.MAX_VALUE;
                            for(Entry<Double, Bucket> entry : subMap.entrySet()) {
                                double diff = Math.abs(r.getScore() - entry.getKey());
                                if(diff < min) {
                                    min = diff;
                                    b = entry.getValue();
                                }
                            }
                            buckets.remove(b.score());
                        }
                        
                        b.add(r);
                        if(b.ready()) {
                            b.end = System.currentTimeMillis();
                            System.out.println(b);
                        }
                        else {
                            buckets.put(b.score(), b);
                        }
                        
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(System.currentTimeMillis());
        }
        
    }
    
    @ToString
    static class Bucket implements Comparable<Bucket> {
        Set<Request> requests = new HashSet<>();
        
        long start = System.currentTimeMillis();
        
        long end;
        
        
        void add(Request r) {
            requests.add(r);
        }
        
        double score() {
            return requests.stream().mapToInt(Request::getScore).average().orElse(0);
        }
        
        boolean ready() {
            return requests.size() == 10;
        }

        @Override
        public int compareTo(Bucket o) {
            return Double.compare(this.score(), o.score());
        }
    }

}
