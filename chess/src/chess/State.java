package chess;

import static chess.Index.GRID;
import static chess.Index.NO_INDEX;
import static chess.Piece.BISHOP;
import static chess.Piece.EMPTY;
import static chess.Piece.KING;
import static chess.Piece.KNIGHT;
import static chess.Piece.PAWN;
import static chess.Piece.QUEEN;
import static chess.Piece.ROOK;
import static chess.Piece.isColor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

final public class State extends HashMap<Index, HashSet<Index>> {
    final private static String SLASH = "/";
    final private static String NONE = "-";
    
    final private Side color;
    final private Piece[][] board;
    final private LinkedHashSet<Index>[] castling;
    final private Index enPassant;
    final private int rule50;
    final private int halfMoves;
    
    final private boolean check;
    
    private State(Piece[][] board, Side color, LinkedHashSet<Index>[] castling, Index enPassant, int rule50, int halfMoves) {
        check = isCheck(color, board);
        
        for (Index from : Index.values()) {
            if (Piece.isColor(color, board[from.rank][from.file])) {
                Piece piece = board[from.rank][from.file];
                HashSet<Index> pieceMoves = new HashSet();
                
                board[from.rank][from.file] = EMPTY;
                
                for (Step step : Step.getSteps(piece)) {
                    if (step.canStep(from)) {
                        Index to = step.getStep(from);
                        
                        if (((piece == KING[color.ordinal()] || piece == QUEEN[color.ordinal()] || piece == ROOK[color.ordinal()] || piece == BISHOP[color.ordinal()] || piece == KNIGHT[color.ordinal()]) && !isColor(color, board[to.rank][to.file])) || (piece == PAWN[color.ordinal()] && ((step.horizontal == 0 && board[to.rank][to.file] == EMPTY) || (Math.abs(step.horizontal) == 1 && isColor(color.flip(), board[to.rank][to.file])) || to == enPassant))) {
                            Piece copy = board[to.rank][to.file];
                            board[to.rank][to.file] = piece;
                            
                            if (piece == PAWN[color.ordinal()] && to == enPassant) {
                                board[from.rank][to.file] = EMPTY;
                            }
                            
                            if (!isCheck(color, board)) {
                                pieceMoves.add(to);
                            }
                            
                            board[to.rank][to.file] = copy;
                            
                            if (piece == QUEEN[color.ordinal()] || piece == ROOK[color.ordinal()] || piece == BISHOP[color.ordinal()]) {
                                while (board[to.rank][to.file] == EMPTY && step.canStep(to)) {
                                    to = step.getStep(to);
                                        
                                    if (!isColor(color, board[to.rank][to.file])) {
                                        copy = board[to.rank][to.file];
                                        board[to.rank][to.file] = piece;

                                        if (!isCheck(color, board)) {
                                            pieceMoves.add(to);
                                        }

                                        board[to.rank][to.file] = copy;
                                    }
                                }
                            } else if (piece == PAWN[color.ordinal()]) {
                                if (step.horizontal == 0 && from.rank == new int[] {1, 6}[color.ordinal()]) {
                                    to = step.getStep(to);
                                    
                                    if (board[to.rank][to.file] == EMPTY) {
                                        board[to.rank][to.file] = piece;
                                        
                                        if (!isCheck(color, board)) {
                                            pieceMoves.add(to);
                                        }

                                        board[to.rank][to.file] = EMPTY;
                                    }
                                } else if (to == enPassant) {
                                    board[from.rank][to.file] = PAWN[color.flip().ordinal()];
                                }
                            }
                        }
                    }
                }
                
                if (piece == KING[color.ordinal()] && !check) {
                    for (Index rook : castling[color.ordinal()]) {
                        test : {
                            Index to = Index.values()[Math.max(from.ordinal() - 1, Math.min(rook.ordinal(), from.ordinal() + 1))];

                            for (Index step = to; step != rook; step = Index.values()[Math.max(step.ordinal() - 1, Math.min(rook.ordinal(), step.ordinal() + 1))]) {
                                if (board[step.rank][step.file] != EMPTY) {
                                    break test;
                                }
                            }

                            if (pieceMoves.contains(to)) {
                                to = Index.values()[Math.max(to.ordinal() - 1, Math.min(rook.ordinal(), to.ordinal() + 1))];
    
                                board[to.rank][to.file] = piece;
                                        
                                if (!isCheck(color, board)) {
                                    pieceMoves.add(to);
                                }

                                board[to.rank][to.file] = EMPTY;
                            }
                        }
                    }
                }

                board[from.rank][from.file] = piece;
                
                if (!pieceMoves.isEmpty()) {
                    put(from, pieceMoves);
                }
            }
        }
        
        this.board = board;
        this.color = color;
        this.castling = castling;
        this.enPassant = enPassant;
        this.rule50 = rule50;
        this.halfMoves = halfMoves;
    }
    
    public Piece[][] getBoard() {
        return board;
    }
    
    public Side getColor() {
        return color;
    }
    
    public LinkedHashSet<Index>[] getCastling() {
        return castling;
    }
    
    public Index getEnPassant() {
        return enPassant;
    }
    
    public int getRule50() {
        return rule50;
    }
    
    public int getHalfMoves() {
        return halfMoves;
    }
    
    public boolean getCheck() {
        return check;
    }
    
