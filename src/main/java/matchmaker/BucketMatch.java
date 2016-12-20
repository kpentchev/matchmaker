package matchmaker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BucketMatch implements Match {
    
    private Set<Request> requests = new HashSet<>();
    
    private final int readySize;
    
    private BucketMatch(int size) {
        this.readySize = size;
    }
    
    @Override
    public void add(Request r) {
        this.requests.add(r);
    }

    @Override
    public double score() {
        return requests.stream().mapToInt(Request::getScore).average().orElse(0);
    }

    @Override
    public boolean ready() {
        return readySize == requests.size();
    }
    
    public static BucketMatch ofSize(int size) {
        return new BucketMatch(size);
    }

    @Override
    public int compareTo(Match o) {
        return Double.compare(this.score(), o.score());
    }

    @Override
    public Collection<Request> getMatched() {
        return this.requests;
    }

}
