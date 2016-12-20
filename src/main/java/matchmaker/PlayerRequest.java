package matchmaker;

public class PlayerRequest implements Request {

    private long id;
    
    private int score;
    
    public PlayerRequest(long id, int score) {
        this.id = id;
        this.score = score;
    }
    
    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public String toString() {
        return "PlayerRequest [id=" + id + ", score=" + score + "]";
    }
    
    

}
