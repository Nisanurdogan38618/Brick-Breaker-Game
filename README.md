# 🧱 Brick Breaker Game

A classic arcade-style Brick Breaker game developed in **Java** using the **StdDraw** library. This project features advanced collision physics, an aiming mechanic, and smooth gameplay dynamics.

## 🎮 Features

* **Advanced Physics Engine:** Uses vector math (dot product) to calculate realistic bounce angles when the ball hits the corners of a brick, rather than simple direction flipping.
* **Aim & Launch System:** Players can adjust the launch angle (0° - 180°) using a visual trajectory line before starting the round.
* **Pause Functionality:** The game can be paused and resumed at any time by pressing the Space bar.
* **Dynamic Scoring:** Score increases by 10 points for every brick destroyed.
* **Visual Feedback:** Features colorful brick patterns, a trajectory guide, and game state overlays (Victory/Game Over).

## 🕹️ Controls

The controls change slightly depending on the game state:

| Key | Before Launch (Aiming) | During Gameplay |
| :--- | :--- | :--- |
| **⬅️ Left Arrow** | Rotate launch angle left | Move paddle left |
| **➡️ Right Arrow** | Rotate launch angle right | Move paddle right |
| **Space Bar** | Launch the ball | Pause / Resume Game |

## 🛠️ Installation & Execution

To run this game, you need the **Java Development Kit (JDK)** installed on your machine.

### Prerequisites
* Java 8 or higher.
* `StdDraw` library (Princeton Standard Library).

### How to Run

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/brick-breaker.git](https://github.com/YOUR_USERNAME/brick-breaker.git)
    cd brick-breaker
    ```

2.  **Add the Library:**
    * Download `StdDraw.java` and place it in the same directory as `Main.java`.
    * *Alternatively*, add `stdlib.jar` to your project's classpath.

3.  **Compile and Run:**
    Open your terminal or command prompt and run:
    ```bash
    javac Main.java
    java Main
    ```

## 🧠 Code Highlights

The project is structured within `Main.java` using modular methods:

* **`checkCornerCollision()`**: Handles the complex physics of corner impacts. It calculates the collision normal and reflects the ball's velocity vector using the dot product formula.
* **`handleInput()`**: Manages state-dependent input (adjusting the `launchAngle` before the game starts vs. moving the paddle during the game).
* **`initializeBricks()`**: Generates the level layout using a coordinate array and assigns colors in a repeating pattern.
* **`updateBall()`**: Updates the ball's position and checks for boundary, paddle, and simple brick collisions.

## 📷 Screenshots

*(You can add a screenshot of your gameplay here)*

## 📝 License

This project is open-source and intended for educational purposes. Feel free to modify and distribute it.
