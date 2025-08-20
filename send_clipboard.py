import sys
from PyQt5.QtWidgets import QApplication, QWidget, QTextEdit, QPushButton, QVBoxLayout, QLabel
import subprocess

class MyApp(QWidget):
    def __init__(self):
        super().__init__()

        self.setWindowTitle("Send Clipboard android")
        self.setGeometry(100, 100, 400, 300)

        # Text area
        self.text_area = QTextEdit(self)

        # Button
        self.button = QPushButton("Kirim", self)
        self.button.clicked.connect(self.tampilkan_teks)

        # Label untuk hasil
        self.label_hasil = QLabel("", self)

        # Layout
        layout = QVBoxLayout()
        layout.addWidget(self.text_area)
        layout.addWidget(self.button)
        layout.addWidget(self.label_hasil)
        self.setLayout(layout)

    def tampilkan_teks(self):
        teks = self.text_area.toPlainText()
        if teks:
           try:
              #process = subprocess.Popen(["nc", "192.168.0.100", "9090"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True).communicate(teks)
              #out, err = process.communicate(teks)

              result = subprocess.run(["nc", "192.168.0.100", "9090"], input=teks, text=True, capture_output=True, timeout=1)

              self.label_hasil.setText(f"sukses: {result}")
           except Exception as e:
              self.label_hasil.setText(f"error: {e}")

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MyApp()
    window.show()
    sys.exit(app.exec_())
