package matchmaker;

import java.util.Collection;

public interface Match extends Comparable<Match> {
    
    Collection<Request> getMatched();
    
    void add(Request r);
    
    double score();
    
    boolean ready();
    
}
