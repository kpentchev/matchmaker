package matchmaker;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchMakerImplTest {
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);
    
    @Test
    public void test() throws InterruptedException {
        MatchMaker matchMaker = new MatchMakerImpl();
        
        Random random = new Random();
        
        for(int i = 0; i < 1000; i++) {
            final int id = i;
            executor.execute(() -> {
                try {
                    Thread.sleep((long) random.nextInt(2000));
                    matchMaker.findMatch(new PlayerRequest(id, random.nextInt(24)));
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
            
        }
        
        Thread.sleep(1000 * 60 * 5);
    }

}
