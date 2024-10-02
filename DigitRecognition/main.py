import cv2 as cv
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from tkinter import *
from PIL import Image, ImageDraw


# Lade Datenset, Teile in Text und Trainingsdaten
mnist = tf.keras.datasets.mnist
(x_train, y_train), (x_test, y_test) = mnist.load_data()

# Normalisiere Daten --> einfacher für Netzwerk zu verarbeiten
x_train = tf.keras.utils.normalize(x_train, axis=1)
x_test = tf.keras.utils.normalize(x_test, axis=1)

# Erstelle Neurales Netzwerk in Sequentieller Anordnung
# (Schichte nacheinander hinzugefügt)
model = tf.keras.models.Sequential()

# Inputlayer
model.add(tf.keras.layers.Flatten(input_shape=(28, 28)))

# zwei verdeckte Schichten mit 128 Neuronen und ReLU Funktion
model.add(tf.keras.layers.Dense(units=512, activation='relu'))
model.add(tf.keras.layers.Dense(units=128, activation='relu'))

# Outputlayer, mit Softmax Funktion in Wahrscheinlichkeiten umgewandelt
model.add(tf.keras.layers.Dense(units=10, activation='softmax'))

# Kompilliervorgang mit Überwachung der Genauigkeit
model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])


# Modell wird trainiert und getestet
model.fit(x_train, y_train, epochs=3)
loss, accuracy = model.evaluate(x_test, y_test)
print(accuracy)
print(loss)

model.save('ZahlenErkennung.keras')

class DrawingApp:
    def __init__(self, model):
        self.model = model
        self.root = Tk()
        self.root.title("Draw a Digit")
        
        self.canvas = Canvas(self.root, width=280, height=280, bg="white")
        self.canvas.pack()
        
        self.image = Image.new("L", (280, 280), color="white")
        self.draw = ImageDraw.Draw(self.image)
        
        self.canvas.bind("<B1-Motion>", self.paint)
        self.canvas.bind("<ButtonRelease-1>", self.reset_xy)
        self.x = None
        self.y = None
        
        self.recognize_button = Button(self.root, text="Recognize", command=self.recognize_digit)
        self.recognize_button.pack()
        
        self.clear_button = Button(self.root, text="Clear", command=self.clear_canvas)
        self.clear_button.pack()
        
        self.result_label = Label(self.root, text="Draw a digit and click Recognize")
        self.result_label.pack()
        
    def paint(self, event):
        if self.x and self.y:
            self.canvas.create_line(self.x, self.y, event.x, event.y, 
                                    width=15, fill="black", 
                                    capstyle=ROUND, smooth=TRUE, splinesteps=36)
            self.draw.line([self.x, self.y, event.x, event.y], 
                           fill="black", width=15)
        self.x = event.x
        self.y = event.y

    def reset_xy(self, event):
        self.x = None
        self.y = None
        
    def recognize_digit(self):
        # Resize image to 28x28
        img = self.image.resize((28, 28))
        img = np.array(img)
        
        # Invert colors and normalize
        img = 255 - img
        img = img / 255.0
        img = img.reshape(1, 28, 28)
        
        # Predict
        prediction = self.model.predict(img)
        digit = np.argmax(prediction)
        confidence = np.max(prediction) * 100
        
        self.result_label.config(text=f"Recognized digit: {digit} (Confidence: {confidence:.2f}%)")
        
    def clear_canvas(self):
        self.canvas.delete("all")
        self.image = Image.new("L", (280, 280), color="white")
        self.draw = ImageDraw.Draw(self.image)
        self.result_label.config(text="Draw a digit and click Recognize")
        
    def run(self):
        self.root.mainloop()

# After training, create and run the app
print("Model trained. Opening drawing application...")
app = DrawingApp(model)
app.run()
