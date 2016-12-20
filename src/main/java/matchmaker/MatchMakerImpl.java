package matchmaker;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class MatchMakerImpl implements MatchMaker {

    private BlockingQueue<Request> requests = new ArrayBlockingQueue<>(1000);
    
    private final TreeMap<Double, Match> buckets = new TreeMap<>();
    
    private final Notifier notifier = new PrintingNotifier();
    
    public MatchMakerImpl() {
        CompletableFuture.runAsync(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Request r = requests.poll(100, TimeUnit.MILLISECONDS);
                    if(r != null) {
                        Match b = null;
                        
                        SortedMap<Double, Match> subMap = buckets.subMap(r.getScore() - 3d, r.getScore() + 3d);
                        if(subMap.isEmpty()) {
                            b = BucketMatch.ofSize(5);
                            b.add(r);
                            buckets.put(b.score(), b);
                        }
                        else {
                            double min = Double.MAX_VALUE;
                            for(Entry<Double, Match> entry : subMap.entrySet()) {
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
                            notifier.sendNotification(b);
                        }
                        else {
                            buckets.put(b.score(), b);
                        }
                        
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
    
    @Override
    public void findMatch(Request r) {
        requests.add(r);
    }

}
