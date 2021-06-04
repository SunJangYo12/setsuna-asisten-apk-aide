package com.setsunajin.asisten.task;


import java.util.Vector;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.app.Activity;

import com.setsunajin.asisten.MainActivity;
import com.setsunajin.asisten.R;

/**
 * 
 * Main controller activity for NetMeter application.
 * 
 * Creates the display (table plus graph view) and connects to
 * the NetMeterService, starting it if necessary. Since the service
 * will directly update the display when it generates new data, references
 * of the display elements are passed to the service after binding.
 */
public class MainTaskManager extends Activity {
	final private String TAG="NetMeter";

	private ServiceStatus mService;
	private Vector<TextView> mStatsFields;
	private Vector<TextView> mInfoFields;
	private Vector<TextView> mCpuFields;
	private TextView mPowerBidang;

	private GraphView mGraph;

	/**
	 * Service connection callback object used to establish communication with 
	 * the service after binding to it.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

        	// Get reference to (local) service from binder
            mService = ((ServiceStatus.NetMeterBinder)service).getService();
            Log.i(TAG, "service connected");
            // link up the display elements to be updated by the service
            mService.setDisplay(mStatsFields, mInfoFields, mCpuFields, mGraph);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.i(TAG, "service disconnected - should never happen");
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.FIRST, 1, 1, "Task APK").setIcon(R.drawable.icon_css);
		menu.add(Menu.FIRST, 2, 1, "Exit").setIcon(R.drawable.icon_css);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			startActivity(new Intent(this, TaskList.class));
		}
		if (item.getItemId() == 2) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

    /** 
     * Framework method called when the activity is first created. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        startService(new Intent(this, ServiceStatus.class));

        setContentView(R.layout.activity_status);
        mStatsFields = new Vector<TextView>();
        mInfoFields = new Vector<TextView>();
        mCpuFields = new Vector<TextView>();
        mGraph = (GraphView)findViewById(R.id.activity_status_graph);
		
        createTable();
    }

    /**
     * Framework method called when activity becomes the foreground activity.
     * 
     * onResume/onPause implement the most narrow window of activity life-cycle
     * during which the activity is in focus and foreground.
     */
    @Override
    public void onResume() {
    	super.onResume();
    	bindService(new Intent(this, 
							   ServiceStatus.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * Framework method called when activity looses foreground position
     */
    @Override
    public void onPause() {
    	super.onPause();
    	unbindService(mConnection);
    }

    /**
     *  Algorithmically generate the table on the top half of the screen,
     *  which is used to display status and cummulative usage of
     *  cellular and wifi network interfaces, as well as the current
     *  CPU usage.
     */
    private void createTable() {
    	TableLayout table = (TableLayout)findViewById(R.id.activity_status_disp);

    	mInfoFields.addElement(createTableRow(table, R.string.task_disp_cell, -1, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.task_disp_in, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.task_disp_out, 0));
    	createTableRow(table, 0, 0, 0);
    	mInfoFields.addElement(createTableRow(table, R.string.task_disp_wifi, -1, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.task_disp_in, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.task_disp_out, 0));
    	createTableRow(table, 0, 0, 0);
    	mCpuFields.addElement(createTableRow(table, R.string.task_disp_cpu, R.string.task_disp_cpu_type, 0));
    }

    private TextView createTableRow(TableLayout table, int c1, int c2, int c3) {
    	int[] cell_text_ids = {c1, c2, c3};
    	TableRow tr = new TableRow(this);
		table.addView(tr);
		for (int i=0; i < 3; ++i) {
			TextView txt = new TextView(this);
			tr.addView(txt);
			if (cell_text_ids[i] == -1) {
				txt.setVisibility(View.INVISIBLE);
			} 
			else if (cell_text_ids[i] == 0) {
				txt.setText("");
				txt.setGravity(Gravity.RIGHT);
				return txt;
			}
			else {
				txt.setText(getString(cell_text_ids[i]));
			}
		}
		return null;
    }
}
