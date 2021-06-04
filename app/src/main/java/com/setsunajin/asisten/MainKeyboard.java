package com.setsunajin.asisten;

import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class MainKeyboard extends InputMethodService {
    private InputThread inputThread;
    private String pesan;
    private TextView pesanView;
    private ArrayBlockingQueue<String> pesanRemoteQueue = new ArrayBlockingQueue<>(100);

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Keyboard tcp service start ... ", Toast.LENGTH_LONG).show();

        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.activity_keyboard, null);
        pesanView = (TextView) view.findViewById(R.id.keyb_message);
        pesanView.setText(pesan);

        view.findViewById(R.id.keyb_batal).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideWindow();
                if (inputThread != null) {
                    inputThread.selesai();
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restaring) {
        inputThread = new InputThread(getCurrentInputConnection());
        inputThread.start();
    }

    public void tampilkanPesan(final String pesan) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if ((pesanView != null) && (pesan != null)) {
                    pesanView.setText(pesan);
                }
            }
        });
    }

    private class InputThread extends Thread {
        private InputConnection inputConnection;
        private boolean berjalan;

        public InputThread(InputConnection inputConnection) {
            this.inputConnection = inputConnection;
        }

        @Override
        public void run() {
            berjalan = true;
            while (berjalan) {
                String pesan = pesanRemoteQueue.poll();
                if (pesan != null && inputConnection != null) {
                    getCurrentInputConnection().commitText(pesan, 1);
                    sendDefaultEditorAction(false);
                }
            }
            onFinishInput();
        }
        public void selesai() {
            berjalan = false;
        }
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9090);
                while(true) {
                    tampilkanPesan("Menunggu koneksi diport 9090");
                    try (Socket socket = serverSocket.accept()) {
                        tampilkanPesan("Terhubung dengan "+socket.getInetAddress().getHostAddress());
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                            String pesan;
                            while ((pesan = in.readLine()) != null) {
                                pesanRemoteQueue.offer(pesan);
                            }
                        }
                    }
                    tampilkanPesan("Koneksi tutup");
                }
            } catch (final IOException e) {
                tampilkanPesan("Kesalahan koneksi: "+ e.getMessage());

            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    }catch(IOException e) {
                        tampilkanPesan("Tidak dapat menutup socket: "+e.getMessage());
                    }
                }
            }
        }
    }
}
