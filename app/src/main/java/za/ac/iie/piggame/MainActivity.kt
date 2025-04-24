package za.ac.iie.piggame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class  MainActivity : AppCompatActivity() {
    private var scores = intArrayOf(0, 0)
    private var turnScore = 0
    private var currentPlayerIndex = 0 // 0 = Player, 1 = PC

    private lateinit var currentPlayer: TextView
    private lateinit var currentScore: TextView
    private lateinit var score1: TextView
    private lateinit var score2: TextView
    private lateinit var ivDice: ImageView
    private lateinit var btnRoll: Button
    private lateinit var btnHold: Button

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentPlayer = findViewById(R.id.currentPlayer)
        currentScore = findViewById(R.id.currentScore)
        score1 = findViewById(R.id.score1)
        score2 = findViewById(R.id.score2)
        ivDice = findViewById(R.id.ivDice)
        btnRoll = findViewById(R.id.btnRoll)
        btnHold = findViewById(R.id.btnHold)

        btnRoll.setOnClickListener { if (currentPlayerIndex == 0) rollDice() }
        btnHold.setOnClickListener { if (currentPlayerIndex == 0) holdScore() }

        updateUI()
    }

    private fun rollDice() {
        val roll = Random.nextInt(1, 7)
        val diceResource = resources.getIdentifier("dice_$roll", "drawable", packageName)
        ivDice.setImageResource(diceResource)

        if (roll == 1) {
            turnScore = 0
            switchPlayer()
        } else {
            turnScore += roll
        }
        updateUI()
    }

    private fun holdScore() {
        scores[currentPlayerIndex] += turnScore
        turnScore = 0

        if (scores[currentPlayerIndex] >= 100) {
            Toast.makeText(this, if (currentPlayerIndex == 0) "You win!" else "PC wins!", Toast.LENGTH_LONG).show()
            resetGame()
        } else {
            switchPlayer()
        }
        updateUI()
    }

    private fun switchPlayer() {
        currentPlayerIndex = 1 - currentPlayerIndex
        updateUI()
        if (currentPlayerIndex == 1) {
            pcTurn()
        }
    }

    private fun pcTurn() {
        btnRoll.isEnabled = false
        btnHold.isEnabled = false

        var pcRolls = 0
        fun rollNext() {
            if (currentPlayerIndex != 1) return
            if (pcRolls >= Random.nextInt(2, 5) || turnScore >= 10) {
                handler.postDelayed({
                    holdScore()
                    btnRoll.isEnabled = true
                    btnHold.isEnabled = true
                }, 1000)
                return
            }

            handler.postDelayed({
                val roll = Random.nextInt(1, 7)
                val diceResource = resources.getIdentifier("dice_$roll", "drawable", packageName)
                ivDice.setImageResource(diceResource)

                if (roll == 1) {
                    turnScore = 0
                    switchPlayer()
                    btnRoll.isEnabled = true
                    btnHold.isEnabled = true
                    updateUI()
                    return@postDelayed
                } else {
                    turnScore += roll
                    pcRolls++
                    updateUI()
                    rollNext()
                }
            }, 1000)
        }

        rollNext()
    }

    private fun updateUI() {
        currentPlayer.text = if (currentPlayerIndex == 0) "Your Turn" else "PC's Turn"
        currentScore.text = "Turn Score: $turnScore"
        score1.text = "You: ${scores[0]}"
        score2.text = "PC: ${scores[1]}"
    }

    private fun resetGame() {
        scores = intArrayOf(0, 0)
        turnScore = 0
        currentPlayerIndex = 0
        btnRoll.isEnabled = true
        btnHold.isEnabled = true
        updateUI()
    }
}

