import cv2 as cv
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf

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
input("Model trainiert. Mit Entertaste werden Bilder geladen...")

# Lies Bilder ein
for x in range(1,4):
    img = cv.imread(f'test{x}.png')[:,:,0]
    img = np.invert(np.array([img]))
    # Gibt alle Outputneuronen (Prozente) aus, nehme höchsten Index davon
    prediction = model.predict(img)
    print(f'Erkannte Zahl: {np.argmax(prediction)}')

    plt.imshow(img[0], cmap=plt.cm.binary)
    plt.show()
    input("Mit Enter nächstes Bild anzeigen...")