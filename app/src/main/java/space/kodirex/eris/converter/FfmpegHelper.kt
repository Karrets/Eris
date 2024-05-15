package space.kodirex.eris.converter

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FfmpegHelper(private val context: Context) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun convert(input: Uri, output: Uri, options: ConversionOptions) {
        options.explodeMIME()

        FFmpegKit.executeAsync(
            "-i ${uriTofilePath(input)} -crf ${options.crf} -preset slow -vf scale=${options.width}:${options.height} -c:v ${options.codecFFMPEG} -f ${options.container} ${uriTofilePath(output, "w")}",
            { Log.i("ffmpegKIT-COMPL", "Finished...")},
            { Log.i("ffmpegKIT-${it.level.name}", it.message)},
            { Log.i("ffmpegKIT-STATS", it.toString())},
            executorService
        )
    }

    fun getSourceResolution(uri: Uri): Pair<String, String> {
        val result = FFprobeKit.execute("-v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 ${uriTofilePath(uri)}")
        val width = result.output.split("x")[0].trim()
        val height = result.output.split("x")[1].trim()

        return width to height
    }

    private fun uriTofilePath(uri: Uri, mode: String = "r"): String? {
        return FFmpegKitConfig.getSafParameter(context, uri, mode)
    }

}

class ConversionOptions(
    val width: Int,
    val height: Int,
    val crf: Int,
    val mediaType: String
) {
    lateinit var container: String
    lateinit var codec: String
    lateinit var codecFFMPEG: String

    fun explodeMIME(): ConversionOptions {
        container = mediaType.split('-')[0]
        codec = mediaType.split('-')[1]

        codecFFMPEG = when(codec) {
            "h265" -> "hevc"
            else -> codec //If not in here, source codec name is ok!
        }

        return this
    }
}
