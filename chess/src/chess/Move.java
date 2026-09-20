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
    
    public static Move fromString(String move) {
        Index from = Index.valueOf(move.substring(0, 2));
        Index to = Index.valueOf(move.substring(2, 4));
        
        if (move.length() == 5) {
            Piece piece = Piece.valueOf(move.substring(4));
            
            return new PromotionMove(from, to, piece);
        } else {
            return new Move(from, to);
        }
    }
    
    @Override
    public String toString() {
        return from + "" + to;
    }
    
}
