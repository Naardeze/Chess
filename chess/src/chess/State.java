package chess;

import static chess.Index.FILE;
import static chess.Index.NO_INDEX;
import static chess.Index.RANK;
import static chess.Piece.BISHOP;
import static chess.Piece.EMPTY;
import static chess.Piece.KING;
import static chess.Piece.KNIGHT;
import static chess.Piece.PAWN;
import static chess.Piece.QUEEN;
import static chess.Piece.ROOK;
import static chess.Piece.isColor;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

final public class State extends EnumMap<Index, HashSet<Index>> {
    final private Piece[][] board;
    final private Side color;
    final private LinkedHashSet<Index>[] castling;
    final private Index enPassant;
    final private int rule50Counter;
    final private int halfMoveCounter;
    
    final private boolean check;
    
    private State(Piece[][] board, Side color, LinkedHashSet<Index>[] castling, Index enPassant, int rule50Counter, int halfMoveCounter) {
        super(Index.class);
        
        check = check(color, board);
        
        for (Index from : Index.values()) {
            if (isColor(color, board[from.rank][from.file])) {
                Piece piece = board[from.rank][from.file];
                HashSet<Index> index = new HashSet();
                
                board[from.rank][from.file] = EMPTY;
                
                for (Step step : Step.getSteps(piece)) {
                    if (from.hasNext(step)) {
                        Index to = from.getNext(step);
                        
                        if (((piece == KING[color.ordinal()] || piece == QUEEN[color.ordinal()] || piece == ROOK[color.ordinal()] || piece == BISHOP[color.ordinal()] || piece == KNIGHT[color.ordinal()]) && !isColor(color, board[to.rank][to.file])) || (piece == PAWN[color.ordinal()] && ((step.horizontal == 0 && board[to.rank][to.file] == EMPTY) || (Math.abs(step.horizontal) == 1 && (isColor(color.opposite(), board[to.rank][to.file]) || to == enPassant))))) {
                            Piece copy = board[to.rank][to.file];
                        
                            board[to.rank][to.file] = piece;
                            
                            if (piece == PAWN[color.ordinal()] && to == enPassant) {
                                board[from.rank][to.file] = EMPTY;
                            }
                            
                            if (!check(color, board)) {
                                index.add(to);
                            }
                            
                            board[to.rank][to.file] = copy;
                            
                            if (piece == QUEEN[color.ordinal()] || piece == ROOK[color.ordinal()] || piece == BISHOP[color.ordinal()]) {
                                while (board[to.rank][to.file] == EMPTY && to.hasNext(step)) {
                                    to = to.getNext(step);
                                        
                                    if (!isColor(color, board[to.rank][to.file])) {
                                        copy = board[to.rank][to.file];
                                        board[to.rank][to.file] = piece;

                                        if (!check(color, board)) {
                                            index.add(to);
                                        }

                                        board[to.rank][to.file] = copy;
                                    }
                                }
                            } else if (piece == PAWN[color.ordinal()]) {
                                if (step.horizontal == 0 && from.rank == new int[] {1, 6}[color.ordinal()]) {
                                    to = to.getNext(step);
                                    
                                    if (board[to.rank][to.file] == EMPTY) {
                                        board[to.rank][to.file] = piece;
                                        
                                        if (!check(color, board)) {
                                            index.add(to);
                                        }

                                        board[to.rank][to.file] = EMPTY;
                                    }
                                } else if (to == enPassant) {
                                    board[from.rank][to.file] = PAWN[color.opposite().ordinal()];
                                }
                            }
                        }
                    }
                }

                if (piece == KING[color.ordinal()] && !check) {
                    for (Index rook : castling[color.ordinal()]) {
                        castlingCheck : {
                            Index to = Index.valueOf(rook.toString().replace('a', 'd').replace('h', 'f'));

                            for (int file = to.file; file != rook.file; file += Integer.signum(rook.file - file)) {
                                if (board[to.rank][file] != EMPTY) {
                                    break castlingCheck;
                                }
                            }

                            if (index.contains(to)) {
                                to = Index.valueOf(to.toString().replace('d', 'c').replace('f', 'g'));
                                board[to.rank][to.file] = piece;
                                        
                                if (!check(color, board)) {
                                    index.add(to);
                                }

                                board[to.rank][to.file] = EMPTY;
                            }
                        }
                    }
                }

                board[from.rank][from.file] = piece;
                
                if (!index.isEmpty()) {
                    put(from, index);
                }
            }
        }
        
        this.board = board;
        this.color = color;
        this.castling = castling;
        this.enPassant = enPassant;
        this.rule50Counter = rule50Counter;
        this.halfMoveCounter = halfMoveCounter;
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
    
    public int getRule50Counter() {
        return rule50Counter;
    }
    
    public int getHalfMoveCounter() {
        return halfMoveCounter;
    }
    
    public boolean isCheck() {
        return check;
    }
    
    private static boolean check(Side color, Piece[][] board) {
        for (Index from : Index.values()) {
            if (board[from.rank][from.file] == KING[color.ordinal()]) {
                for (Step step : Step.values()) {
                    if (from.hasNext(step)) {
                        Index to = from.getNext(step);
                        
                        if ((Math.abs(step.horizontal * step.vertical) <= 1 && board[to.rank][to.file] == KING[color.opposite().ordinal()]) || (Math.abs(step.horizontal * step.vertical) == 2 && board[to.rank][to.file] == KNIGHT[color.opposite().ordinal()]) || (Math.abs(step.horizontal) == 1 && step.vertical == new int[] {1, -1}[color.ordinal()] && board[to.rank][to.file] == PAWN[color.opposite().ordinal()])) {
                            return true;
                        } else if (Math.abs(step.horizontal * step.vertical) <= 1) {
                            while (board[to.rank][to.file] == EMPTY && to.hasNext(step)) {
                                to = to.getNext(step);
                            }
                            
                            if (board[to.rank][to.file] == QUEEN[color.opposite().ordinal()] || (step.horizontal * step.vertical == 0 && board[to.rank][to.file] == ROOK[color.opposite().ordinal()]) || (Math.abs(step.horizontal * step.vertical) == 1 && board[to.rank][to.file] == BISHOP[color.opposite().ordinal()])) {
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
    
    public State move(Move move) {
        Piece[][] board = Arrays.asList(this.board).toArray(new Piece[this.board.length][]);
        LinkedHashSet<Index>[] castling = Arrays.asList(this.castling).toArray(new LinkedHashSet[this.castling.length]);
        
        Index from = move.getFrom();
        Index to = move.getTo();
        
        Piece piece = board[from.rank][from.file];

        int rule50Counter = piece != PAWN[color.ordinal()] && board[to.rank][to.file] == EMPTY ? this.rule50Counter + 1 : 0;
        
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
            if (to == enPassant) {
                board[from.rank][to.file] = EMPTY;
            } else if (move instanceof Promotion) {
                board[to.rank][to.file] = ((Promotion) move).getPromotion();
            }
        }
        
        castling[color.ordinal()].remove(from);
        castling[color.opposite().ordinal()].remove(to);

        Index enPassant = piece == PAWN[color.ordinal()] && Math.abs(to.rank - from.rank) == 2 ? Index.values()[(from.ordinal() + to.ordinal()) / 2] : NO_INDEX;
        
        int halfMoveCounter = this.halfMoveCounter + 1;

        Side color = this.color.opposite();
        
        return new State(board, color, castling, enPassant, rule50Counter, halfMoveCounter);
    }
    
    final private static String SLASH = "/";
    final private static String NONE = "-";
    
    public static State fromFEN(String fenString) {
        StringTokenizer tokenizer = new StringTokenizer(fenString);
        
        Piece[][] board = new Piece[RANK.length()][FILE.length()];
        
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

        castling[Side.w.ordinal()] = new LinkedHashSet();
        castling[Side.b.ordinal()] = new LinkedHashSet();
        
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
        
        int rule50Counter = Integer.parseInt(tokenizer.nextToken());
        int halfMoveCounter = Integer.parseInt(tokenizer.nextToken());
        
        return new State(board, color, castling, enPassant, rule50Counter, halfMoveCounter);
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
        
        return String.join(SLASH, board) + " " + color + " " + (QKqk.isEmpty() ? NONE : QKqk) + " " + (enPassant == NO_INDEX ? NONE : enPassant) + " " + rule50Counter + " " + halfMoveCounter;
    }
}
