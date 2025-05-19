import java.util.*;

public class Main {
    public static void main(String[] args) {
        TicTacToeAdvanced game = new TicTacToeAdvanced();
        game.startGame();
    }
}

class TicTacToeAdvanced {
    private final int size = 3;
    private char[][] board;
    private char currentPlayer;
    private final char player1Symbol = 'X';
    private final char player2Symbol = 'O';
    private boolean vsAI;
    private final Scanner scanner = new Scanner(System.in);

    private int player1Score = 0;
    private int player2Score = 0;
    private int draws = 0;

    private List<int[]> winningLine = new ArrayList<>();

    public void startGame() {
        System.out.println("Welcome to Tic Tac Toe! ");

        vsAI = chooseGameMode();

        boolean playAgain;
        do {
            initializeBoard();
            currentPlayer = player1Symbol;
            winningLine.clear();
            playRound();

            System.out.println("\nScores:");
            System.out.println("Player 1 (X): " + player1Score);
            System.out.println("Player 2 (O): " + (vsAI ? "AI" : "Player 2") + ": " + player2Score);
            System.out.println("Draws: " + draws);

            System.out.print("\nPlay another round? (y/n): ");
            playAgain = scanner.nextLine().trim().equalsIgnoreCase("y");
        } while (playAgain);

        System.out.println("Thanks for playing!");
    }

    private boolean chooseGameMode() {
        while (true) {
            System.out.print("Choose mode: 1. Multiplayer  2. Play vs AI : ");
            String input = scanner.nextLine();
            if (input.equals("1")) return false;
            else if (input.equals("2")) return true;
            else System.out.println("Invalid choice.");
        }
    }

    private void initializeBoard() {
        board = new char[size][size];
        for (int i = 0; i < size; i++)
            Arrays.fill(board[i], ' ');
    }

    private void playRound() {
        boolean gameEnded = false;

        while (!gameEnded) {
            if (currentPlayer == player1Symbol || !vsAI) {
                playerMove();
            } else {
                aiMove();
            }

            printBoard();

            if (hasWon(currentPlayer)) {
                System.out.println("Player " + currentPlayer + " wins!");
                if (currentPlayer == player1Symbol) player1Score++;
                else player2Score++;
                highlightWinningLine();
                gameEnded = true;
            } else if (isBoardFull()) {
                System.out.println("It's a draw!");
                draws++;
                gameEnded = true;
            } else {
                switchPlayer();
            }
        }
    }

    private void printBoard() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(" ");
            for (int j = 0; j < size; j++) {
                char c = board[i][j];
                if (isInWinningLine(i, j)) {
                    System.out.print("[" + c + "]");
                } else {
                    System.out.print(" " + c + " ");
                }
                if (j < size - 1) System.out.print("|");
            }
            System.out.println();
            if (i < size - 1) {
                System.out.print(" ");
                for (int k = 0; k < size; k++) {
                    System.out.print("---");
                    if (k < size - 1) System.out.print("+");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    private boolean isInWinningLine(int row, int col) {
        for (int[] pos : winningLine) {
            if (pos[0] == row && pos[1] == col) return true;
        }
        return false;
    }

    private void playerMove() {
        while (true) {
            System.out.print("Player " + currentPlayer + " enter row (1-" + size + "): ");
            int row = getIntInput() - 1;
            System.out.print("Player " + currentPlayer + " enter column (1-" + size + "): ");
            int col = getIntInput() - 1;

            if (row < 0 || row >= size || col < 0 || col >= size) {
                System.out.println("Invalid position. Try again.");
                continue;
            }
            if (board[row][col] != ' ') {
                System.out.println("Spot already taken. Try again.");
                continue;
            }
            board[row][col] = currentPlayer;
            break;
        }
    }

    private void aiMove() {
        System.out.println("AI is making a move...");
        int[] bestMove = minimax(0, player2Symbol);
        board[bestMove[1]][bestMove[2]] = player2Symbol;
    }

    private int[] minimax(int depth, char player) {
        if (hasWon(player1Symbol)) return new int[]{-10 + depth, -1, -1};
        if (hasWon(player2Symbol)) return new int[]{10 - depth, -1, -1};
        if (isBoardFull()) return new int[]{0, -1, -1};

        List<int[]> moves = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = player;
                    int score;
                    if (player == player2Symbol) {
                        score = minimax(depth + 1, player1Symbol)[0];
                    } else {
                        score = minimax(depth + 1, player2Symbol)[0];
                    }
                    moves.add(new int[]{score, i, j});
                    board[i][j] = ' ';
                }
            }
        }

        int[] bestMove = null;
        if (player == player2Symbol) {
            int maxScore = Integer.MIN_VALUE;
            for (int[] move : moves) {
                if (move[0] > maxScore) {
                    maxScore = move[0];
                    bestMove = move;
                }
            }
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int[] move : moves) {
                if (move[0] < minScore) {
                    minScore = move[0];
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private boolean hasWon(char player) {
        winningLine.clear();

        for (int i = 0; i < size; i++) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != player) {
                    win = false;
                    break;
                }
            }
            if (win) {
                for (int j = 0; j < size; j++) winningLine.add(new int[]{i, j});
                return true;
            }
        }

        for (int j = 0; j < size; j++) {
            boolean win = true;
            for (int i = 0; i < size; i++) {
                if (board[i][j] != player) {
                    win = false;
                    break;
                }
            }
            if (win) {
                for (int i = 0; i < size; i++) winningLine.add(new int[]{i, j});
                return true;
            }
        }

        boolean win = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) {
                win = false;
                break;
            }
        }
        if (win) {
            for (int i = 0; i < size; i++) winningLine.add(new int[]{i, i});
            return true;
        }


        win = true;
        for (int i = 0; i < size; i++) {
            if (board[i][size - 1 - i] != player) {
                win = false;
                break;
            }
        }
        if (win) {
            for (int i = 0; i < size; i++) winningLine.add(new int[]{i, size - 1 - i});
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board[i][j] == ' ') return false;
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1Symbol) ? player2Symbol : player1Symbol;
    }

    private int getIntInput() {
        while (true) {
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private void highlightWinningLine() {
        System.out.println("\nWinning line highlighted below (in brackets []):");
        printBoard();
    }
}
