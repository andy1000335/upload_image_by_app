package com.example.uplaodimage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private val url = "http://d869bdac.ngrok.io/post"     //ngrok url
    private var postQueue: RequestQueue? = null
    private var postRequest: StringRequest? = null

    private var imagePath = ""
    private var base64 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postQueue = Volley.newRequestQueue(this)

        postRequest = object: StringRequest(Request.Method.POST, url,
            Response.Listener<String> {response ->



            }, Response.ErrorListener { error ->
                error.printStackTrace()

            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): HashMap<String, String>? {
                val hashMap = HashMap<String, String>()
                hashMap["img"] = base64
                return hashMap
            }

        }
    }

    fun postData(v: View) {
        postQueue!!.add(postRequest)
    }

    fun choosePicture(v: View) {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imgUri = data?.data
            imagePath = imgUri!!.path!!
            val cr = this.contentResolver

            try {
                val bitmap = BitmapFactory.decodeStream(cr.openInputStream(imgUri))
                base64 = Bitmap2Base64(bitmap)
            } catch (e: FileNotFoundException) {
                Log.e("Exception", e.message, e)
            }
        }
    }

    fun Bitmap2Base64 (bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