    private static boolean isCheck(Side color, Piece[][] board) {
        for (Index from : Index.values()) {
            if (board[from.rank][from.file] == KING[color.ordinal()]) {
                for (Step step : Step.values()) {
                    if (step.canStep(from)) {
                        Index to = step.getStep(from);
                        
                        if ((Math.abs(step.horizontal * step.vertical) <= 1 && board[to.rank][to.file] == KING[color.flip().ordinal()]) || (Math.abs(step.horizontal * step.vertical) == 2 && board[to.rank][to.file] == KNIGHT[color.flip().ordinal()]) || (Math.abs(step.horizontal) == 1 && step.vertical == new int[] {1, -1}[color.ordinal()] && board[to.rank][to.file] == PAWN[color.flip().ordinal()])) {
                            return true;
                        } else if (Math.abs(step.horizontal * step.vertical) <= 1) {
                            while (board[to.rank][to.file] == EMPTY && step.canStep(to)) {
                                to = step.getStep(to);
                            }
                            
                            if (board[to.rank][to.file] == QUEEN[color.flip().ordinal()] || (step.horizontal * step.vertical == 0 && board[to.rank][to.file] == ROOK[color.flip().ordinal()]) || (Math.abs(step.horizontal * step.vertical) == 1 && board[to.rank][to.file] == BISHOP[color.flip().ordinal()])) {
                                return true;
                            }
                        }
                    }
                }
                
                break;
            }
        }
           
        return false;
    }
    
    public State doMove(Move move) {
        Piece[][] board = this.board.clone();
        
        for (int i = 0; i < board.length; i++) {
            board[i] = this.board[i].clone();
        }
        
        LinkedHashSet<Index>[] castling = this.castling.clone();
        
        for (int i = 0; i < castling.length; i++) {
            castling[i] = new LinkedHashSet(this.castling[i]);
        }
        
        Index from = move.getFrom();
        Index to = move.getTo();
        
        Piece piece = board[from.rank][from.file];
        
        board[from.rank][from.file] = EMPTY;
        board[to.rank][to.file] = piece;
        
        if (piece == KING[color.ordinal()] && !castling[color.ordinal()].isEmpty()) {
            if (Math.abs(to.file - from.file) == 2) {
                Index rook = Index.valueOf(to.toString().replace('c', 'a').replace('g', 'h'));
                
                board[to.rank][(from.file + to.file) / 2] = board[rook.rank][rook.file];
                board[rook.rank][rook.file] = EMPTY;
            }
            
            castling[color.ordinal()].clear();
        } else if (piece == PAWN[color.ordinal()]) {
            if (move instanceof PromotionMove) {
                board[to.rank][to.file] = ((PromotionMove) move).getPiece();
            } else if (to == enPassant) {
                board[from.rank][to.file] = EMPTY;
            }
        }
        
        castling[color.ordinal()].remove(from);
        castling[color.flip().ordinal()].remove(to);

        Index enPassant = piece == PAWN[color.ordinal()] && Math.abs(to.rank - from.rank) == 2 ? Index.values()[(from.ordinal() + to.ordinal()) / 2] : NO_INDEX;
        
        int rule50 = piece != PAWN[color.ordinal()] && this.board[to.rank][to.file] == EMPTY ? this.rule50 + 1 : 0;
        int halfMoves = this.halfMoves + 1;

        Side color = this.color.flip();
        
        return new State(board, color, castling, enPassant, rule50, halfMoves);
    }
    
    public static State fromString(String fen) {
        StringTokenizer tokenizer = new StringTokenizer(fen);
        
        Piece[][] board = new Piece[GRID][GRID];
        
        int row = board.length - 1;
        
        for (String rank : tokenizer.nextToken().split(SLASH)) {
            int column = 0;
            
            for (char piece : rank.toCharArray()) {
                try {
                    column += Integer.parseInt("" + piece);
                } catch (Exception ex) {
                    board[row][column++] = Piece.valueOf("" + piece);
                }
            }
            
            row--;
        }
        
        Side color = Side.valueOf(tokenizer.nextToken());
       
        LinkedHashSet<Index>[] castling = new LinkedHashSet[Side.values().length];
        
        for (int i = 0; i < castling.length; i++) {
            castling[i] = new LinkedHashSet();
        }
        
        for (char rook : tokenizer.nextToken().toCharArray()) {
            switch (rook) {
                case 'Q' : castling[Side.w.ordinal()].add(Index.a1); break;
                case 'K' : castling[Side.w.ordinal()].add(Index.h1); break;
                case 'q' : castling[Side.b.ordinal()].add(Index.a8); break;
                case 'k' : castling[Side.b.ordinal()].add(Index.h8);
            }
        }
        
        Index enPassant = NO_INDEX;

        try {
            enPassant = Index.valueOf(tokenizer.nextToken());
        } catch (Exception ex) {}
        
        int rule50 = Integer.parseInt(tokenizer.nextToken());
        int halfMoves = Integer.parseInt(tokenizer.nextToken());
        
        return new State(board, color, castling, enPassant, rule50, halfMoves);
    }

    @Override
    public String toString() {
        String[] board = new String[this.board.length];
        String QKqk = "";
        
        int row = board.length;
        
        for (Piece[] rank : this.board) {
            board[--row] = "";
            
            for (Piece piece : rank) {
                board[row] = (board[row] + (piece == EMPTY ? "1" : piece)).replace("11", "2").replace("21", "3").replace("31", "4").replace("41", "5").replace("51", "6").replace("61", "7").replace("71", "8");
            }
        }
        
        for (LinkedHashSet<Index> castling : castling) {
            for (Index rook : castling) {
                switch (rook) {
                    case a1 : QKqk += Piece.Q; break;
                    case h1 : QKqk += Piece.K; break;
                    case a8 : QKqk += Piece.q; break;
                    case h8 : QKqk += Piece.k;
                }
            }
        }
        
        return String.join(SLASH, board) + " " + color + " " + (QKqk.isEmpty() ? NONE : QKqk) + " " + (enPassant == NO_INDEX ? NONE : enPassant) + " " + rule50 + " " + halfMoves;
    }

}
