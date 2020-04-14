# 使用 Android app 上傳圖片至 Server

### android app
- 使用 volley 進行網路通訊
  - 在 AndroidManifest.xml 加入網頁權限
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    ```
  - 在 build.gradle(Module:app) 引入 volley
    ```gradle
    implementation 'com.android.volley:volley:1.1.1'
    ```
- 上傳資料
  - Post 方法
    ```kotlin
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
    ```
  - 按鈕監聽
    ```kotlin
    fun postData(v: View) {
        postQueue!!.add(postRequest)
    }
    ```
- 選擇照片
  - 按鈕開啟媒體庫
    ```kotlin
    fun choosePicture(v: View) {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, 100)
    }
    ```
  - 覆寫 onActivityResult
    ```kotlin
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
    ```
  - Base64 編碼
    ```kotlin
    fun Bitmap2Base64 (bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    ```
### Server
接收圖片並解碼 Base64
```python
import flask
from flask import jsonify, request
from PIL import Image
import base64
import numpy as np
import cv2
import os

path = os.path.abspath('.')

app = flask.Flask(__name__)

@app.route('/post', methods=['POST'])
def post():
    print('Upload...')
    img = request.form.get('img')

    print('Processing...')
    img = base64.b64decode(img)

    img = np.fromstring(img, np.uint8)
    img = cv2.imdecode(img, cv2.IMREAD_COLOR)
    cv2.imwrite(path + r'\upload.png', img)

    print('Upload success')
    return 'Success'

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)
```
