package space.kodirex.eris

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        ffHelper = FfmpegHelper(this@MainActivity)

        binding.exportButton.setOnClickListener { exportPressed() }
        binding.importFileButton.setOnClickListener { fileSelect() }
        binding.advancedSettingsButton.setOnClickListener { navAdvancedSettings() }
        binding.crfInfoButton.setOnClickListener{ crfInfo() }

        binding.mediaFormatSelectContent.setOnItemClickListener { _, _, _, _ ->
            when(binding.mediaFormatSelectContent.text.toString()) {
                "MP4-h264", "MP4-h265" -> {
                    binding.crfSeekBar.setEnabled(true)
                    binding.crfSeekBar.max = 51
                    binding.crfSeekBar.progress = 23
                }
                "WEBM-vp8", "WEBM-vp9" -> {
                    binding.crfSeekBar.setEnabled(true)
                    binding.crfSeekBar.max = 63
                    binding.crfSeekBar.progress = 31
                }
                else -> {
                    binding.crfSeekBar.setEnabled(false)
                    binding.crfSeekBar.max = 0
                    binding.crfSeekBar.progress = 0
                }
            }
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
            -> "video/*" //This should never happen ideally.
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

                val command = ffHelper.compile(
                        fileSource, fileDest, ConversionOptions(
                            binding.widthEntryContent.text.toString().toInt(),
                            binding.heightEntryContent.text.toString().toInt(),
                            binding.crfValue.text.toString().toInt(),
                            binding.mediaFormatSelectContent.text.toString()
                        )
                    )

                startActivity(
                    Intent(this@MainActivity, ProgressActivity::class.java)
                        .putExtra("ffmpegCommand", command)
//                        .putExtra("FFHelper", ffHelper)
                )

            }
        }

    private fun exportPressed() {
        fileOutputLauncher.launch(
            Intent(Intent.ACTION_CREATE_DOCUMENT)
                .setType(translateToMime(binding.mediaFormatSelectContent.text.toString()))
        )
    }

    private fun crfInfo() {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(resources.getString(R.string.crf_info_title))
            .setMessage(resources.getString(R.string.crf_info_body))
            .setPositiveButton(resources.getString(R.string.acknowledge)) { _, _ -> }
            .show()
    }

    private fun fileSelect() {
        val getFile = Intent(Intent.ACTION_GET_CONTENT)
            .setType("video/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*", "audio/*"))

        fileSelectLauncher.launch(getFile)
    }

    private fun navAdvancedSettings() {
        startActivity(
            Intent(this@MainActivity, AdvancedSettings::class.java)
        )
    }
}
