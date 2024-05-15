package space.kodirex.eris

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import space.kodirex.eris.converter.ConversionOptions
import space.kodirex.eris.converter.FfmpegHelper
import space.kodirex.eris.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fileSource: Uri
    private lateinit var fileDest: Uri

    private lateinit var ffHelper: FfmpegHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ffHelper = FfmpegHelper(applicationContext)

        binding.exportButton.setOnClickListener {
            fileOutputLauncher.launch(
                Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .setType(translateToMime(binding.mediaFormatSelectContent.text.toString()))
            )
        }

        binding.crfSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.crfValue.text = getString(R.string.crf_value, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.crfValue.text = getString(R.string.crf_value, binding.crfSeekBar.progress)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun translateToMime(type: String): String {
        /*
         * <item>MP4-h254</item>
         * <item>MP4-h265</item>
         * <item>WEBM-av1</item>
         * <item>WEBM-vp8</item>
         * <item>WEBM-vp9</item>
         */

        return when (type) {
            "MP4-h264", "MP4-h265"
            -> "video/mp4"

            "WEBM-av1", "WEBM-vp8", "WEBM-vp9"
            -> "video/webm"

            else
            -> "video/mp4" //This should never happen ideally.
        }
    }

    private val fileSelectLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                fileSource = result.data?.data!!

                val c = contentResolver.query(fileSource, null, null, null, null)
                var fileNameOrPlaceholder = "File Selected!"
                if (c != null) {
                    c.moveToFirst()
                    val indexOfFileName = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (indexOfFileName >= 0) {
                        fileNameOrPlaceholder = c.getString(indexOfFileName)
                    }
                }

                binding.selectedFileName.text = fileNameOrPlaceholder

                val (width, height) = ffHelper.getSourceResolution(fileSource)
                binding.widthEntryContent.setText(width)
                binding.heightEntryContent.setText(height)
            }
        }

    private val fileOutputLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                fileDest = result.data?.data!!

                ffHelper.convert(fileSource, fileDest, ConversionOptions(
                    binding.widthEntryContent.text.toString().toInt(),
                    binding.heightEntryContent.text.toString().toInt(),
                    binding.crfValue.text.toString().toInt(),
                    binding.mediaFormatSelectContent.text.toString()
                ))
            }
        }

    fun fileSelect(view: View) {
        val getFile = Intent(Intent.ACTION_GET_CONTENT)
            .setType("video/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*", "audio/*"))

        fileSelectLauncher.launch(getFile)
    }

    fun navAdvancedSettings(view: View) {
        startActivity(
            Intent(this, AdvancedSettings::class.java)
        )
    }
}
