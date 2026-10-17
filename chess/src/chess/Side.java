package chess;

public enum Side {
    w {
        @Override
        public Side opposite() {
            return b;
        }
    },
    b {
        @Override
        public Side opposite() {
            return w;
        }
    };
    
    public abstract Side opposite();
}
