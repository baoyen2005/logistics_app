package android.print;

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import com.example.baseapp.view.safeLog
import java.io.File

class PdfPrint(private val printAttributes: PrintAttributes) {

    fun print(
        printAdapter: PrintDocumentAdapter,
        path: File,
        fileName: String,
        callback: CallbackPrint
    ) {
        printAdapter.onLayout(
            null,
            printAttributes,
            null,
            object : PrintDocumentAdapter.LayoutResultCallback() {
                override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                    getOutputFile(path, fileName)?.let {
                        printAdapter.onWrite(
                            arrayOf(PageRange.ALL_PAGES),
                            it,
                            CancellationSignal(),
                            object : PrintDocumentAdapter.WriteResultCallback() {
                                override fun onWriteFinished(pages: Array<PageRange>) {
                                    super.onWriteFinished(pages)
                                    if (pages.isNotEmpty()) {
                                        val file = File(path, fileName)
                                        val path = file.absolutePath
                                        callback.success(path)
                                    } else {
                                        callback.onFailure()
                                    }

                                }
                            })
                    }
                }
            },
            null
        )
    }

    private fun getOutputFile(path: File, fileName: String): ParcelFileDescriptor? {
        if (!path.exists()) {
            path.mkdirs()
        }
        val file = File(path, fileName)
        try {
            if (file.exists()) {
                file.delete()
            }
            if (file.createNewFile()) {
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            }
            return null
        } catch (e: Exception) {
            e.safeLog()
        }

        return null
    }


    interface CallbackPrint {
        fun success(path: String)
        fun onFailure()
    }
}