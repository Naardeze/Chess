package chess;

public enum Side {
    w {
        @Override
        public Side flip() {
            return b;
        }
    },
    b {
        @Override
        public Side flip() {
            return w;
        }
    };
    
    public abstract Side flip();
}
