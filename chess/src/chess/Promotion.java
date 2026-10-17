package chess;

public class Promotion extends Move {
    final private Piece promotion;
    
    public Promotion(Index from, Index to, Piece promotion) {
        super(from, to);
        
        this.promotion = promotion;
    }
    
    public Piece getPromotion() {
        return promotion;
    }
    
    @Override
    public String toString() {
        return from + "" + to + "" + promotion;
    }    
}
