package com.setsunajin.asisten;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainTerminal extends Activity {
    private EditText output;
    private EditText input;
    private Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        output= (EditText)findViewById(R.id.terminal_output);
        input= (EditText)findViewById(R.id.terminal_input);
        submit= (Button)findViewById(R.id.terminal_submit);

        input.setText("/system/bin/ps");
        String commandOutput = runShellCommand(input.getText().toString());
        output.setText(commandOutput);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String commandOutput = runShellCommand(input.getText().toString());
                output.setText(commandOutput);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "Exit").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
           finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }

    private String runShellCommand(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            int numRead;
            char[] buffer = new char[5000];
            StringBuffer commandOutput = new StringBuffer();
            while ((numRead = bufferedReader.read(buffer)) > 0) {
                commandOutput.append(buffer, 0, numRead);
            }
            bufferedReader.close();
            process.waitFor();

            return commandOutput.toString();
        } catch (IOException e) {

            throw new RuntimeException(e);

        } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }

    }
}
