package com.setsunajin.asisten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

import com.setsunajin.asisten.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainApkExtrak extends Activity {
    private String packageName = "";
    private String paket = "";
    private boolean isTouchPause = false;
    ArrayList<String> Listname = new ArrayList<>();
    ArrayList<String> Listpaket = new ArrayList<>();
    ArrayList<String> ListpackageList = new ArrayList<>();
    ArrayList<Drawable> Listlogo = new ArrayList<>();
    public static boolean setTouch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (setTouch) {
            touch();
        }
        else {
            setTouch = true;
            apkManagerUser();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTouchPause)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "System Apps").setIcon(R.drawable.icon_css);
        menu.add(Menu.FIRST, 2, 1, "Exit").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            apkManager();
        }
        if (item.getItemId() == 2) {
            MainApkExtrak.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void touch() {
        setContentView(R.layout.activity_touch);
        isTouchPause = true;
        Button btn_home = (Button)findViewById(R.id.activity_touch_home);
        btn_home.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });

        Button btn_recent = (Button)findViewById(R.id.activity_touch_recent);
        btn_recent.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                doRecentAction();
            }
        });

        Button btn_apk = (Button)findViewById(R.id.activity_touch_app);
        btn_apk.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                apkManagerUser();
            }
        });
    }

    private AdapterView.OnItemLongClickListener getLongPressListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int arg2, long arg3)
            {
                Context context = view.getContext(); // Get a context for further usages.
                packageName = ((TextView) view.findViewById(R.id.list_apk_user_textViewPackageItem)).getText().toString();

                String[] aksi = {"Extract...", "Systems app", "Uninstall"};

                AlertDialog.Builder builderIndex1 = new AlertDialog.Builder(MainApkExtrak.this);
                builderIndex1.setTitle(packageName);
                builderIndex1.setItems(aksi, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (item == 0) {
                            if ( ! MainApkExtrak.isSDCardPresent() ) { // Check for SD Card
                                new AlertDialog.Builder(MainApkExtrak.this) // Build a dialog
                                        .setTitle( "SD Card is not available" ) // Here's the title
                                        .setMessage( "SD Card isn't available. We can't continue." ) // And the content
                                        .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick( DialogInterface dialog, int which ) {
                                                // Do nothing...
                                            }
                                        }).show(); // Show it
                                return; // Exit the function.
                            }

                            ExtractOperation operation = new ExtractOperation(MainApkExtrak.this); // Initialize the operation
                            operation.execute( packageName ); // Execute it!
                        }
                        else if (item == 1) {
                            apkManager();
                        }
                        else if (item == 2) {
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+packageName));
                                startActivity(intent);

                            } catch(Exception e) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivity(intent);
                            }
                        }
                    }
                });
                builderIndex1.create().show();
                return true;
            }

        };
    }

    public void apkManagerUser() {
        isTouchPause = false;
        setContentView(R.layout.activity_apk_user);
        List apps = (List) getInstalledApplicationsUser();
        ArrayAdapterItem appsAdapter = new ArrayAdapterItem(this, R.layout.list_apkuser_row, apps);
        ListView appsView = (ListView) findViewById(R.id.activity_apk_user_listView);

        appsView.setAdapter(appsAdapter);
        appsView.setOnItemLongClickListener(getLongPressListener());
        appsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext(); // Get a context for further usages.
                String packageName = ((TextView) view.findViewById(R.id.list_apk_user_textViewPackageItem)).getText().toString();
            }
        });
    }
    public void apkManager() {
        isTouchPause = false;
        setContentView(R.layout.activity_apk);
        ListView lsview = (ListView) findViewById(R.id.activity_apk_listview);

        PackageManager packageManager = getPackageManager();

        for(ApplicationInfo applicationInfo:packageManager.getInstalledApplications(0))
        {
            Listname.add(applicationInfo.loadLabel(packageManager).toString());
            ListpackageList.add(applicationInfo.sourceDir);
            Listpaket.add(applicationInfo.packageName);
            Listlogo.add(applicationInfo.loadIcon(packageManager));
        }

        customAdapter d = new customAdapter(MainApkExtrak.this, Listname, ListpackageList, Listlogo, Listpaket);
        lsview.setAdapter(d);
        lsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext();
                String[] aksi = {"Extract...", "Users app", "Open", "Uninstall"};
                paket = ((TextView)view.findViewById(R.id.list_apk_txtPaket)).getText().toString();

                AlertDialog.Builder builderIndex1 = new AlertDialog.Builder(MainApkExtrak.this);
                builderIndex1.setTitle(paket);
                builderIndex1.setItems(aksi, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (item == 0) {
                            if ( ! MainApkExtrak.isSDCardPresent() ) { // Check for SD Card
                                new AlertDialog.Builder(MainApkExtrak.this) // Build a dialog
                                        .setTitle( "SD Card is not available" ) // Here's the title
                                        .setMessage( "SD Card isn't available. We can't continue." ) // And the content
                                        .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick( DialogInterface dialog, int which ) {
                                                // Do nothing...
                                            }
                                        }).show(); // Show it
                                return; // Exit the function.
                            }

                            ExtractOperation operation = new ExtractOperation(MainApkExtrak.this); // Initialize the operation
                            operation.execute( paket ); // Execute it!
                        }
                        else if (item == 1) {
                            apkManagerUser();
                        }
                        else if (item == 2) {
                            MainApkExtrak.this.finish();
                            //Toast.makeText(MainApkExtrak.this, paket+new MainPaket().apkMana(paket, "open", MainTouchAsisten.this), Toast.LENGTH_LONG).show();
                        }
                        else if (item == 3) {
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+paket));
                                startActivity(intent);

                            } catch(Exception e) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivity(intent);
                            }
                        }
                    }
                });
                builderIndex1.create().show();
            }
        });
    }

    private class ExtractOperation extends AsyncTask<String, Integer, Boolean> {
        File mApp;
        Context mContext;
        ProgressDialog mDialog;

        public ExtractOperation( Context context ) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            // Display a progress dialog before start the task.
            ProgressDialog dialog = new ProgressDialog( this.mContext );
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage( "Extracting APK in /YourApps/ at SD Card..." );

            if ( ! dialog.isShowing() ) {
                dialog.show();
                this.mDialog = dialog; // Set a global variable to handle this later.
            }
        }

        @Override
        protected Boolean doInBackground( String... params ) {
            String packageName = params[0];
            ExtractResults res = MainApkExtrak.ExtractPackage( this.mContext, packageName );

            if ( res.result ) {
                this.mApp = res.file; // This will be used for sharing intent.
                return true;
            } else {
                return false;
            }
        }

        protected void onPostExecute( Boolean result ) {
            this.mDialog.dismiss(); // Completed, so hide the progress dialog.
            if ( result ) {
                Toast.makeText(this.mContext, "Extracted to /YourApps/ directory on SD Card", Toast.LENGTH_LONG).show(); // Make a toast
                Intent share = new Intent( Intent.ACTION_SEND ); // Make a share intent
                share.setType( "application/vnd.android.package-archive" ); // Set the type for APK

                share.putExtra( Intent.EXTRA_STREAM, Uri.fromFile( this.mApp ) ); // Send the file to sharing intent.
                this.mContext.startActivity(Intent.createChooser(share, "Share the application" ) ); // Start the sharing intent.
            } else {
                Toast.makeText(this.mContext, "A problem occurred.", Toast.LENGTH_SHORT).show(); // Show a toast that says it's failed.
            }
        }
    }
    public static ExtractResults ExtractPackage( Context context, String packageName ) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);
        //mainIntent.setFlags(ApplicationInfo.FLAG_ALLOW_BACKUP);
        final List pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        for (Object object : pkgAppsList) {
            ResolveInfo info = (ResolveInfo) object;
            if ( info.activityInfo.applicationInfo.packageName == null ) {
                new AlertDialog.Builder( context)
                        .setTitle( "Wrong package" )
                        .setMessage( "Package isn't available for extracting." )
                        .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick( DialogInterface dialog, int which ) {

                            }
                        })
                        .show();
            }
            File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
            File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YourApps/" + info.activityInfo.applicationInfo.packageName + ".apk");
            File parent = dest.getParentFile();
            if ( parent != null ) parent.mkdirs();

            try {
                copyFile(file, dest);
            } catch (IOException e) {
                new AlertDialog.Builder( context)
                        .setTitle( "Exception detected" )
                        .setMessage( "Exception detected: " + e.getMessage() )
                        .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick( DialogInterface dialog, int which ) {

                            }
                        })
                        .show();
            }

            ExtractResults res = new ExtractResults( true );
            res.setFile( dest );
            return res;
        }

        return new ExtractResults( false );
    }

    public List<PackageItem> getInstalledApplicationsUser() {
        final Intent mainIntent = new Intent( Intent.ACTION_MAIN, null );
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List AppsList = getPackageManager().queryIntentActivities( mainIntent, 0 );
        Collections.sort( AppsList, new ResolveInfo.DisplayNameComparator( getPackageManager() ) );

        List<PackageItem> data = new ArrayList<PackageItem>();
        for( Object object : AppsList ) {
            try {
                ResolveInfo info = (ResolveInfo) object;
                if (info.activityInfo.applicationInfo.icon != 0 && ( ( info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) ) {
                    PackageItem item = new PackageItem();
                    item.setName(getPackageManager().getApplicationLabel(info.activityInfo.applicationInfo).toString());
                    item.setPackageName(info.activityInfo.applicationInfo.packageName);
                    item.setIcon(info.activityInfo.applicationInfo.loadIcon(getPackageManager()));

                    File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
                    item.setApkSize( bytesToMB( file.length() ) );
                    data.add(item);
                }
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }

        return data;
    }
    public static boolean isSDCardPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    public static boolean ExtractAll(Context context) {
        new BackupApps(context);
        return true;
    }
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
        in.close();
        out.close();
    }
    public String bytesToMB( long bytes ) {
        String res = "";
        Integer num = 0;
        if ( bytes < 1000000 ) {
            // In kilobytes
            num = ( ( int ) Math.ceil( bytes / 1000 ) );
            res = num.toString() + " KB";
        } else {
            // In megabytes
            num = ( ( int ) Math.ceil( bytes / 1000000 ) );
            res = num.toString() + " MB";
        }

        return res;
    }
    public void doRecentAction() {
        try {
            Class ServiceManager = Class.forName("android.os.ServiceManager");
            Method getService = ServiceManager.getMethod("getService", new Class[]{String.class});
            Object[] statusbarObj = new Object[]{"statusbar"};

            IBinder binder = (IBinder) getService.invoke(ServiceManager, statusbarObj);
            Class IStatusBarService = Class.forName("com.android.internal.statusbar.IStatusBarService").getClasses()[0];

            Method asInterface = IStatusBarService.getMethod("asInterface", new Class[]{IBinder.class});
            Object obj = asInterface.invoke(null, new Object[]{binder});
            IStatusBarService.getMethod("toggleRecentApps", new Class[0]).invoke(obj, new Object[0]);
        } catch (Exception e) {
            Toast.makeText(MainApkExtrak.this, "ERR: "+e, Toast.LENGTH_LONG).show();
        }
    }
}

