package chess;

public enum Piece {
    K, Q, R, B, N, P,
    k, q, r, b, n,p;
    
    final public static Piece[] KING = {K, k};
    final public static Piece[] QUEEN = {Q, q};
    final public static Piece[] ROOK = {R, r};
    final public static Piece[] BISHOP = {B, b};
    final public static Piece[] KNIGHT = {N, n};
    final public static Piece[] PAWN = {P, p};

    public static boolean isColor(Side color, Piece piece) {
        return piece == KING[color.ordinal()] || piece == QUEEN[color.ordinal()] || piece == ROOK[color.ordinal()] || piece == BISHOP[color.ordinal()] || piece == KNIGHT[color.ordinal()] || piece == PAWN[color.ordinal()]; 
    }

    final public static Piece EMPTY = null;
}
