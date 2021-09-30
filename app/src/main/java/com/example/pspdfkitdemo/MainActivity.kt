package com.example.pspdfkitdemo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.document.processor.*
import com.pspdfkit.ui.PdfActivity
import com.pspdfkit.utils.Size
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE = 1
    }

    private var waitingForResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convert_button.setOnClickListener {
            Log.d("Main", "hi")

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            openActivityForResult(intent)

//            val intent = Intent(this, PdfFromImageActivity::class.java)
//            openActivityForResult(intent)
        }

        /**
         * Deprecated
         */
        /*
        if (!waitingForResult) {
            waitingForResult = true
            startActivityForResult(getImagePickerIntent(), REQUEST_IMAGE)
        }
         */

        /**
         * Load PDF file on android
         */
        /*
        val uri = Uri.parse("file:///android_asset/cs380-intro.pdf")
        val config = PdfActivityConfiguration.Builder(this).build()
        PdfActivity.showDocument(this, uri, config)
         */

        /**
         * Img to PDF
         */
        /*
        val image: Bitmap = BitmapFactory.decodeFile("C:/Users/deokhwa_lee/Downloads/greenbay.jpg")
        val outputFile: File =
        val imageSize = Size(image.width.toFloat(), image.height.toFloat())
        val pageImage = PageImage(image, PagePosition.CENTER)
        pageImage.setJpegQuality(70)
        val newPage = NewPage
            .emptyPage(imageSize)
            .withPageItem(pageImage)
            .build()


        val task = PdfProcessorTask.newPage(newPage)
        val disposable = PdfProcessor.processDocumentAsync(task, outputFile)
            .subscribe { progress ->  }
         */
    }

    /**
     * Deprecated
     */
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Once the image is picked and we're done with this activity, close it.
        finish()

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data?.data != null) {
            // Grab the path to the selected image.
            val imageUri = data.data ?: return

            // Create a `PdfProcessorTask` to create the new PDF.
            val task = createPdfFromImageTask(imageUri)

            // Obtain a path where we can save the resulting file.
            // For simplicity we always put it in our application directory.
            val outputPath = filesDir.resolve("image.pdf")

            // Process the document.
            PdfProcessor.processDocument(task, outputPath)

            // And finally show it.
            PdfActivity.showDocument(this, Uri.fromFile(outputPath), PdfActivityConfiguration.Builder(this).build())
        }
    }
    */

    private fun getImagePickerIntent(): Intent? {
        // Creates an intent that will open a file picker with the filter set to only open images.
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        return if (intent.resolveActivity(packageManager) == null) null else Intent.createChooser(intent, "")
    }

    /**
     * This creates a `PdfProcessorTask` that will create a single-page document using the supplied image as the page background.
     */
    private fun createPdfFromImageTask(imageUri: Uri) : PdfProcessorTask {
        // First obtain the size of the image.
        val options = BitmapFactory.Options().apply {
            // By setting this, we won't actually load the image but only figure out the size.
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)
        val imageHeight: Int = options.outHeight
        val imageWidth: Int = options.outWidth

        // We take A4 as a baseline and alter the page's aspect ratio based on the given bitmap.
        val pageSize: Size = if (imageWidth <= imageHeight) {
            Size(NewPage.PAGE_SIZE_A4.width, imageHeight * (NewPage.PAGE_SIZE_A4.width / imageWidth))
        } else {
            Size(NewPage.PAGE_SIZE_A4.height, imageHeight * NewPage.PAGE_SIZE_A4.height / imageWidth)
        }

        // Now that we know the desired size, we can create a `PdfProcessorTask` that will create a document containing a single page.
        return PdfProcessorTask.newPage(NewPage.emptyPage(pageSize)
            // We initialize our new page using the passed-in image URI and calculated page size.
            .withPageItem(PageImage(this, imageUri, RectF(0f, pageSize.height, pageSize.width, 0f)))
            .build())
    }

    private var imageUri: Uri? = null

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Grab the path to the selected image.
            val imageUri = result.data?.data as Uri

            // Create a `PdfProcessorTask` to create the new PDF.
            val task = createPdfFromImageTask(imageUri)

            // Obtain a path where we can save the resulting file.
            // For simplicity we always put it in our application directory.
            val outputPath = filesDir.resolve("image.pdf")

            // Process the document.
            PdfProcessor.processDocument(task, outputPath)

            // And finally show it.
            PdfActivity.showDocument(this, Uri.fromFile(outputPath), PdfActivityConfiguration.Builder(this).build())
        }
    }

    private fun openActivityForResult(intent : Intent) {
        Log.d("Main", "openActivityForResult")
        startForResult.launch(intent)
    }

}