class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {

    }


}

class ArrayAdapterItem extends ArrayAdapter<PackageItem> {
    Context mContext;
    int layoutResourceId;
    List data = null;

    public ArrayAdapterItem(Context mContext, int layoutResourceId, List data) {
        super( mContext, layoutResourceId, data );

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        if ( convertView == null ) {
            LayoutInflater inflater = ( (Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate( layoutResourceId, parent, false );
        }

        PackageItem packageItem = (PackageItem) data.get( position );

        TextView textViewNameItem = (TextView) convertView.findViewById(R.id.list_apk_user_textViewNameItem);
        TextView textViewPackageItem = (TextView) convertView.findViewById(R.id.list_apk_user_textViewPackageItem);
        TextView textViewSize = (TextView) convertView.findViewById( R.id.list_apk_user_textViewSize );
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.list_apk_user_appIcon);

        textViewNameItem.setText( packageItem.getName() );
        textViewPackageItem.setText( packageItem.getPackageName() );
        textViewSize.setText( packageItem.getApkSize() );
        appIcon.setImageDrawable( packageItem.getIcon() );

        return convertView;
    }
}

class BackupApps {
    public BackupApps( Context mContext ) {
        if ( ! MainApkExtrak.isSDCardPresent() ) { // Check for SD Card
            new AlertDialog.Builder( mContext) // Build a dialog
                    .setTitle( "SD Card is not available" ) // Here's the title
                    .setMessage( "SD Card isn't available. We can't continue." ) // And the content
                    .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            // Do nothing...
                        }
                    })
                    .show(); // Show it
            return; // Exit the function.
        }
        BackupOperation bOperation = new BackupOperation( mContext );
        bOperation.execute();

    }

    private class BackupOperation extends AsyncTask<Void, String, Boolean> {
        Context mContext;
        ProgressDialog mDialog;
        Integer iApps = 0;
        Integer iConverted = 0;
        List mApps;

        public BackupOperation( Context mAContext ) { this.mContext = mAContext; }

        protected void onPreExecute() {
            try {
                ProgressDialog pDialog = new ProgressDialog(this.mContext);
                pDialog.setTitle("Extracting all apps...");
                pDialog.setMessage("Initializing... Be patient.");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if ( ! pDialog.isShowing() ) {
                    pDialog.show();
                    this.mDialog = pDialog;
                }

                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                final List AppsList = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
                Collections.sort(AppsList, new ResolveInfo.DisplayNameComparator(this.mContext.getPackageManager()));
                mApps = AppsList;
                for( Object object : mApps ) {
                    try {
                        ResolveInfo info = (ResolveInfo) object;

                        if (info.activityInfo.applicationInfo.icon != 0 && ( info.activityInfo.applicationInfo.packageName != null ) && ( ( info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) ) {
                            this.iApps++;
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground( Void... params ) {
            String packageName;
            for( Object object : mApps ) {
                try {
                    ResolveInfo info = (ResolveInfo) object;
                    if ( info.activityInfo.applicationInfo.packageName == null )
                        continue;
                    if (info.activityInfo.applicationInfo.icon != 0 && ( ( info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) ) {
                        packageName = info.activityInfo.applicationInfo.packageName;
                        publishProgress( packageName );

                        ExtractResults res = MainApkExtrak.ExtractPackage(this.mContext, packageName);
                        if (res.result) {
                            iConverted++;
                        } else {
                            Toast.makeText(mContext, packageName + " extraction failed", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                    publishProgress( "error" );
                }
            }

            return ( iApps == iConverted );
        }

        @Override
        protected void onProgressUpdate( String... values ) {
            if ( values[0].equals( "error" ) ) {
                this.mDialog.dismiss();
                Toast.makeText(this.mContext, "Something bad occurred!", Toast.LENGTH_SHORT).show();
            }
            this.mDialog.setMessage( "Extracting app : " + values[0] );
            super.onProgressUpdate( values );
        }

        @Override
        protected void onPostExecute( Boolean converted ) {
            this.mDialog.dismiss();
            if ( converted ) {
                new AlertDialog.Builder( this.mContext )
                        .setTitle( "Successful" )
                        .setMessage( "" + iConverted.toString() + "/" + iApps.toString() + " applications extracted to /YourApps/ at SD Card." )
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Perfect!
                            }
                        }).show();
            } else {
                new AlertDialog.Builder( this.mContext)
                        .setTitle( "Unsuccessful" )
                        .setMessage( "" + iConverted.toString() + "/" + iApps.toString() + " applications extracted to /YourApps/ at SD Card." )
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Perfect!
                            }
                        }).show();
            }
        }
    }
}

class PackageItem {
    private Drawable icon;
    private String name;
    private String packageName;
    private String apkSize;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setApkSize(String apkSize) { this.apkSize = apkSize; }
    public String getApkSize() { return apkSize; }
}

class ExtractResults {
    public File file;
    public final boolean result;

    public ExtractResults( boolean result ) {
        this.result = result;
    }

    public void setFile( File file ) {
        this.file = file;
    }
}

class customAdapter extends ArrayAdapter {

    ArrayList<String> Listname = new ArrayList<>();
    ArrayList<String> Listpaket = new ArrayList<>();
    ArrayList<String> ListpackageList = new ArrayList<>();
    ArrayList<Drawable> Listlogo = new ArrayList<>();
    Activity activity;
    public customAdapter(Activity activity, ArrayList<String> Listname,
                         ArrayList<String> ListpackageList,
                         ArrayList<Drawable> Listlogo, ArrayList<String> Listpaket)
    {
        super(activity,R.layout.list_apk_row, Listname);
        this.activity=activity;
        this.Listlogo=Listlogo;
        this.Listname=Listname;
        this.Listpaket=Listpaket;
        this.ListpackageList=ListpackageList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.list_apk_row, null);

        //find view's here.
        TextView txtAPKNAME,txtPackageName, txtPaket;
        ImageView imageView;

        txtAPKNAME = (TextView) view.findViewById(R.id.list_apk_txtName);
        txtPackageName = (TextView) view.findViewById(R.id.list_apk_txtPackage);
        txtPaket = (TextView) view.findViewById(R.id.list_apk_txtPaket);
        imageView = (ImageView) view.findViewById(R.id.list_apk_imageView);

        txtAPKNAME.setText(Listname.get(position));
        txtPaket.setText(Listpaket.get(position));
        txtPackageName.setText(ListpackageList.get(position)); //path
        imageView.setImageDrawable(Listlogo.get(position));

        return view;
    }
}
