package com.setsunajin.asisten;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.app.*;
import java.io.*;
import android.widget.*;
import android.view.*;
import java.util.*;

public class MainReadJson extends Activity
{
	
	private TextView txtCount, txtTitle, txtPath;
	private Button btnTitle, btnContent, btnTag;
	
	private File[] directories;
    private int currentIndex = 0;
	private String mainpath = "/sdcard/aaa/out_manual";
	private String finishpath = "/sdcard/aaa/finish";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_readjson);
		
		txtPath = findViewById(R.id.rjson_pathname);
		txtCount = findViewById(R.id.rjson_total);
		txtTitle = findViewById(R.id.rjson_title);
		btnTitle = findViewById(R.id.rjson_copy_title);
		btnContent = findViewById(R.id.rjson_content);
		btnTag = findViewById(R.id.rjson_tag);
		
		
		final int totaldir = countDirectories(new File(mainpath));
		
		txtCount.setText("["+currentIndex+" / "+totaldir+"]");
		txtCount.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if (btnTitle.isEnabled())
					{
						Toast.makeText(MainReadJson.this, "title belum dicopy", Toast.LENGTH_LONG).show();
					}
					else if (btnContent.isEnabled())
					{
						Toast.makeText(MainReadJson.this, "content belum dicopy", Toast.LENGTH_LONG).show();
					}
					else if (btnTag.isEnabled())
					{
						Toast.makeText(MainReadJson.this, "tag belum dicopy", Toast.LENGTH_LONG).show();
					}
					else {
						
						executer("mv "+mainpath+"/"+directories[currentIndex].getName()+" "+finishpath);
							
					
						showNextDirectory(totaldir);
						btnTitle.setEnabled(true);
						btnContent.setEnabled(true);
						btnTag.setEnabled(true);
					}
					
				}
			});
			
		File rootDir = new File(mainpath);
        if (rootDir.exists() && rootDir.isDirectory())
		{
            directories = rootDir.listFiles(); // Mendapatkan daftar direktori
			Arrays.sort(directories, Collections.reverseOrder());
			
            
			String dirname = directories[currentIndex].getName();
			String fullname = mainpath+"/"+ dirname;
			
			txtTitle.setText(readFile(fullname+"/title"));
			txtPath.setText(dirname);
			
            btnTitle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						btnTitle.setEnabled(false);
						
						String dirname = directories[currentIndex].getName();
						String fullname = mainpath+"/"+ dirname;
						
						// Baca file dan salin ke clipboard
						String fileContent = readFile(fullname+"/title");
						
						
						copyToClipboard(fileContent);
					}
				});
			btnContent.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						btnContent.setEnabled(false);
						
						String dirname = directories[currentIndex].getName();
						String fullname = mainpath+"/"+ dirname;
						

						String fileContent = readFile(fullname+"/x.html");
						copyToClipboard(fileContent);
					}
				});
			btnTag.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						btnTag.setEnabled(false);
						
						String dirname = directories[currentIndex].getName();
						String fullname = mainpath+"/"+ dirname;
						
						
						String fileContent = readFile(fullname+"/tag.txt");
						
						String[] xx = fileContent.split("\n");
						StringBuilder mout = new StringBuilder();
						
						for (int i=0; i<xx.length;i++)
						{
							if (i<3) {
								mout.append(xx[i]+"\n");
							}
						}
						
						String out = mout.toString().replace("\n", ",");
						
						copyToClipboard(out);
						
						
					}
				});
        } else {
            btnTitle.setText("Path tidak valid atau bukan direktori.");
            btnTitle.setEnabled(false); // Nonaktifkan tombol jika direktori tidak valid
        }
	}
	
	private void showNextDirectory(int total) {
        if (directories != null && currentIndex < directories.length)
		{
			currentIndex++;
			String dirname = directories[currentIndex].getName();
			
			txtCount.setText("["+currentIndex+" / "+total+"]");
			txtTitle.setText(readFile(mainpath+"/"+ dirname+"/title"));
			txtPath.setText(dirname);
            
        }
		else {
            btnTitle.setText("Tidak ada direktori lagi.");
            btnTitle.setEnabled(false); // Nonaktifkan tombol jika sudah tidak ada lagi
        }
    }
	
	
	public void executer(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line+"\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        
    }
	
	
	private int countDirectories(File dir) {
        int count = 0;
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count++; // Hitung folder
                    count += countDirectories(file); // Rekursi untuk menghitung sub-folder
                }
            }
        }
        return count;
    }
	
	private String readFile(String path) {
        StringBuilder content = new StringBuilder();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }

	private void copyToClipboard(String text) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Copied Text", text);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(this, "Teks disalin ke clipboard", Toast.LENGTH_SHORT).show();
	}
	
}
