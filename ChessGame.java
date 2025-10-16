import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChessGame extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGame::new);
    }

    // --- UI ---
    private final BoardPanel boardPanel;
    private final JLabel status;
    private final JButton newGameButton;
    private final JComboBox<String> difficultyComboBox;
    private final JCheckBox playAsWhiteCheckbox;
    private BotDifficulty currentDifficulty = BotDifficulty.MEDIUM;
    private boolean humanPlaysWhite = true;

    public ChessGame() {
        super("Java Chess — Single File");
        this.boardPanel = new BoardPanel();
        this.status = new JLabel("White to move");
        status.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        newGameButton = new JButton("New Game");
        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setSelectedIndex(1);
        playAsWhiteCheckbox = new JCheckBox("Play as White", true);

        controlPanel.add(newGameButton);
        controlPanel.add(new JLabel("Bot Level:"));
        controlPanel.add(difficultyComboBox);
        controlPanel.add(playAsWhiteCheckbox);

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // allow resizing so it's responsive
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Repaint board on resize to ensure responsive redraw
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardPanel.repaint();
            }
        });

        boardPanel.onStatusChange = s -> SwingUtilities.invokeLater(() -> status.setText(s));
        boardPanel.onGameOver = winner -> SwingUtilities.invokeLater(() -> showWinnerPopup(winner));

        // Add event listeners
        newGameButton.addActionListener(e -> startNewGame());
        difficultyComboBox.addActionListener(e -> updateDifficulty());
        playAsWhiteCheckbox.addActionListener(e -> updatePlayerColor());

        // Initialize the game
        startNewGame();
    }

    private void startNewGame() {
        humanPlaysWhite = playAsWhiteCheckbox.isSelected();
        boardPanel.startNewGame(humanPlaysWhite, currentDifficulty);
        updateStatusText();
    }

    private void updateDifficulty() {
        String selected = (String) difficultyComboBox.getSelectedItem();
        switch (selected) {
            case "EASY": currentDifficulty = BotDifficulty.EASY; break;
            case "MEDIUM": currentDifficulty = BotDifficulty.MEDIUM; break;
            case "HARD": currentDifficulty = BotDifficulty.HARD; break;
        }
        boardPanel.setBotDifficulty(currentDifficulty);
    }

    private void updatePlayerColor() {
        boolean newHumanPlaysWhite = playAsWhiteCheckbox.isSelected();

        // Only restart if the color actually changed
        if (newHumanPlaysWhite != humanPlaysWhite) {
            humanPlaysWhite = newHumanPlaysWhite;
            startNewGame();
        }
    }

    private void updateStatusText() {
        if (boardPanel.board.gameOver) {
            return;
        }

        if (humanPlaysWhite) {
            status.setText(boardPanel.board.turn == ColorSide.WHITE ? "Your turn (White)" : "Bot thinking...");
        } else {
            status.setText(boardPanel.board.turn == ColorSide.BLACK ? "Your turn (Black)" : "Bot thinking...");
        }
    }

    private void showWinnerPopup(ColorSide winner) {
        boolean humanWon = (winner == ColorSide.WHITE && humanPlaysWhite) ||
                (winner == ColorSide.BLACK && !humanPlaysWhite);

        // Create a custom dialog with confetti
        JDialog winnerDialog = new JDialog(this, "Game Over", true);
        winnerDialog.setLayout(new BorderLayout());
        winnerDialog.setSize(400, 300);
        winnerDialog.setLocationRelativeTo(this);
        winnerDialog.setResizable(false);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 240, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Winner message
        String message;
        String title;
        Color backgroundColor;

        if (humanWon) {
            message = "<html><div style='text-align: center;'>" +
                    "<font size='5' color='#2E8B57'>🎉 CONGRATULATIONS! 🎉</font><br><br>" +
                    "<font size='4'>You are a Chess Master!</font><br><br>" +
                    "Your brilliant strategy defeated the bot!<br>" +
                    "Want to play again?</div></html>";
            title = "VICTORY!";
            backgroundColor = new Color(230, 255, 230);
        } else {
            message = "<html><div style='text-align: center;'>" +
                    "<font size='5' color='#DC143C'>💀 BOT WINS! 💀</font><br><br>" +
                    "<font size='4'>Better luck next time!</font><br><br>" +
                    "The bot outsmarted you this round.<br>" +
                    "Ready for a rematch?</div></html>";
            title = "DEFEAT";
            backgroundColor = new Color(255, 230, 230);
        }

        contentPanel.setBackground(backgroundColor);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton newGameBtn = new JButton("New Game");
        JButton exitBtn = new JButton("Exit");

        newGameBtn.addActionListener(e -> {
            winnerDialog.dispose();
            startNewGame();
        });

        exitBtn.addActionListener(e -> {
            winnerDialog.dispose();
            System.exit(0);
        });

        buttonPanel.add(newGameBtn);
        buttonPanel.add(exitBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        winnerDialog.add(contentPanel);
        winnerDialog.setTitle(title);

        // Add confetti effect - FIXED VERSION
        ConfettiPanel confettiPanel = new ConfettiPanel(400, 300); // Pass explicit dimensions
        winnerDialog.setGlassPane(confettiPanel);
        confettiPanel.setVisible(true);

        winnerDialog.setVisible(true);
    }

    // Confetti animation panel - FIXED VERSION
    static class ConfettiPanel extends JPanel {
        private final java.util.List<Confetti> confettiList = new ArrayList<>();
        private final javax.swing.Timer animationTimer;
        private final Random random = new Random();
        private final int width;
        private final int height;

        ConfettiPanel(int width, int height) {
            this.width = width;
            this.height = height;
            setOpaque(false);
            setPreferredSize(new Dimension(width, height));

            // Create initial confetti - use the known dimensions
            for (int i = 0; i < 50; i++) {
                confettiList.add(new Confetti(width, height));
            }

            animationTimer = new javax.swing.Timer(50, e -> {
                for (Confetti confetti : confettiList) {
                    confetti.update(width, height);
                }
                repaint();
            });
            animationTimer.start();

            // Stop animation after 5 seconds
            new javax.swing.Timer(5000, e -> animationTimer.stop()).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Confetti confetti : confettiList) {
                g2.setColor(confetti.color);
                g2.fillRect(confetti.x, confetti.y, confetti.size, confetti.size);
            }
        }

        class Confetti {
            int x, y;
            int size;
            Color color;
            double speed;
            double angle;

            Confetti(int width, int height) {
                // Use the known dimensions instead of getWidth()/getHeight()
                x = random.nextInt(width);
                y = -random.nextInt(100);
                size = random.nextInt(10) + 5;
                color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                speed = random.nextDouble() * 3 + 2;
                angle = random.nextDouble() * Math.PI * 2;
            }

            void update(int width, int height) {
                y += speed;
                x += Math.cos(angle) * 2;
                angle += 0.1;

                // Reset if out of bounds - use the known dimensions
                if (y > height || x < -size || x > width) {
                    x = random.nextInt(width);
                    y = -random.nextInt(100);
                    speed = random.nextDouble() * 3 + 2;
                }
            }
        }
    }

    // --- Game Model ---
    enum ColorSide { WHITE, BLACK;
        ColorSide opposite(){return this==WHITE?BLACK:WHITE;}
    }

    enum BotDifficulty {
        EASY(1, 500), MEDIUM(2, 300), HARD(3, 200);

        final int searchDepth;
        final int moveDelay;

        BotDifficulty(int depth, int delay) {
            this.searchDepth = depth;
            this.moveDelay = delay;
        }
    }

    static final class Pos {
        final int r, c;
        Pos(int r,int c){this.r=r;this.c=c;}
        public boolean equals(Object o){
            if(!(o instanceof Pos)) return false;
            Pos p=(Pos)o; return r==p.r&&c==p.c;
        }
        public int hashCode(){ return Objects.hash(r,c); }
        public String toString(){ return "("+r+","+c+")"; }
    }

    static abstract class Piece {
        final ColorSide side;
        Piece(ColorSide s){this.side=s;}
        abstract char fen();
        abstract java.util.List<Pos> pseudoLegal(Board b, Pos from);
        boolean isKing(){return false;}
    }

    static class King extends Piece {
        King(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'K':'k';}
        boolean isKing(){return true;}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            for(int dr=-1;dr<=1;dr++) for(int dc=-1;dc<=1;dc++) if(!(dr==0&&dc==0)){
                int r=f.r+dr, c=f.c+dc;
                if(b.in(r,c) && b.emptyOrEnemy(r,c,side)) m.add(new Pos(r,c));
            }
            return m;
        }
    }

    static class Queen extends Piece {
        Queen(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'Q':'q';}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            slide(b,f,m,1,0); slide(b,f,m,-1,0); slide(b,f,m,0,1); slide(b,f,m,0,-1);
            slide(b,f,m,1,1); slide(b,f,m,1,-1); slide(b,f,m,-1,1); slide(b,f,m,-1,-1);
            return m;
        }
    }

    static class Rook extends Piece {
        Rook(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'R':'r';}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            slide(b,f,m,1,0); slide(b,f,m,-1,0); slide(b,f,m,0,1); slide(b,f,m,0,-1);
            return m;
        }
    }

    static class Bishop extends Piece {
        Bishop(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'B':'b';}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            slide(b,f,m,1,1); slide(b,f,m,1,-1); slide(b,f,m,-1,1); slide(b,f,m,-1,-1);
            return m;
        }
    }

    static class Knight extends Piece {
        Knight(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'N':'n';}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            int[][] d={{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
            for(int[] x:d){
                int r=f.r+x[0], c=f.c+x[1];
                if(b.in(r,c) && b.emptyOrEnemy(r,c,side)) m.add(new Pos(r,c));
            }
            return m;
        }
    }

    static class Pawn extends Piece {
        Pawn(ColorSide s){super(s);}
        char fen(){return side==ColorSide.WHITE?'P':'p';}
        java.util.List<Pos> pseudoLegal(Board b, Pos f){
            java.util.List<Pos> m=new ArrayList<>();
            int dir = (side==ColorSide.WHITE)?-1:1;
            int startRow = (side==ColorSide.WHITE)?6:1;
            int r1=f.r+dir;
            if(b.in(r1,f.c) && b.empty(r1,f.c)) {
                m.add(new Pos(r1,f.c));
                int r2=f.r+2*dir;
                if(f.r==startRow && b.in(r2,f.c) && b.empty(r2,f.c)) m.add(new Pos(r2,f.c));
            }
            // captures
            for(int dc: new int[]{-1,1}){
                int rc=f.r+dir, cc=f.c+dc;
                if(b.in(rc,cc) && b.enemy(rc,cc,side)) m.add(new Pos(rc,cc));
            }
            return m;
        }
    }

    static void slide(Board b, Pos f, java.util.List<Pos> m, int dr, int dc){
        int r=f.r+dr, c=f.c+dc;
        while(b.in(r,c)){
            if(b.empty(r,c)) {
                m.add(new Pos(r,c));
            } else {
                if(b.enemy(r,c,b.get(f).side)) m.add(new Pos(r,c));
                break;
            }
            r+=dr; c+=dc;
        }
    }

    static final class Move {
        final Pos from,to;
        final Piece captured;
        final Piece moved;
        final boolean wasPromotion;
        Move(Pos f, Pos t, Piece cap, Piece moved, boolean wasPromotion){
            this.from=f; this.to=t; this.captured=cap; this.moved=moved;
            this.wasPromotion=wasPromotion;
        }
    }

    static class Board {
        Piece[][] g = new Piece[8][8];
        ColorSide turn = ColorSide.WHITE;
        boolean gameOver = false;
        ColorSide winner = null;

        Board(){ setup(); }

        void setup(){
            // Clear the board
            for(int r = 0; r < 8; r++) {
                for(int c = 0; c < 8; c++) {
                    g[r][c] = null;
                }
            }

            // place pieces
            for(int c=0;c<8;c++){
                g[1][c]=new Pawn(ColorSide.BLACK);
                g[6][c]=new Pawn(ColorSide.WHITE);
            }
            g[0][0]=new Rook(ColorSide.BLACK); g[0][7]=new Rook(ColorSide.BLACK);
            g[7][0]=new Rook(ColorSide.WHITE); g[7][7]=new Rook(ColorSide.WHITE);
            g[0][1]=new Knight(ColorSide.BLACK); g[0][6]=new Knight(ColorSide.BLACK);
            g[7][1]=new Knight(ColorSide.WHITE); g[7][6]=new Knight(ColorSide.WHITE);
            g[0][2]=new Bishop(ColorSide.BLACK); g[0][5]=new Bishop(ColorSide.BLACK);
            g[7][2]=new Bishop(ColorSide.WHITE); g[7][5]=new Bishop(ColorSide.WHITE);
            g[0][3]=new Queen(ColorSide.BLACK); g[7][3]=new Queen(ColorSide.WHITE);
            g[0][4]=new King(ColorSide.BLACK); g[7][4]=new King(ColorSide.WHITE);

            // Reset game state
            turn = ColorSide.WHITE;
            gameOver = false;
            winner = null;
        }

        boolean in(int r,int c){ return 0<=r && r<8 && 0<=c && c<8; }
        boolean empty(int r,int c){ return g[r][c]==null; }
        boolean enemy(int r,int c, ColorSide s){
            return g[r][c]!=null && g[r][c].side!=s;
        }
        boolean emptyOrEnemy(int r,int c, ColorSide s){
            return empty(r,c) || enemy(r,c,s);
        }
        Piece get(Pos p){ return g[p.r][p.c]; }

        java.util.List<Pos> legalMoves(Pos from){
            Piece p=get(from);
            if(p==null || p.side!=turn) return Collections.emptyList();
            java.util.List<Pos> cand=p.pseudoLegal(this, from);
            java.util.List<Pos> legal=new ArrayList<>();
            for(Pos to:cand){
                if(isLegalMove(from,to)) legal.add(to);
            }
            return legal;
        }

        boolean isLegalMove(Pos from, Pos to){
            Piece p=get(from);
            if(p==null || p.side!=turn) return false;

            // Check if the move is in pseudo-legal moves
            boolean listed=false;
            for(Pos x:p.pseudoLegal(this, from))
                if(x.equals(to)) {listed=true; break;}
            if(!listed) return false;

            // simulate and check if king is in check after move
            Move mv = makeMove(from,to, false); // false = don't check for king capture
            boolean ok = !inCheck(turn);
            undoMove(mv);
            return ok;
        }

        Move makeMove(Pos from, Pos to, boolean checkKingCapture){
            Piece p=get(from);
            Piece cap=get(to);
            boolean promoting = (p instanceof Pawn) &&
                    ((p.side==ColorSide.WHITE && to.r==0) ||
                            (p.side==ColorSide.BLACK && to.r==7));

            // Check for king capture (only if requested)
            if (checkKingCapture && cap != null && cap.isKing()) {
                winner = p.side;
                gameOver = true;
            }

            g[to.r][to.c] = promoting ? new Queen(p.side) : p;
            g[from.r][from.c] = null;
            turn = turn.opposite();
            return new Move(from,to,cap,p,promoting);
        }

        // Overloaded method for backward compatibility
        Move makeMove(Pos from, Pos to) {
            return makeMove(from, to, true);
        }

        void undoMove(Move mv){
            Piece atTo = g[mv.to.r][mv.to.c];
            g[mv.from.r][mv.from.c] = mv.wasPromotion ? mv.moved : atTo;
            g[mv.to.r][mv.to.c] = mv.captured;
            turn = turn.opposite();

            if (gameOver) {
                gameOver = false;
                winner = null;
            }
        }

        boolean inCheck(ColorSide side){
            Pos king=findKing(side);
            if(king==null) return false;
            return squareAttackedBy(king, side.opposite());
        }

        Pos findKing(ColorSide side){
            for(int r=0;r<8;r++) for(int c=0;c<8;c++){
                Piece p=g[r][c];
                if(p!=null && p.side==side && p.isKing()) return new Pos(r,c);
            }
            return null;
        }

        boolean squareAttackedBy(Pos target, ColorSide attacker){
            for(int r=0;r<8;r++) for(int c=0;c<8;c++){
                Piece p=g[r][c];
                if(p==null || p.side!=attacker) continue;
                Pos from=new Pos(r,c);

                // Special handling for pawn attacks
                if(p instanceof Pawn){
                    int dir = (p.side==ColorSide.WHITE)?-1:1;
                    for(int dc:new int[]{-1,1}){
                        int rr=r+dir, cc=c+dc;
                        if(in(rr,cc) && rr==target.r && cc==target.c) return true;
                    }
                } else {
                    // For other pieces, check if they can move to the target
                    for(Pos m:p.pseudoLegal(this, from))
                        if(m.r==target.r && m.c==target.c) return true;
                }
            }
            return false;
        }

        GameState computeState(){
            // First check if king is captured
            if (gameOver && winner != null) {
                return GameState.CHECKMATE;
            }

            boolean inC = inCheck(turn);
            boolean anyLegalMoves = hasAnyLegalMoves(turn);

            if(!anyLegalMoves){
                return inC ? GameState.CHECKMATE : GameState.STALEMATE;
            }
            return inC ? GameState.CHECK : GameState.NORMAL;
        }

        boolean hasAnyLegalMoves(ColorSide side) {
            for(int r=0;r<8;r++) {
                for(int c=0;c<8;c++) {
                    Piece p = g[r][c];
                    if(p==null || p.side!=side) continue;
                    Pos from = new Pos(r,c);
                    if(!legalMoves(from).isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }

        java.util.List<Move> getAllLegalMoves(ColorSide side) {
            java.util.List<Move> moves = new ArrayList<>();
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Pos from = new Pos(r, c);
                    Piece p = get(from);
                    if (p != null && p.side == side) {
                        for (Pos to : legalMoves(from)) {
                            moves.add(new Move(from, to, get(to), p, false));
                        }
                    }
                }
            }
            return moves;
        }
    }

    enum GameState { NORMAL, CHECK, CHECKMATE, STALEMATE }

    // --- Chess Bot ---
    static class ChessBot {
        private BotDifficulty difficulty;

        ChessBot(BotDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        void setDifficulty(BotDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        Move getBestMove(Board board, ColorSide botColor) {
            switch (difficulty) {
                case EASY: return getRandomMove(board, botColor);
                case MEDIUM: return getMinimaxMove(board, botColor, 2);
                case HARD: return getMinimaxMove(board, botColor, 3);
                default: return getRandomMove(board, botColor);
            }
        }

        private Move getRandomMove(Board board, ColorSide botColor) {
            java.util.List<Move> legalMoves = board.getAllLegalMoves(botColor);
            if (legalMoves.isEmpty()) return null;

            Random random = new Random();
            return legalMoves.get(random.nextInt(legalMoves.size()));
        }

        private Move getMinimaxMove(Board board, ColorSide botColor, int depth) {
            java.util.List<Move> legalMoves = board.getAllLegalMoves(botColor);
            if (legalMoves.isEmpty()) return null;

            Move bestMove = null;
            int bestValue = Integer.MIN_VALUE;

            for (Move move : legalMoves) {
                board.makeMove(move.from, move.to, false); // Don't check king capture during simulation
                int moveValue = minimax(board, depth - 1, false, botColor,
                        Integer.MIN_VALUE, Integer.MAX_VALUE);
                board.undoMove(move);

                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }

            return bestMove != null ? bestMove : getRandomMove(board, botColor);
        }

        private int minimax(Board board, int depth, boolean isMaximizing,
                            ColorSide botColor, int alpha, int beta) {
            if (depth == 0 || board.gameOver) {
                return evaluateBoard(board, botColor);
            }

            ColorSide currentSide = isMaximizing ? botColor : botColor.opposite();
            java.util.List<Move> legalMoves = board.getAllLegalMoves(currentSide);

            if (legalMoves.isEmpty()) {
                // Checkmate or stalemate
                if (board.inCheck(currentSide)) {
                    return isMaximizing ? -10000 : 10000;
                } else {
                    return 0; // Stalemate
                }
            }

            if (isMaximizing) {
                int maxEval = Integer.MIN_VALUE;
                for (Move move : legalMoves) {
                    board.makeMove(move.from, move.to, false);
                    int eval = minimax(board, depth - 1, false, botColor, alpha, beta);
                    board.undoMove(move);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
                return maxEval;
            } else {
                int minEval = Integer.MAX_VALUE;
                for (Move move : legalMoves) {
                    board.makeMove(move.from, move.to, false);
                    int eval = minimax(board, depth - 1, true, botColor, alpha, beta);
                    board.undoMove(move);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
                return minEval;
            }
        }

        private int evaluateBoard(Board board, ColorSide botColor) {
            if (board.gameOver && board.winner != null) {
                return board.winner == botColor ? 10000 : -10000;
            }

            int score = 0;

            // Material evaluation
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece piece = board.g[r][c];
                    if (piece != null) {
                        int pieceValue = getPieceValue(piece);
                        if (piece.side == botColor) {
                            score += pieceValue;
                        } else {
                            score -= pieceValue;
                        }
                    }
                }
            }

            return score;
        }

        private int getPieceValue(Piece piece) {
            if (piece instanceof King) return 10000;
            if (piece instanceof Queen) return 900;
            if (piece instanceof Rook) return 500;
            if (piece instanceof Bishop) return 300;
            if (piece instanceof Knight) return 300;
            if (piece instanceof Pawn) return 100;
            return 0;
        }
    }

    // --- UI Board Panel ---
    static class BoardPanel extends JPanel implements MouseListener {
        // base tile only used for initial preferred size — actual drawing uses dynamic tile
        final int BASE_TILE = 80;
        final Board board = new Board();
        final ChessBot bot = new ChessBot(BotDifficulty.MEDIUM);
        Pos selected = null;
        java.util.List<Pos> legal = Collections.emptyList();
        java.util.function.Consumer<String> onStatusChange = s -> {};
        java.util.function.Consumer<ColorSide> onGameOver = winner -> {};
        private boolean humanPlaysWhite = true;
        private BotDifficulty botDifficulty = BotDifficulty.MEDIUM;
        private javax.swing.Timer botTimer;

        BoardPanel(){
            // initial preferred size but allow resizing
            setPreferredSize(new Dimension(BASE_TILE*8, BASE_TILE*8));
            addMouseListener(this);
            setFocusable(true);

            botTimer = new javax.swing.Timer(0, e -> {
                if (!board.gameOver && board.turn != (humanPlaysWhite ? ColorSide.WHITE : ColorSide.BLACK)) {
                    makeBotMove();
                }
            });
            botTimer.setRepeats(false);
        }

        void startNewGame(boolean humanPlaysWhite, BotDifficulty difficulty) {
            this.humanPlaysWhite = humanPlaysWhite;
            this.botDifficulty = difficulty;
            bot.setDifficulty(difficulty);

            // Reset the board completely
            board.setup();
            selected = null;
            legal = Collections.emptyList();

            // Cancel any pending bot moves
            if (botTimer.isRunning()) {
                botTimer.stop();
            }

            updateGameState();

            // Start bot move if needed
            if (!humanPlaysWhite && !board.gameOver) {
                scheduleBotMove();
            }

            repaint();
        }

        void setBotDifficulty(BotDifficulty difficulty) {
            this.botDifficulty = difficulty;
            bot.setDifficulty(difficulty);
        }

        void setHumanPlaysWhite(boolean humanPlaysWhite) {
            this.humanPlaysWhite = humanPlaysWhite;
        }

        private void scheduleBotMove() {
            if (botTimer.isRunning()) {
                botTimer.stop();
            }
            botTimer.setInitialDelay(botDifficulty.moveDelay);
            botTimer.start();
        }

        private void makeBotMove() {
            if (board.gameOver || board.turn == (humanPlaysWhite ? ColorSide.WHITE : ColorSide.BLACK)) {
                return;
            }

            ColorSide botColor = humanPlaysWhite ? ColorSide.BLACK : ColorSide.WHITE;
            Move botMove = bot.getBestMove(board, botColor);
            if (botMove != null) {
                board.makeMove(botMove.from, botMove.to);
                updateGameState();
                repaint();
            }
        }

        private void updateGameState() {
            GameState st = board.computeState();

            if (board.gameOver) {
                if (st == GameState.CHECKMATE) {
                    ColorSide winner = board.turn.opposite();
                    onStatusChange.accept((winner == ColorSide.WHITE ? "White" : "Black") + " wins by checkmate!");
                    onGameOver.accept(winner);
                } else if (st == GameState.STALEMATE) {
                    onStatusChange.accept("Stalemate — Draw");
                }
                return;
            }

            // Update status based on who is playing
            if (humanPlaysWhite) {
                if (board.turn == ColorSide.WHITE) {
                    onStatusChange.accept("Your turn (White)");
                } else {
                    onStatusChange.accept("Bot thinking...");
                }
            } else {
                if (board.turn == ColorSide.BLACK) {
                    onStatusChange.accept("Your turn (Black)");
                } else {
                    onStatusChange.accept("Bot thinking...");
                }
            }

            // Add check indicator
            if (st == GameState.CHECK) {
                String currentStatus = "";
                if (humanPlaysWhite) {
                    currentStatus = (board.turn == ColorSide.WHITE) ? "Your turn (White) — CHECK!" : "Bot thinking... — CHECK!";
                } else {
                    currentStatus = (board.turn == ColorSide.BLACK) ? "Your turn (Black) — CHECK!" : "Bot thinking... — CHECK!";
                }
                onStatusChange.accept(currentStatus);
            }

            // Schedule bot move if it's bot's turn and game isn't over
            if (!board.gameOver && board.turn != (humanPlaysWhite ? ColorSide.WHITE : ColorSide.BLACK)) {
                scheduleBotMove();
            }
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // compute dynamic tile and offsets to keep board square & centered
            int boardSize = Math.min(getWidth(), getHeight());
            int tile = boardSize / 8;
            if (tile <= 0) return;
            int offsetX = (getWidth() - boardSize) / 2;
            int offsetY = (getHeight() - boardSize) / 2;

            // Draw squares
            for(int r=0;r<8;r++){
                for(int c=0;c<8;c++){
                    boolean light=((r+c)&1)==0;
                    g2.setColor(light? new Color(238,238,210): new Color(118,150,86));
                    g2.fillRect(offsetX + c*tile, offsetY + r*tile, tile, tile);
                }
            }

            // Highlight selection and legal dots (scaled)
            if(selected!=null){
                g2.setColor(new Color(246,246,105,180));
                g2.fillRect(offsetX + selected.c*tile, offsetY + selected.r*tile, tile, tile);
                g2.setColor(new Color(33,33,33,160));
                int dotSize = Math.max(4, tile/6);
                for(Pos m:legal){
                    int x = offsetX + m.c*tile + tile/2 - dotSize/2;
                    int y = offsetY + m.r*tile + tile/2 - dotSize/2;
                    g2.fillOval(x, y, dotSize, dotSize);
                }
            }

            // Draw pieces scaled
            int fontSize = Math.max(12, tile - (tile/6));
            g2.setFont(new Font(Font.SERIF, Font.PLAIN, fontSize));
            FontMetrics fm=g2.getFontMetrics();
            for(int r=0;r<8;r++){
                for(int c=0;c<8;c++){
                    Piece p=board.g[r][c]; if(p==null) continue;
                    String sym = pieceUnicode(p);
                    int x = offsetX + c*tile + (tile - fm.stringWidth(sym))/2;
                    int y = offsetY + r*tile + (tile + fm.getAscent()-fm.getDescent())/2;

                    if(p.side == ColorSide.WHITE) {
                        g2.setColor(new Color(255, 255, 255));
                        // draw a subtle outline for visibility on light tiles
                        g2.setStroke(new BasicStroke(Math.max(1, tile/40)));
                        g2.drawString(sym, x, y);
                        g2.setColor(new Color(0,0,0,180));
                        g2.drawString(sym, x, y);
                    } else {
                        g2.setColor(new Color(50, 50, 50));
                        g2.drawString(sym, x, y);
                    }
                }
            }
        }

        static String pieceUnicode(Piece p){
            if(p instanceof King) return p.side==ColorSide.WHITE?"\u2654":"\u265A";
            if(p instanceof Queen) return p.side==ColorSide.WHITE?"\u2655":"\u265B";
            if(p instanceof Rook) return p.side==ColorSide.WHITE?"\u2656":"\u265C";
            if(p instanceof Bishop) return p.side==ColorSide.WHITE?"\u2657":"\u265D";
            if(p instanceof Knight) return p.side==ColorSide.WHITE?"\u2658":"\u265E";
            if(p instanceof Pawn) return p.side==ColorSide.WHITE?"\u2659":"\u265F";
            return "?";
        }

        @Override public void mouseClicked(MouseEvent e){
            if(board.gameOver) {
                return;
            }

            // Check if it's human's turn
            if (board.turn != (humanPlaysWhite ? ColorSide.WHITE : ColorSide.BLACK)) {
                return;
            }

            // compute dynamic tile and offsets same as paintComponent
            int boardSize = Math.min(getWidth(), getHeight());
            int tile = boardSize / 8;
            if (tile <= 0) return;
            int offsetX = (getWidth() - boardSize) / 2;
            int offsetY = (getHeight() - boardSize) / 2;

            int ex = e.getX() - offsetX;
            int ey = e.getY() - offsetY;
            int c = ex / tile;
            int r = ey / tile;
            if(c < 0 || c >= 8 || r < 0 || r >= 8) return;
            Pos clicked=new Pos(r,c);

            if(selected == null){
                Piece p = board.get(clicked);
                if(p != null && p.side == board.turn){
                    selected = clicked;
                    legal = board.legalMoves(selected);
                    repaint();
                }
            } else {
                if(selected.equals(clicked)) {
                    selected = null;
                    legal = Collections.emptyList();
                    repaint();
                    return;
                }

                boolean isLegalMove = false;
                for(Pos m : legal) {
                    if(m.equals(clicked)){
                        isLegalMove = true;
                        break;
                    }
                }

                if(isLegalMove){
                    board.makeMove(selected, clicked);
                    selected = null;
                    legal = Collections.emptyList();
                    updateGameState();
                    repaint();
                } else {
                    Piece p = board.get(clicked);
                    if(p != null && p.side == board.turn){
                        selected = clicked;
                        legal = board.legalMoves(selected);
                        repaint();
                    } else {
                        selected = null;
                        legal = Collections.emptyList();
                        repaint();
                    }
                }
            }
        }

        @Override public void mousePressed(MouseEvent e){}
        @Override public void mouseReleased(MouseEvent e){}
        @Override public void mouseEntered(MouseEvent e){}
        @Override public void mouseExited(MouseEvent e){}
    }
}
