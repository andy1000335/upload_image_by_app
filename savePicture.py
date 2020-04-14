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