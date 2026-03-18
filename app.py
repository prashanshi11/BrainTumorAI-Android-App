import os
import numpy as np
import cv2
from flask import Flask, request, jsonify, render_template
import tensorflow as tf

app = Flask(__name__)

# Load model
MODEL_PATH = "model/brain_tumor_vgg16.h5"
print("Loading model from:", os.path.abspath(MODEL_PATH))
model = tf.keras.models.load_model(MODEL_PATH)

classes = ['glioma_tumor', 'meningioma_tumor', 'no_tumor', 'pituitary_tumor']
IMG_SIZE = 224


@app.route('/')
def home():
    return "Brain Tumor Detection API is running!"


@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({'error': 'Empty filename'}), 400

    # read image
    img = cv2.imdecode(
        np.frombuffer(file.read(), np.uint8),
        cv2.IMREAD_COLOR
    )

    img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
    img = img / 255.0
    img = np.reshape(img, (1, IMG_SIZE, IMG_SIZE, 3))

    prediction = model.predict(img)
    class_index = np.argmax(prediction)

    return jsonify({
        'prediction': classes[class_index],
        'confidence': float(np.max(prediction))
    })


if __name__ == "__main__":
    app.run(debug=True)