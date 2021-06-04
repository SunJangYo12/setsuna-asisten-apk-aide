package com.setsunajin.asisten;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

import com.setsunajin.asisten.memori.MainMemori;
import android.widget.*;
import android.location.*;
import java.util.*;
import java.io.*;

public class MainCatatan extends Activity {
    private EditText edit_alert;
	private EditText edtNewGps, edtNewMessage;
    private LinearLayout layout, layoutGps;
    private ListView list;
	private ScrollView consoleScroll, consoleScrollGps;
    private MainMemori memori;
    private int position = 0;
    private int index;
	private boolean isGps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan);

        layout = (LinearLayout)findViewById(R.id.catatan_layout);
		consoleScroll = (ScrollView)findViewById(R.id.console_scroll_note);
		
		final TextView txtNew = (TextView)findViewById(R.id.catatan_new);
		final EditText edtNew = (EditText)findViewById(R.id.catatan_new_edt);
		final Button btnNew = (Button)findViewById(R.id.catatan_new_btn);
		
		memori = new MainMemori();
		
		txtNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				edtNew.setVisibility(View.VISIBLE);
				btnNew.setVisibility(View.VISIBLE);
				txtNew.setText("new Note");
				
			}
		});
		btnNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				memori.setCatatan("new", edtNew.getText().toString(), "05:00", MainCatatan.this);

				edtNew.setVisibility(View.INVISIBLE);
				btnNew.setVisibility(View.INVISIBLE);

				showNote(MainCatatan.this);
				consoleFocus();
				Toast.makeText(MainCatatan.this, "Saved", Toast.LENGTH_SHORT).show();
				
			}
		});
		btnNew.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v ) {
				viewGps();
				return true;
			}
		});
		showNote(this);
		
		try {
			if (getIntent().getStringExtra("shift").equals("gps")) {
				viewGps();
				isGps = true;
			}
		}catch(Exception e) {}
		
    }
	
	private void viewGps() {
		setContentView(R.layout.activity_catatan_gps);
		
		final LocationManager locationManager = (LocationManager)
		MainCatatan.this.getSystemService(Context.LOCATION_SERVICE);
		
		layoutGps = (LinearLayout)findViewById(R.id.catatan_layout_gps);
		consoleScrollGps = (ScrollView)findViewById(R.id.console_scroll_note_gps);

		edtNewGps = (EditText)findViewById(R.id.catatan_new_edt_gps_koor);
		edtNewMessage = (EditText)findViewById(R.id.catatan_new_edt_gps_msg);
		
		final Button btnNewGps = (Button)findViewById(R.id.catatan_new_btn_gps);
		
		memori = new MainMemori();

		btnNewGps.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					LocationListener locationListener = new MyLocationListener();
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
					
					Toast.makeText(MainCatatan.this, "Getting gps... the current coordinat autosave", Toast.LENGTH_LONG).show();
				}
			});
		
		showNote(MainCatatan.this);
	}
	
	public void showNote(Context context) {
		int lenNote = memori.getStringCatatan(context).size();

        if (lenNote == 0 && !isGps) {
            TextView txtEmp = (TextView)findViewById(R.id.catatan_txt);
            txtEmp.setVisibility(View.VISIBLE);
        } else {
            for (int i=0; i<lenNote; i++) {
                createButton(context, i, memori.getStringCatatan(context).get(i));
            }
        }
	}
	
	private void consoleFocus() {
        consoleScroll.post(new Runnable() {
				@Override
				public void run() {
					consoleScroll.fullScroll(View.FOCUS_DOWN);
				}
			});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "New").setIcon(R.drawable.icon_css);
        menu.add(Menu.FIRST, 2, 1, "Exit").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ini", "note");
            startActivity(intent);
        }
        if (item.getItemId() == 2) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		super.onBackPressed();
		finish();
	}
	
	

    private void createButton(Context context, final int letak, String title) {
        LinearLayout.LayoutParams bparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (letak % 2 == 0) {
            bparams.gravity = Gravity.LEFT;
            bparams.leftMargin = 20;
        } else {
            bparams.gravity = Gravity.RIGHT;
            bparams.rightMargin = 20;
        }
        bparams.topMargin = 50;

        Button b1 = new Button(MainCatatan.this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(letak);
            }
        });
        b1.setText(title);
        b1.setLayoutParams(bparams);
		if (isGps)
			layoutGps.addView(b1);
		else
            layout.addView(b1);
    }

    private void alert(int posisi) {
        final CharSequence[] dialogitem = {"Open...", "Edit", "Hapus"};
        position = posisi;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainCatatan.this);
        builder.setTitle(memori.isCatatan(position, 2, MainCatatan.this));
        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    alertOpen();
                }
                else if (item == 1) {
                    String[] rinci = {"Nama: \n"+memori.isCatatan(position, 0, MainCatatan.this),
                            "Content: \n"+memori.isCatatan(position, 1, MainCatatan.this),
                            "Date: \n"+memori.isCatatan(position, 2, MainCatatan.this) };
                    AlertDialog.Builder builderIndex1 = new AlertDialog.Builder(MainCatatan.this);
                    builderIndex1.setTitle("Rincian: "+position);
                    builderIndex1.setItems(rinci, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item)
                        {
                            String[] rinci = {"Nama: \n"+memori.isCatatan(position, 0, MainCatatan.this),
                                    "Content: \n"+memori.isCatatan(position, 1, MainCatatan.this),
                                    "Date: \n"+memori.isCatatan(position, 2, MainCatatan.this) };
                            if (item == 0) {
                                alertEdit(rinci[0], 0);
                            }
                            else if (item == 1) {
                                alertEdit(rinci[1], 1);
                            }
                            else if (item == 2) {
                                alertEdit(rinci[2], 2);
                            }
                        }
                    });
                    builderIndex1.create().show();
                }
                else if (item == 2) {
                    memori.position = position;
                    memori.setCatatan("rm", "", "", MainCatatan.this);
                    Toast.makeText(MainCatatan.this, "Saved", Toast.LENGTH_SHORT).show();
                    MainCatatan.this.finish();
                }
            }
        });
        builder.create().show();
    }

    private void alertEdit(String inEdit, int xindex) {
        index = xindex;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainCatatan.this);
        builder1.setTitle("Edit: "+inEdit);
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_catatan, null);

        edit_alert = (EditText) layout.findViewById(R.id.alert_catatan_amEdit);
        edit_alert.setText(memori.isCatatan(position, index, MainCatatan.this));

        Button bt = (Button) layout.findViewById(R.id.alert_catatan_amButton);
        bt.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String edit = edit_alert.getText().toString();
                if (index == 0) {
                    memori.position = position;
                    memori.setCatatan("edit", edit, memori.isCatatan(position, 1, MainCatatan.this), MainCatatan.this);
                }
                if (index == 1) {
                    memori.position = position;
                    memori.setCatatan("edit", memori.isCatatan(position, 0, MainCatatan.this), edit, MainCatatan.this);
                }
                if (index == 2) {
                    Toast.makeText(MainCatatan.this, "Auto edit", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(MainCatatan.this, "Saved", Toast.LENGTH_SHORT).show();
                MainCatatan.this.finish();
            }
        });
        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void alertOpen() {
        final CharSequence[] dialogitem = {"Google.com", "bing.com"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainCatatan.this);
        builder.setTitle("Pilihan");
        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String data = memori.titles.get(position);

                if (item == 0) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/m?hl=in&q="+data+"&source=android-browser-type")));

                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?safe=strict&client=firefox-b-ab&ei=BRpWXKm1F9DprQGN6p3oCA&q=zzz&oq=zzz&gs_l=psy-ab.3..0l2j0i131l2j0l6.7384.8817..9380...0.0..0.213.723.0j2j2......0....1..gws-wiz.....0..0i131i67.65XSM2uBm9c")));
                }
                else if (item == 1) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://http-www-bing-com.0.freebasics.com/search?iorg_service_id_internal=803478443041409%3BAfrEX0ng8fF-69Ni&iorgbsid=AZwOf5p9ZGHdo4ma-_4xLROJiPP57wR4JxMMMfZYMk2RHTXt0k_suZhZX4ELlv0Xo8d0A99ibKz2Zk2OYsINpLd4&q="+data+"&go=Search&qs=ds&form=QBRE&pc=FBIO")));
                }
            }
        });
        builder.create().show();
    }
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			edtNewGps.setText("");
			
			Toast.makeText(
                getBaseContext(),
                "Saved: " + loc.getLatitude() + "," + loc.getLongitude(), Toast.LENGTH_SHORT).show();
			String longitude = "garis bujur: " + loc.getLongitude();
			
			String latitude = "garis lintang: " + loc.getLatitude();
			

			/*------- To get city name from coordinates -------- */
			String cityName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(),
												loc.getLongitude(), 1);
				if (addresses.size() > 0) {
					System.out.println(addresses.get(0).getLocality());
					cityName = addresses.get(0).getLocality();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			//String s = longitude + "\n" + latitude + "\n\nMy Current City is: "+ cityName;
			String s = longitude + "\n" + latitude;
			
			edtNewGps.setText(s);
			
			memori.setCatatan("new", edtNewGps.getText().toString()+" > "+edtNewMessage.getText().toString(), "05:00", MainCatatan.this);
			
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
}
