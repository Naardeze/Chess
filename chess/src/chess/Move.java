package chess;

public class Move {
    final protected Index from;
    final protected Index to;
    
    public Move(Index from, Index to) {
        this.from = from;
        this.to = to;
    }
    
    public Index getFrom() {
        return from;
    }
    
    public Index getTo() {
        return to;
    }
    
    @Override
    public String toString() {
        return from + "" + to;
    }    
}
