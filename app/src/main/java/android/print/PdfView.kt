package android.print

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebView
import androidx.core.content.FileProvider
import com.example.baseapp.view.safeLog
import com.example.bettinalogistics.R
import org.koin.androidx.scope.BuildConfig
import java.io.File

object PdfView {

    private val REQUEST_CODE = 101

    fun createWebPdfJob(
        activity: Activity,
        webView: WebView,
        directory: File,
        fileName: String,
        callback: Callback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
                callback.failure()
                return
            }
        }

        val jobName = activity.getString(R.string.app_name) + " Document"
        var attributes: PrintAttributes? = null
        attributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
        val pdfPrint = PdfPrint(attributes)
        pdfPrint.print(
            webView.createPrintDocumentAdapter(jobName),
            directory,
            fileName,
            object : PdfPrint.CallbackPrint {
                override fun success(path: String) {
                    callback.success(path)
                }

                override fun onFailure() {
                    callback.failure()
                }
            })
    }

    fun sharePdfFile(activity: Activity, path: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
                return
            }
        }

        fileChooser(activity, path)
    }

    interface Callback {
        fun success(path: String)
        fun failure()
    }

    private fun fileChooser(activity: Activity, path: String) {
        val file = File(path)
        val intent = Intent()
        val uri =
            FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.action = Intent.ACTION_SEND
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val chooser = Intent.createChooser(intent, "Share")

        try {
            activity.startActivity(chooser)
        } catch (e: Exception) {
            e.safeLog()
        }
    }

}