package chess;

import static chess.Piece.BISHOP;
import static chess.Piece.KING;
import static chess.Piece.KNIGHT;
import static chess.Piece.QUEEN;
import static chess.Piece.ROOK;

public enum Step {
    xxy(-2, -1),
    xxY(-2, 1),
    xyy(-1, -2),
    xy(-1, -1),
    x(-1, 0),
    xY(-1, 1),
    xYY(-1, 2),
    y(0, -1),
    Y(0, 1),
    Xyy(1, -2),
    Xy(1, -1),
    X(1, 0),
    XY(1, 1),
    XYY(1, 2),
    XXy(2, -1),
    XXY(2, 1);
    
    final public int horizontal;
    final public int vertical;
    
    Step(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
    
    final static char[] RANK = "12345678".toCharArray();
    final static char[] FILE = "abcdefgh".toCharArray();
    
    public boolean canStep(Index index) {
        return index.file + horizontal >= 0 && index.file + horizontal < FILE.length && index.rank + vertical >= 0 && index.rank + vertical < RANK.length;
    }
    
    public Index getStep(Index index) {
        return Index.valueOf(FILE[index.file + horizontal] + "" + RANK[index.rank + vertical]);
    }
    
    public static Step[] getSteps(Piece piece) {
        int color = (piece.toString().toUpperCase() + piece.toString().toLowerCase()).indexOf(piece.toString());
        
        if (piece == KING[color] || piece == QUEEN[color]) {
            return new Step[] {xy, x, xY, y, Y, Xy, X, XY};
        } else if (piece == ROOK[color]) {
            return new Step[] {x, y, Y, X};
        } else if (piece == BISHOP[color]) {
            return new Step[] {xy, xY, Xy, XY};
        } else if (piece == KNIGHT[color]) {
            return new Step[] {xxy, xxY, xyy, xYY, Xyy, XYY, XXy, XXY};
        } else {
            return new Step[][] {{xY, Y, XY}, {xy, y, Xy}}[color];
        }
    }

}
