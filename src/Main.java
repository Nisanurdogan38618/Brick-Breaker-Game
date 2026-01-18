import java.awt.*;
import java.awt.event.KeyEvent;

public class Main{
    // Game settings and properties and defining constants
    static double xScale = 800.0, yScale = 400.0;
    static double ballRadius = 8;
    static double ballVelocity = 5;
    static Color ballColor = new Color(15, 82, 186);
    static double[] ballPos = {400, 18};
    static double[] ballVel = {0, 0};
    static double[] paddlePos = {400, 5};
    static double paddleHalfwidth = 60;
    static double paddleHalfheight = 5;
    static double paddleSpeed = 20;
    static Color paddleColor = new Color(128, 128, 128);
    static double brickHalfwidth = 50;
    static double brickHalfheight = 10;
    static boolean[][] bricks; // Array to store brick states (true = exists, false = broken)
    static Color[] brickColors;
    static double[][] brickCoordinates;
    static boolean gameRunning = false; // Game state flag
    static boolean ballLaunched = false; // Ball launch flag
    static boolean gamePaused = false; // Pause flag
    static int score = 0;
    static double launchAngle = 45;
    static int lastBrokenBrickIndex = -1; // Stores the last broken brick index


    public static void main(String[] args) {
        // Initialize game window and settings
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, xScale);
        StdDraw.setYscale(0.0, yScale);
        StdDraw.enableDoubleBuffering(); // Enables smooth rendering
        initializeBricks(); // Generate bricks on the screen

        // Waiting for player to launch the ball
        while (!gameRunning) {
            StdDraw.clear(); // Clear screen
            drawBricks();
            drawPaddle();
            drawBall();
            drawScore();
            drawLaunchAngle();
            drawLaunchLine();
            StdDraw.show(); // Render frame

            // Calculate launch angle
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                launchAngle += 2; // Decrease angle when the left arrow is pressed
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                launchAngle -= 2; // Increase angle when the right arrow is pressed
            }

            // Ensure the angle remains within a valid range (0° to 180°)
            launchAngle = Math.max(0, Math.min(launchAngle, 180));

            // Convert the angle to radians for further calculations
            double launchAngleRadians = Math.toRadians(launchAngle);


