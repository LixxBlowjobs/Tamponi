package com.example.happynewyear

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.nio.file.Files.size

class MainActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализация вибрации
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // Инициализация MediaPlayer (если аудио существует)
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.last_christmas).apply {
                isLooping = false
                start()
            }
        } catch (e: Exception) {
            // Без звука — продолжаем работу (ёлка и текст с таймингом по умолчанию)
            mediaPlayer = null
        }

        setContent {
            ChristmasTreeApp(mediaPlayer, vibrator)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}

@Composable
fun ChristmasTreeApp(mediaPlayer: MediaPlayer?, vibrator: Vibrator?) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TreeCanvas(
                modifier = Modifier.weight(1f)
            )

            LyricsText(
                modifier = Modifier.weight(1f),
                mediaPlayer = mediaPlayer,
                vibrator = vibrator
            )
        }
    }
}


@Composable
fun TreeCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "TreeTransition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TreeBlink"
    )

    Canvas(modifier = modifier) {
        drawStarTree(progress)
    }
}

fun DrawScope.drawStarTree(frame: Float) {
    val centerX = size.width / 2
    val treeTop = size.height * 0.05f
    val levelHeight = size.height * 0.1f
    val trunkHeight = size.height * 0.1f
    val trunkWidth = size.width * 0.05f
    val trunkX = centerX - trunkWidth / 2
    val trunkY = treeTop + 9 * levelHeight

    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.Blue,
        Color.Magenta,
        Color.Cyan,
        Color.White
    )

    for (i in 0..8) {
        val levelY = treeTop + i * levelHeight
        val starsInLevel = i * 2 + 1
        val levelWidth = (i + 1) * size.width * 0.08f
        for (j in 0 until starsInLevel) {
            val starX = centerX - levelWidth / 2 + (j + 1) * (levelWidth / (starsInLevel + 1))
            val starY = levelY

            val colorIndex = (frame.toInt() + i * 7 + j * 11) % colors.size
            val color = colors[colorIndex]

            drawCircle(
                color = color,
                radius = size.width * 0.012f,
                center = Offset(starX, starY)
            )
        }
    }

    drawRect(
        color = Color(0xFF8B4513),
        topLeft = Offset(trunkX, trunkY),
        size = androidx.compose.ui.geometry.Size(trunkWidth, trunkHeight)
    )
}

@Composable
fun LyricsText(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer?,
    vibrator: Vibrator?
) {
    // Все таймкоды из .lrc (ms)
    val lyricsWithTime = listOf(
        17460L to "Last Christmas, I gave you my heart",
        21690L to "But the very next day you gave it away",
        26180L to "This year, to save me from tears",
        30710L to "I'll give it to someone special",
        35450L to "Last Christmas, I gave you my heart",
        39710L to "But the very next day you gave it away",
        44710L to "This year, to save me from tears",
        48960L to "I'll give it to someone special",
        72210L to "Once bitten and twice shy",
        76450L to "I keep my distance",
        77960L to "But you still catch my eye",
        81460L to "Tell me, baby",
        82960L to "Do you recognize me?",
        85210L to "Well, it's been a year",
        87710L to "It doesn't surprise me",
        91210L to "(Merry Christmas!) I wrapped it up and sent it",
        94460L to "With a note saying, \"I love you,\" I meant it",
        98710L to "Now, I know what a fool I've been",
        102460L to "But if you kissed me now",
        104960L to "I know you'd fool me again",
        107670L to "Last Christmas, I gave you my heart",
        111170L to "But the very next day you gave it away",
        113960L to "This year, to save me from tears",
        120710L to "I'll give it to someone special",
        125720L to "Last Christmas, I gave you my heart",
        129460L to "But the very next day you gave it away",
        138960L to "This year, to save me from tears",
        140460L to "I'll give it to someone special",
        161960L to "A crowded room, friends with tired eyes",
        166710L to "I'm hiding from you, and your soul of ice",
        171210L to "My god, I thought you were someone to rely on",
        174960L to "Me? I guess I was a shoulder to cry on",
        179210L to "A face on a lover with a fire in his heart",
        183460L to "A man under cover but you tore me apart",
        194210L to "Now, I've found a real love you'll never fool me again",
        197460L to "Last Christmas, I gave you my heart",
        201210L to "But the very next day you gave it away",

        206710L to "This year, to save me from tears",
        210210L to "I'll give it to someone special",
        215210L to "Last Christmas, I gave you my heart",
        218940L to "But the very next day you gave it away",
        223930L to "This year, to save me from tears",
        228210L to "I'll give it to someone special",
        233460L to "A face on a lover with a fire in his heart",
        237210L to "A man under cover but you tore him apart",
        244210L to "Maybe next year I'll give it to someone",
        248700L to "I'll give it to someone special"
    )

    var currentLineIndex by remember { mutableStateOf(-1) }
    var currentText by remember { mutableStateOf("") }

    // Время (если нет mediaPlayer — эмулируем его)
    var simulatedTime by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }

    // Запуск имитации времени, если медиаплеер недоступен
    LaunchedEffect(Unit) {
        if (mediaPlayer == null) {
            isPlaying = true
            while (isPlaying) {
                simulatedTime += 50L
                delay(50L)
            }
        }
    }

    // Основной цикл обновления времени и субтитров
    LaunchedEffect(Unit) {
        while (true) {
            val pos = if (mediaPlayer?.isPlaying == true) {
                mediaPlayer.currentPosition.toLong()
            } else if (isPlaying) {
                simulatedTime
            } else {
                0L
            }

            // Показ следующей строки
            while (
                currentLineIndex + 1 < lyricsWithTime.size &&
                pos >= lyricsWithTime[currentLineIndex + 1].first
            ) {
                currentLineIndex += 1
                currentText = ""
                val line = lyricsWithTime[currentLineIndex].second
                if (line.isNotEmpty()) {
                    line.forEachIndexed { index, char ->
                        delay(40L)
                        currentText = line.substring(0, index + 1)
                    }
                } else {
                    delay(300L)
                }
            }

            delay(80L)
        }
    }

    // Вибрация (работает только при наличии mediaPlayer и вибратора)
    LaunchedEffect(Unit) {
        while (true) {
            if (vibrator != null && (mediaPlayer?.isPlaying == true || isPlaying)) {
                val pos = if (mediaPlayer?.isPlaying == true) mediaPlayer?.currentPosition?.toLong() ?: 0L else simulatedTime
                // Вибрируем только в интервале с текстом (~17–249 сек)
                if (pos in 17000L..250000L) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(40L)
                    }
                }
            }
            delay(500L) // 120 BPM
        }
    }

    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            lyricsWithTime.forEachIndexed { index, (_, text) ->
                AnimatedVisibility(visible = index <= currentLineIndex) {
                    Text(
                        text = if (index == currentLineIndex) currentText else text,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
