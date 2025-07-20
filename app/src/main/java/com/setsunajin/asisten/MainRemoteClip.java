package com.setsunajin.asisten;
import java.net.*;
import java.io.*;
import android.os.*;
import android.app.*;
import java.util.concurrent.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;


public class MainRemoteClip extends Activity
{
	private TextView pesanView, msgView;
	Button btnC;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_remote_clip);
		pesanView = findViewById(R.id.clip_remote_text);
		msgView = findViewById(R.id.clip_remote_msg);
		
		btnC = findViewById(R.id.clip_remote_close);
		
		ServerThread serverThread = new ServerThread();
        serverThread.start();
		
		btnC.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				
				MainRemoteClip.this.finish();
			}
		});
		
		
	}
	
	private void copyToClipboard(String text) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Copied Text", text);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(this, "Teks disalin ke clipboard", Toast.LENGTH_SHORT).show();
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
	public void tampilkanMsg(final String pesan) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
				public void run() {
					if ((pesanView != null) && (pesan != null)) {
						msgView.setText(pesan);
						copyToClipboard(pesan);
					}
				}
			});
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
                                
								tampilkanMsg(pesan);
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
