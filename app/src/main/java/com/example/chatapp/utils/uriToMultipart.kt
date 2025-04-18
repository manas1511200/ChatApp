// utils/UriUtils.kt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    // Compress image to ultra-low quality (5%)
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream) // 5% quality [[2]][[5]]

    return MultipartBody.Part.createFormData(
        "profilePhoto",
        "compressed.jpg",
        outputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
    )
}