            // If space key is pressed, start the game and launch the ball
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                gameRunning = true;
                ballLaunched = true;
                ballVel[0] = ballVelocity * Math.cos(Math.toRadians(launchAngle));
                ballVel[1] = ballVelocity * Math.sin(Math.toRadians(launchAngle));
                StdDraw.pause(200); // Short delay to prevent accidental multiple presses
            }
            StdDraw.pause(20); // Control frame rate
        }

        // Main game loop
        while (true) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.filledRectangle(xScale / 2, yScale / 2, xScale / 2, yScale / 2); // Clear screen with white

            if (!gameRunning) { // If game is over
                StdDraw.clear();
                drawBricks(); // Show remaining bricks
                drawPaddle();
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 48));
                StdDraw.text(400, 200, "Game Over!"); // Display Game Over text
                StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
                StdDraw.text(400, 160, "Final Score: " + score); // Display final score
                StdDraw.show();
                StdDraw.pause(3000); // Wait 3 seconds
                System.exit(0); // Exit game
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && ballLaunched && gameRunning) {
                gamePaused = !gamePaused; // Toggle pause state
                StdDraw.pause(200); // Small delay to avoid multiple toggles
            }

            // Draw game objects
            drawBricks();
            drawPaddle();
            drawBall();
            drawScore();
            drawLaunchAngle();
            drawLaunchLine();


            if (gamePaused) {
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.text(50, 380, "PAUSED"); // Display "PAUSED"
                StdDraw.show();
                continue; // Skip the rest of the loop when paused
            }

            if (gameRunning && ballLaunched) {
                updateBall(); // Update ball movement and collisions
            }
            handleInput();
            checkWinCondition();

            StdDraw.pause(20); // Control frame rate
            StdDraw.show(); // Render frame
        }
    }

    /**
     * Checks for collisions between the ball and the corners of the bricks.
     * If a collision occurs, the ball's velocity is adjusted accordingly, and the brick is removed.
     */
    private static void checkCornerCollision() {
        boolean collisionOccurred = false;
        double reflectionX = 0, reflectionY = 0;
        int collisionCount = 0;

        for (int i = 0; i < brickCoordinates.length; i++) {
            if (!bricks[i][0]) continue; // Skip if the brick is already removed

            double brickX = brickCoordinates[i][0];
            double brickY = brickCoordinates[i][1];

            // Define the four corners of the brick
            double[][] corners = {
                    {brickX - brickHalfwidth, brickY - brickHalfheight}, // Bottom-left corner
                    {brickX + brickHalfwidth, brickY - brickHalfheight}, // Bottom-right corner
                    {brickX - brickHalfwidth, brickY + brickHalfheight}, // Top-left corner
                    {brickX + brickHalfwidth, brickY + brickHalfheight}  // Top-right corner
            };

            // Check each corner for a collision with the ball
            for (double[] corner : corners) {
                double dx = ballPos[0] - corner[0];
                double dy = ballPos[1] - corner[1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= ballRadius) {
                    // Calculate the normal vector at the point of collision
                    double normalX = dx / distance;
                    double normalY = dy / distance;

                    // Reflect the velocity vector based on the normal
                    double dotProduct = ballVel[0] * normalX + ballVel[1] * normalY;
                    reflectionX += -2 * dotProduct * normalX;
                    reflectionY += -2 * dotProduct * normalY;
                    collisionCount++;

                    // Remove the brick and increase the score
                    bricks[i][0] = false;
                    score += 10;
                    collisionOccurred = true;
                }
            }
        }

        // Apply the reflection vector if at least one collision occurred
        if (collisionOccurred) {
            ballVel[0] += reflectionX / collisionCount;
            ballVel[1] += reflectionY / collisionCount;
        }
    }

    /**
     * Initializes the brick field by defining their positions and colors.
     * Also, marks all bricks as unbroken.
     */
    private static void initializeBricks() {
        brickCoordinates = new double[][]{
                {250, 320},{350, 320},{450, 320},{550, 320},
                {150,300},{250, 300},{350, 300},{450, 300},{550, 300},{650, 300},
                {50, 280},{150, 280},{250, 280},{350, 280},{450, 280},{550, 280},{650, 280},{750, 280},
                {50, 260},{150, 260},{250, 260},{350, 260},{450, 260},{550, 260},{650, 260},{750, 260},
                {50, 240},{150, 240},{250, 240},{350, 240},{450, 240},{550, 240},{650, 240},{750, 240},
                {150, 220},{250, 220},{350, 220},{450, 220},{550, 220},{650, 220},
                {250, 200},{350, 200},{450, 200},{550, 200}};

        bricks = new boolean[brickCoordinates.length][1];
        for (int i = 0; i < bricks.length; i++) {
            bricks[i][0] = true; // Mark all bricks as present
        }

        // Define brick colors
        Color[] colors = { new Color(255, 0, 0), new Color(220, 20, 60),
                new Color(178, 34, 34), new Color(139, 0, 0),
                new Color(255, 69, 0), new Color(165, 42, 42) };

        // Assign colors to bricks in a pattern
        brickColors = new Color[brickCoordinates.length];
        for (int i = 0; i < brickColors.length; i++) {
            brickColors[i] = colors[i % colors.length];
        }
    }

    /**
     * Draws all the bricks that have not been broken yet.
     */
    private static void drawBricks() {
        for (int i = 0; i < brickCoordinates.length; i++) {
            if (bricks[i][0]) {
                StdDraw.setPenColor(brickColors[i % brickColors.length]);
                StdDraw.filledRectangle(brickCoordinates[i][0], brickCoordinates[i][1], brickHalfwidth, brickHalfheight);
            }
        }
    }

    private static void drawPaddle() {
        StdDraw.setPenColor(paddleColor);
        StdDraw.filledRectangle(paddlePos[0], paddlePos[1], paddleHalfwidth, paddleHalfheight);
    }

    private static void drawBall() {
        StdDraw.setPenColor(ballColor);
        StdDraw.filledCircle(ballPos[0], ballPos[1], ballRadius);
    }

    private static void drawScore() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(750, 380, "Score: " + score);
    }

    private static void drawLaunchAngle() {
        if (!ballLaunched) {
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(50, 380, "Angle: " + (int) launchAngle + "°");
        }
    }
    /**
     *draws the red arrow indicating the angle at which the ball will be launched
     */
    private static void drawLaunchLine() {
        if (!ballLaunched) {
            StdDraw.setPenColor(Color.RED);
            double endX = ballPos[0] + 50 * Math.cos(Math.toRadians(launchAngle));
            double endY = ballPos[1] + 50 * Math.sin(Math.toRadians(launchAngle));
            StdDraw.line(ballPos[0], ballPos[1], endX, endY);
        }
    }

    /**
     * Updates the ball's position based on its current velocity.
     * Also checks for collisions with walls, the paddle, and bricks.
     */
    private static void updateBall() {
        // Update the ball's position by adding the velocity to the current position
        ballPos[0] += ballVel[0];
        ballPos[1] += ballVel[1];

        // Check for collision with the left or right walls
        if (ballPos[0] - ballRadius <= 0 || ballPos[0] + ballRadius >= xScale) {
            ballVel[0] = -ballVel[0]; // Reverse the horizontal velocity
        }

        // Check for collision with the top wall
        if (ballPos[1] + ballRadius >= yScale) {
            ballVel[1] = -ballVel[1]; // Reverse the vertical velocity
        }

        // Check if the ball has fallen below the bottom boundary (game over condition)
        if (ballPos[1] - ballRadius <= 0) {
            gameRunning = false; // Stop the game
            ballLaunched = false; // Reset the ball launch status
        }

        // Check for collisions with the paddle, bricks, and brick corners
        checkPaddleCollision();
        checkBrickCollision();
        checkCornerCollision();
    }

    /**
     * Checks if the ball collides with any brick.
     * If a collision is detected, the brick is removed and the ball's velocity is updated.
     */
    private static void checkBrickCollision() {
        for (int i = 0; i < brickCoordinates.length; i++) {
            // Check if the brick is still present and if the ball is within its bounding box
            if (bricks[i][0] && Math.abs(ballPos[0] - brickCoordinates[i][0]) <= brickHalfwidth &&
                    Math.abs(ballPos[1] - brickCoordinates[i][1]) <= brickHalfheight) {
                bricks[i][0] = false; // Remove the brick
                ballVel[1] = -ballVel[1]; // Reverse the vertical velocity to simulate bounce
                score += 10;
                break; // Stop checking further once a brick is hit
            }
        }
    }

    /**
     *It makes the vertical velocity change when the ball hits the paddle
     */
    private static void checkPaddleCollision() {
        if (ballPos[1] - ballRadius <= paddlePos[1] + paddleHalfheight &&
                ballPos[0] >= paddlePos[0] - paddleHalfwidth &&
                ballPos[0] <= paddlePos[0] + paddleHalfwidth) {
            ballVel[1] = Math.abs(ballVel[1]);
        }
    }

    /**
     * Handles user input for moving the paddle and launching the ball.
     */
    private static void handleInput() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && paddlePos[0] - paddleHalfwidth > 0) {
            paddlePos[0] -= paddleSpeed;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && paddlePos[0] + paddleHalfwidth < xScale) {
            paddlePos[0] += paddleSpeed;
        }
        if (!ballLaunched) {
            launchAngle = Math.toDegrees(Math.atan2(StdDraw.mouseY() - ballPos[1], StdDraw.mouseX() - ballPos[0]));
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && !ballLaunched) {
            ballLaunched = true;
            gameRunning = true;
            ballVel[0] = ballVelocity * Math.cos(Math.toRadians(launchAngle));
            ballVel[1] = ballVelocity * Math.sin(Math.toRadians(launchAngle));
        }
    }


    /**
     * Checks if all bricks have been broken and ends the game if so.
     */
    private static void checkWinCondition() {
        for (boolean[] brick : bricks) {
            if (brick[0]) {
                return; // At least one brick is still present
            }
        }

        // All bricks are destroyed, display victory message and exit
        gameRunning = false;
        ballLaunched = false;
        StdDraw.clear();
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 48));
        StdDraw.text(400, 220, "Victory!");
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
        StdDraw.text(400, 180, "Final Score: " + score);
        StdDraw.show();
        StdDraw.pause(3000);
        System.exit(0);
    }
}