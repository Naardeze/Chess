package chess;

public class PromotionMove extends Move {
    final public static Piece[][] QRBN = {{Piece.Q, Piece.R, Piece.B, Piece.N}, {Piece.q, Piece.r, Piece.b, Piece.n}};
    
    final private Piece piece;
    
    public PromotionMove(Index from, Index to, Piece piece) {
        super(from, to);
        
        this.piece = piece;
    }
    
    public Piece getPiece() {
        return piece;
    }
    
    @Override
    public String toString() {
        return from + "" + to + "" + piece;
    }
    
}
