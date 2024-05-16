package space.kodirex.eris

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arthenica.ffmpegkit.FFmpegSession
import space.kodirex.eris.databinding.ActivityProgressBinding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import space.kodirex.eris.converter.FfmpegHelper

class ProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProgressBinding
    private lateinit var ffmpegHelper: FfmpegHelper
    private var lazyList: LazyListState? = null
    private val logs: MutableList<Pair<String, Int>> = mutableListOf()
    private var id: Int = 0
    private var session: FFmpegSession? = null
    private var complete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ffmpegHelper = FfmpegHelper(this@ProgressActivity)
        val command = intent.getStringExtra("ffmpegCommand")

        if (command != null) {
            ffmpegHelper.convert(command,
                {
                    complete = true
                    runOnUiThread {
                        binding.continueButton.setEnabled(true)

                        binding.progressBar.setProgressCompat(1000, true)
                    }
                },
                {
                    logs.add("${it.level}: ${it.message}" to id++)
                },
                {})
        } else {
            cancelSession()
        }

        binding = ActivityProgressBinding.inflate(layoutInflater)
            .apply {
                composeView.setContent {
                    MaterialTheme {
                        lazyList = rememberLazyListState()
                        LazyColumn(Modifier.fillMaxWidth(), lazyList!!) {
                            items(logs, key = { it.second })
                            {
                                ListItem(headlineContent = {
                                    Text(
                                        it.first
                                    )
                                })
                            }
                        }
                    }
                }
            }


        setContentView(binding.root)

        binding.cancelButton.setOnClickListener { cancelSession() }
        binding.continueButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cancelSession() {
        session?.cancel()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}