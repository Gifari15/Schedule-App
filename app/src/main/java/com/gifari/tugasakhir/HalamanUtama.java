package com.gifari.tugasakhir;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import at.markushi.ui.CircleButton;

public class HalamanUtama extends Activity implements View.OnClickListener {
    //public class HalamanUtama extends AppCompatActivity
    ActionMode myActMode;
    TextView tanggal, deltask, texttime, notask;
    CircleButton smallbtn, smallbtn2, btnUtama;
    TextClock anutime;
    String varngide;
    EditText tgldari, tglsampai, etkegiatan;
    CardView list;
    Dialog dialog;
    int hari, bulan, tahun;
    //EditText timex, tanggalx, tugasx;
    DatePickerDialog datePickerDialog;
    public static String getdate, getTime, gethour, getminute, gethour2, getminute2, gettglx, gettgsx, gettimex;
    //public static int jam;

    //VARIABEL UNTUK NAMBAH TUGAS
    DBHelper helper;
    SQLiteDatabase db;
    String selected_ID="";
    ListView tasks;
    SimpleCursorAdapter adapter;
    RadioButton rb;
    CheckBox cb;
    private static final String CHANNEL_ID = "notif_app";
    private static final int NOTIFICATION_ID = 999;
    String channelnotif = "channelku" ;
    String channelid = "default" ;
    public ArrayList<String> Listdate;
    public ArrayList<String> ListData;
    public ArrayList<String> ListTime;

    //variabel untuk edit data
    public static String name_edt, tgedt, tmedt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
        //getSupportActionBar().hide();

        //init db objects
        helper = new DBHelper(this);
        Calendar calendar = Calendar.getInstance();
        hari = calendar.get(Calendar.DAY_OF_MONTH);
        bulan = calendar.get(Calendar.MONTH);
        tahun = calendar.get(Calendar.YEAR);

        //String tampiltanggal = hari + "/" + bulan + "/" + tahun;
        tanggal = (TextView)findViewById(R.id.tvtanggal);
        tanggal.setText(new StringBuilder()
        .append(hari).append("/").append(bulan+1).append("/").append(tahun));

        notask = (TextView)findViewById(R.id.tvnotaks);
        list = (CardView) findViewById(R.id.card);

        //Mengambil id ettime dari file view_custom_dialog.xml
        //anutime.getText().toString(); -->    MENCOBA MENGIDE MENGAMBIL DATE DARI TEXTCLOCK
        //time = (EditText)findViewById(R.id.ettime);
        //tugasx = (EditText)findViewById(R.id.editTextTask);
        //tanggalx = (EditText)findViewById(R.id.ettgl);
        smallbtn = (CircleButton) findViewById(R.id.btnadd);
        smallbtn2 = (CircleButton) findViewById(R.id.btsearch);
        btnUtama = (CircleButton) findViewById(R.id.circlebtn);
        varngide = "true";
        //cb = (CheckBox) findViewById(R.id.cbtn);
        //texttime = (TextView)findViewById(R.id.timetext);
        //anutime = (TextClock)findViewById(R.id.tvtime);
        deltask = (TextView)findViewById(R.id.tvdel);
        deltask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call delete method of sqlitedb class to delete record and close after performing task
                String gettvtampil = deltask.getText().toString();
                if (gettvtampil.equals("Today")){
                    tampilToday();
                    smallbtn.setVisibility(View.INVISIBLE);
                    smallbtn2.setVisibility(View.INVISIBLE);
                    varngide = "true";
                    Toast.makeText(HalamanUtama.this, "Kegiatan Hari Ini", Toast.LENGTH_LONG).show();
                }
                else{
                    fetchData();
                    deltask.setText("Today");
                    notask.setText("");
                    smallbtn.setVisibility(View.INVISIBLE);
                    smallbtn2.setVisibility(View.INVISIBLE);
                    varngide = "true";
                    //Toast.makeText(HalamanUtama.this, "Gagal Menampilkan Data", Toast.LENGTH_LONG).show();
                }


            }
        });
        tasks = (ListView)findViewById(R.id.lv);
        tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String name, tanggald, timed;
                Cursor row = (Cursor) adapter.getItemAtPosition(position);
                selected_ID = row.getString(0);
                name = row.getString(1);
                tanggald = row.getString(2);
                timed = row.getString(3);
                //list.setBackgroundColor(192532);

//                txtEname.setText(name);
//                txtDesig.setText(desig);
//                txtSalary.setText(salary);

            }
        });
        fetchData();
        dataTask();
        notification();

        tasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                if (myActMode != null) {
                    return false;
                }
                String name, tanggald, timed;
                Cursor row = (Cursor) adapter.getItemAtPosition(position);
                selected_ID = row.getString(0);
                name = row.getString(1);
                tanggald = row.getString(2);
                timed = row.getString(3);
                name_edt = name;
                tgedt = tanggald;
                tmedt = timed;
                myActMode = startActionMode(myActModeCallback);
                smallbtn.setVisibility(View.INVISIBLE);
                smallbtn2.setVisibility(View.INVISIBLE);
                btnUtama.setEnabled(false);
                varngide = "true";
                //varngide = "false";

                //editDialog();
                return true;
            }
        });
    }

    //AWAL DARI CONTEXTUAL BAR ===================
    private ActionMode.Callback myActModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.example_menu, menu);
            mode.setTitle("Menu");
            //btnUtama.setEnabled(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_1:
                    SearchData();
                    //deltask.setText("Today");
                    mode.finish();
                    return true;
                case R.id.option_2:
                    editDialog();
                    //deltask.setText("Today");
                    mode.finish();
                    return true;
                case R.id.option_3:
                    //INI SYNTAX ASLI
                    /*
                    db = helper.getWritableDatabase();
                    db.delete(DBHelper.TABLE, DBHelper.C_ID+ "=?",new String[]{selected_ID});
                    db.close(); */
                    //================

                    //MECOBA MENGIDE SYNTAX
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HalamanUtama.this);
                    alertDialogBuilder.setTitle("Anda Yakin Ingin Hapus Data ?");
                    //membuat pesan keluar aplikasi
                    alertDialogBuilder
                            .setMessage("\n\t\t\tKlik Ya untuk hapus data")
                            .setIcon(R.mipmap.eschedule)
                            .setCancelable(false)
                            .setPositiveButton("Ya",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //jika tombol ya diklik, maka akan menutup activity
                                            db = helper.getWritableDatabase();
                                            db.delete(DBHelper.TABLE, DBHelper.C_ID+ "=?",new String[]{selected_ID});
                                            db.close();
                                            fetchData();
                                            Toast.makeText(HalamanUtama.this, "Record berhasil dihapus",Toast.LENGTH_LONG).show();
                                            //System.exit(0);
                                        }
                                    })

                            .setNegativeButton("Tidak",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                    //membuat alert dari dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    //menampilkan alert dialog
                    alertDialog.show();
                    // AKHIR DARI NGIDE SYNTAX ALERT DIALOG

                    //fetch data from
                    //fetchData();
                    //Toast.makeText(HalamanUtama.this, "Record berhasil dihapus",Toast.LENGTH_LONG).show();
                    mode.finish();
                    return true;
                default:
                    //btnUtama.setEnabled(true);
                    //return false;
                    return false;
            }
            //btnUtama.setEnabled(true);
//            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            btnUtama.setEnabled(true);
            //list.setCardBackgroundColor(Color.parseColor("#273742"));
            //list.setBackgroundColor(273742);
            myActMode = null;
        }
    };
    //AKHIR DARI CONTEXTUAL BAR

    public void dialog(View view) {
        showCustomDialog();
    }

    private void showCustomDialog() {
        //final Dialog dialog = new Dialog(this);
        dialog = new Dialog(this);
        //Mengeset judul dialog
        //dialog.setTitle("Add Task");

        //Mengeset layout
        dialog.setContentView(R.layout.view_custom_dialog);

        //Membuat agar dialog tidak hilang saat di click di area luar dialog
        dialog.setCanceledOnTouchOutside(false);

        //Membuat dialog agar berukuran responsive
        /*
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        */

        String dateNow = "";
        Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
        Button saveButton = (Button) dialog.findViewById(R.id.button_save);
        ImageButton date = (ImageButton) dialog.findViewById(R.id.date);
        EditText tgs = (EditText) dialog.findViewById(R.id.editTextTask);
        EditText tgl = (EditText) dialog.findViewById(R.id.ettgl);
        EditText time = (EditText) dialog.findViewById(R.id.ettime);

        Calendar calendar = Calendar.getInstance();
        hari = calendar.get(Calendar.DAY_OF_MONTH);
        bulan = calendar.get(Calendar.MONTH);
        tahun = calendar.get(Calendar.YEAR);
        String gethari = String.valueOf(hari);
        bulan = bulan+1;
        String getbulan = String.valueOf(bulan);
        if (gethari.length() < 2){
            gethari = "0"+hari;
        }
        else {
            gethari = hari+"";
        }
        if(getbulan.length() < 2){
            getbulan = "0"+bulan;
        }
        else {
            getbulan = bulan+"";
        }
        tgl.setText(new StringBuilder()
                .append(tahun).append("-").append(getbulan).append("-").append(gethari));

//        dateNow = (new StringBuilder()
//                .append(hari).append("-").append(bulan + 1).append("-").append(tahun).toString());
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HalamanUtama.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //time.setText(selectedHour + ":" + selectedMinute);
                        //getTime = selectedHour + ":" + selectedMinute;
                        gethour = selectedHour+"";
                        getminute = selectedMinute+"";
                        if (gethour.length() < 2){
                            gethour2="0"+selectedHour;
                        }
                        else {
                            gethour2=selectedHour+"";
                        }
                        if (getminute.length() < 2){
                            getminute2="0"+selectedMinute;
                        }
                        else {
                            getminute2=selectedMinute+"";
                        }
                        time.setText(gethour2 + ":" +getminute2);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });
        //BUTTON UNTUK MENAMBAHKAN TANGGAL
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anu();
                //tgl.setText(getdate);
            }
        });
        //AKHIR DARI BUTTON PENAMBAHAN TANGGAL

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HalamanUtama.this, "Data saved", Toast.LENGTH_SHORT).show();
                //dialog.dismiss();
                /*
                gettgsx = tgs.getText().toString();
                gettglx = tgl.getText().toString();
                gettimex = time.getText().toString(); */
                //add record with help of ContentValues and DBHelper class object
                ContentValues values = new ContentValues();
                values.put(DBHelper.C_ENAME, tgs.getText().toString());
                values.put(DBHelper.C_DATE, tgl.getText().toString());
                values.put(DBHelper.C_TIME, time.getText().toString());
                //call insert method of sqlitedb class and close after performing task
                db = helper.getWritableDatabase();
                db.insert(DBHelper.TABLE, null, values);
                db.close();

                //clearFields();
                Toast.makeText(HalamanUtama.this, "Berhasil Menambahkan Tugas", Toast.LENGTH_LONG).show();

                fetchData();
                dataTask();
                notification();
                dialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //tgl.setText(newDate.getTime().toString());
            }
        });

        //Menampilkan custom dialog
        dialog.show();
    }


    void anu(){
        Calendar newCalendar = Calendar.getInstance();
        //datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
        EditText tgl = (EditText) dialog.findViewById(R.id.ettgl);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */
                // INI MAH NGIDE SYNTAX CUY !!!!!!
                //mencoba mengambil tanggal sekarang
                String tglsekarang = "";
                Calendar calendar = Calendar.getInstance();
                hari = calendar.get(Calendar.DAY_OF_MONTH);
                bulan = calendar.get(Calendar.MONTH);
                tahun = calendar.get(Calendar.YEAR);
                tglsekarang = (new StringBuilder()
                        .append(hari).append("-").append(bulan + 1).append("-").append(tahun).toString());
                //AKHIR DARI NGIDE SYNTAX
                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //getdate = newDate.getTime().toString();
                String gethari = String.valueOf(dayOfMonth);
                monthOfYear = monthOfYear+1;
                String getbulan = String.valueOf(monthOfYear);
                if (gethari.length() < 2){
                    gethari = "0"+dayOfMonth;
                }
                else {
                    gethari = dayOfMonth+"";
                }
                if(getbulan.length() < 2){
                    getbulan = "0"+monthOfYear;
                }
                else {
                    getbulan = monthOfYear+"";
                }
                getdate = year + "-" + (getbulan) + "-" + gethari;
//                if(getdate.equals(tglsekarang)){
//                    tgl.setText("Today");
//                }

//                else {
                    tgl.setText(getdate);
//                }
                //tgl.setText(newDate.getTime().toString());
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /*
    void addTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.show();
    }
    */


    void addlv(){
        ContentValues nilai = new ContentValues();
        nilai.get(DBHelper.C_ENAME);
        nilai.get(DBHelper.C_DATE);
        nilai.get(DBHelper.C_TIME);
    }

    public void tes(View view) {
        //showCustomDialog();
        if(varngide.equals("true")){
            smallbtn.setVisibility(View.VISIBLE);
            smallbtn2.setVisibility(View.VISIBLE);
            varngide = "false";
        }
        else if(varngide.equals("false")){
            smallbtn.setVisibility(View.INVISIBLE);
            smallbtn2.setVisibility(View.INVISIBLE);
            varngide = "true";
        }
        else{

        }

    }

    @Override
    public void onClick(View v) {
        /*
        if (v == deltask){
            //call delete method of sqlitedb class to delete record and close after performing task
            db = helper.getWritableDatabase();
            db.delete(DBHelper.TABLE, DBHelper.C_ID+ "=?",new String[]{selected_ID});
            db.close();

            //fetch data from
            fetchData();
            Toast.makeText(this, "Record berhasil dihapus",Toast.LENGTH_LONG).show();


        }*/
    }
    private void fetchData() {
        db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE, null, null, null,null, null, null);

        adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
        tasks.setAdapter(adapter);
    }

    //FUNGSI UNTUK MEMANGGIL NOTIFIKASI

    public void notifikasi() {
        String tgsekarang = "";
        String timesekarang = "";
        String datedb = "";
        String timedb = "";
        ContentValues datee = new ContentValues();
        ContentValues timee = new ContentValues();
        //nilai.get(DBHelper.C_ENAME);
        //datee.getAsString(DBHelper.C_DATE);
        //timee.getAsString(DBHelper.C_TIME);
        db = helper.getWritableDatabase();
        //db.(DBHelper.TABLE, null, nilai);

        //MENGAMBIL TANGGAL SEKARANG
        Calendar calendar = Calendar.getInstance();
        hari = calendar.get(Calendar.DAY_OF_MONTH);
        bulan = calendar.get(Calendar.MONTH);
        tahun = calendar.get(Calendar.YEAR);
        tgsekarang = (new StringBuilder()
                .append(hari).append("-").append(bulan + 1).append("-").append(tahun).toString());
        //AKHIR =========

        //MENGAMBIL WAKTU HARI INI / SEKARANG
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        timesekarang = hour + ":" + minute;
        dataTask();
        //Toast.makeText(getApplicationContext(), "Ini adalah pesan Toast! "+datee.get(DBHelper.C_DATE)+"", Toast.LENGTH_SHORT).show();
        //if ((datee.getAsString(DBHelper.C_DATE)) == tgsekarang && (timee.getAsString(DBHelper.C_TIME)) == timesekarang){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HalamanUtama.this, channelid )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle( "E-SCHEDULE" )
                    .setContentText( "Ada tugas yang harus diselesaikan" );
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
            if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                int importance = NotificationManager. IMPORTANCE_HIGH ;
                NotificationChannel notificationChannel = new
                        NotificationChannel( channelnotif , "contoh channel" , importance) ;
                notificationChannel.enableLights( true ) ;
                notificationChannel.setLightColor(Color. RED ) ;
                mBuilder.setChannelId( channelnotif ) ;
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel) ;
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
        //}

    }
    //AKHIR DARI FUNGSI NOTIFIKASI


        public String[] getAppCategoryDetail() {

            final String TABLE_NAME = "task";
            String dari="", sampai="";
            dari = tgldari.getText().toString();
            sampai = tglsampai.getText().toString();
            String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+DBHelper.C_DATE+" BETWEEN'"+dari+"' AND '"+sampai+"'";
            db = helper.getReadableDatabase();
            Cursor cursor      = db.rawQuery(selectQuery, null);
            String[] data      = null;

            if (cursor.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                } while (cursor.moveToNext());
            }
            cursor.close();
            return data;
        }

        private void notification(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = "E-SCHEDULE";
                String description = "Ada tugas yang harus diselesaikan";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel("Notify", name,importance);
                channel.setDescription(description);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

        private void dataTask() {
            String tgsekarang = "";
            String timesekarang = "";
            String datedb = "";
            String timedb = "";
            ContentValues datee = new ContentValues();
            ContentValues timee = new ContentValues();
            //nilai.get(DBHelper.C_ENAME);
            //datee.getAsString(DBHelper.C_DATE);
            //timee.getAsString(DBHelper.C_TIME);
            db = helper.getWritableDatabase();
            //db.(DBHelper.TABLE, null, nilai);

            //MENGAMBIL TANGGAL SEKARANG
            Calendar calendar = Calendar.getInstance();
            hari = calendar.get(Calendar.DAY_OF_MONTH);
            bulan = calendar.get(Calendar.MONTH);
            tahun = calendar.get(Calendar.YEAR);
            tgsekarang = (new StringBuilder()
                    .append(tahun).append("-").append(bulan + 1).append("-").append(hari).toString());
            //AKHIR =========

            //MENGAMBIL WAKTU HARI INI / SEKARANG
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            timesekarang = hour + ":" + minute;
            //Mengambil Repository dengan Mode Membaca
            ListData = new ArrayList<String>();
            ListTime = new ArrayList<String>();
            String getIndex="";
            String getIndexTime="";
            final String TABLE_NAMA = "task";
            SQLiteDatabase db = helper.getReadableDatabase();
            //Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE, null);
            Cursor cursor = db.query(DBHelper.TABLE, null, null, null,null, null, null);
            cursor.moveToFirst();//Memulai Cursor pada Posisi Awal

            //Melooping Sesuai Dengan Jumlan Data (Count) pada cursor
            for (int count = 0; count < cursor.getCount(); count++) {

                cursor.moveToPosition(count);//Berpindah Posisi dari no index 0 hingga no index terakhir

                ListData.add(cursor.getString(2));//Menambil Data Dari Kolom 2 (Date)
                ListTime.add(cursor.getString(3));//Menambil Data Dari Kolom 3 (Time)

                //Lalu Memasukan Semua Datanya kedalam ArrayList
                //getIndex = ListData.get(0);
                //getIndexTime = ListTime.get(0);
                /*
                for (int i=0; i<=ListData.size(); i++){
                    //getIndex = ListData.get(i);
                    //getIndexTime = ListTime.get(i);
                    //if((getIndex == tgsekarang) && (getIndexTime == timesekarang)){
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HalamanUtama.this, channelid )
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle( "E-SCHEDULE" )
                                .setContentText( "Ada tugas yang harus diselesaikan" );
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
                        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                            int importance = NotificationManager. IMPORTANCE_HIGH ;
                            NotificationChannel notificationChannel = new
                                    NotificationChannel( channelnotif , "contoh channel" , importance) ;
                            notificationChannel.enableLights( true ) ;
                            notificationChannel.setLightColor(Color. RED ) ;
                            mBuilder.setChannelId( channelnotif ) ;
                            assert mNotificationManager != null;
                            mNotificationManager.createNotificationChannel(notificationChannel) ;
                        }
                        assert mNotificationManager != null;
                        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
                   // }
                }

                 */
                //Toast.makeText(getApplicationContext(), "Ini adalah pesan Toast! "+ListData.size()+"", Toast.LENGTH_SHORT).show();
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                for (int i = 0; i<ListData.size(); i++){
                    //getIndex = ListData.get(i);
                    String ambil_time="";
                    String tes_time="22:30";
                    String baru="5";
                    TextClock timekx=null;
                    String ambiltg = ListData.get(i);
                    String ambiltm = ListTime.get(i);
                    String ambiltg2 = ambiltg.substring(0,1);
                    //int ambiltg3 = Integer.valueOf(ambiltg2);
                    //timekx = timekx.getTimeZone();
                       TimeZone tz = TimeZone.getTimeZone("Indonesia/Jakarta");
                    //String output = tz.toLocalTime().toString ();
                    //String timekx2 = String.valueOf(tz);

                    //anutime.getTimeZone();
                    //Toast.makeText(getApplicationContext(), "#"+anutime.getTimeZone()+"#", Toast.LENGTH_SHORT).show();
                    //ambiltm = ambiltm+"00";

                    //cal_now.setTime(date);

                    //texttime.setText(currentDateTimeString); // --> Mengambil tanggal dan waktu sekarang
                    //ambiltg.equals()
                    if(ambiltg.equals(tgsekarang)){
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Date date = new Date();
                        Calendar cal_alarm = Calendar.getInstance();
                        Calendar cal_now = Calendar.getInstance();

                        cal_now.setTime(date);
                        cal_alarm.setTime(date);

                        int jam = Integer.valueOf(ambiltm.substring(0,2));
                        int menit = Integer.valueOf(ambiltm.substring(3));
                        cal_alarm.set(Calendar.HOUR_OF_DAY, jam);
                        cal_alarm.set(Calendar.MINUTE, menit);
                        cal_alarm.set(Calendar.SECOND, 0);

                        if (cal_alarm.before(cal_now)){
                            cal_alarm.add(Calendar.DATE, 0);
                        }
                        Intent intent = new Intent(HalamanUtama.this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(HalamanUtama.this,0, intent,0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                        //Toast.makeText(getApplicationContext(), "Muncul tanggal", Toast.LENGTH_SHORT).show();
                        /*
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HalamanUtama.this, channelid )
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle( "E-SCHEDULE" )
                                .setContentText( "Ada tugas yang harus diselesaikan" );
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
                        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                            int importance = NotificationManager. IMPORTANCE_HIGH ;
                            NotificationChannel notificationChannel = new
                                    NotificationChannel( channelnotif , "contoh channel" , importance) ;
                            notificationChannel.enableLights( true ) ;
                            notificationChannel.setLightColor(Color. RED ) ;
                            mBuilder.setChannelId( channelnotif ) ;
                            assert mNotificationManager != null;
                            mNotificationManager.createNotificationChannel(notificationChannel) ;
                        }
                        assert mNotificationManager != null;
                        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
                        */



                    }

                }
                    

            }
        }

        public void getYourData(){
            getAppCategoryDetail();
        }

        void editDialog(){
            //final Dialog dialog = new Dialog(this);
            dialog = new Dialog(this);
            //Mengeset judul dialog
            dialog.setTitle("Edit Task");

            //Mengeset layout
            dialog.setContentView(R.layout.view_custom_dialog);

            //Membuat agar dialog tidak hilang saat di click di area luar dialog
            dialog.setCanceledOnTouchOutside(false);

            Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
            Button saveButton = (Button) dialog.findViewById(R.id.button_save);
            ImageButton date = (ImageButton) dialog.findViewById(R.id.date);
            EditText tgs = (EditText) dialog.findViewById(R.id.editTextTask);
            EditText tgl = (EditText) dialog.findViewById(R.id.ettgl);
            EditText time = (EditText) dialog.findViewById(R.id.ettime);
            TextView ttl_dialog = (TextView) dialog.findViewById(R.id.titleDialog);
            ttl_dialog.setText("Edit Task");

            Calendar calendar = Calendar.getInstance();
            hari = calendar.get(Calendar.DAY_OF_MONTH);
            bulan = calendar.get(Calendar.MONTH);
            tahun = calendar.get(Calendar.YEAR);
            tgs.setText(name_edt);
            tgl.setText(tgedt);
            time.setText(tmedt);


            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(HalamanUtama.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            //time.setText(selectedHour + ":" + selectedMinute);
                            //getTime = selectedHour + ":" + selectedMinute;
                            gethour = selectedHour+"";
                            getminute = selectedMinute+"";
                            if (gethour.length() < 2){
                                gethour2="0"+selectedHour;
                            }
                            else {
                                gethour2=selectedHour+"";
                            }
                            if (getminute.length() < 2){
                                getminute2="0"+selectedMinute;
                            }
                            else {
                                getminute2=selectedMinute+"";
                            }
                            time.setText(gethour2 + ":" +getminute2);
                        }
                    }, hour, minute, true);
                    mTimePicker.show();
                }
            });
            //BUTTON UNTUK MENAMBAHKAN TANGGAL
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anu();
                    //tgl.setText(getdate);
                }
            });
            //AKHIR DARI BUTTON PENAMBAHAN TANGGAL

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HalamanUtama.this, "Data saved", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                    //add record with help of ContentValues and DBHelper class object
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.C_ENAME, tgs.getText().toString());
                    values.put(DBHelper.C_DATE, tgl.getText().toString());
                    values.put(DBHelper.C_TIME, time.getText().toString());
                    //call update method of sqlitedb class and close after performing task
                    db = helper.getWritableDatabase();
                    db.update(DBHelper.TABLE, values, DBHelper.C_ID + "=?",new String[]{selected_ID});
                    db.close();

                    //clearFields();
                    Toast.makeText(HalamanUtama.this, "Berhasil Mengedit Tugas", Toast.LENGTH_LONG).show();
                    deltask.setText("Today");

                    fetchData();
                    dataTask();
                    notification();
                    dialog.dismiss();

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //tgl.setText(newDate.getTime().toString());
                    //deltask.setText("See All");
                }
            });

            //Menampilkan custom dialog
            dialog.show();
        }

        void tampilToday(){
            Listdate = new ArrayList<String>();
            String ambildate = "", ambildate2="";
            String tgNow = "";
            db = helper.getReadableDatabase();
            Calendar calendar = Calendar.getInstance();
            hari = calendar.get(Calendar.DAY_OF_MONTH);
            bulan = calendar.get(Calendar.MONTH);
            tahun = calendar.get(Calendar.YEAR);
            String gethari = String.valueOf(hari);
            bulan = bulan+1;
            String getbulan = String.valueOf(bulan);
            if (gethari.length() < 2){
                gethari = "0"+hari;
            }
            else {
                gethari = hari+"";
            }
            if(getbulan.length() < 2){
                getbulan = "0"+bulan;
            }
            else {
                getbulan = bulan+"";
            }
            tgNow = (new StringBuilder()
                    .append(tahun).append("-").append(getbulan).append("-").append(gethari).toString());
            String selection = DBHelper.C_DATE + " = ?";
            String[] selectionArgs = {tgNow};

            Cursor cursor = db.query(DBHelper.TABLE, null, null, null,null, null, null);
            cursor.moveToFirst();//Memulai Cursor pada Posisi Awal
            for (int count = 0; count < cursor.getCount(); count++) {

                cursor.moveToPosition(count);//Berpindah Posisi dari no index 0 hingga no index terakhir
                Listdate.add(cursor.getString(2));//Menambil Data Dari Kolom 2 (Date)

                for (int i = 0; i<Listdate.size(); i++) {
                    ambildate = Listdate.get(i);
                    if (ambildate.equals(tgNow)){
                        ambildate2 = ambildate;
                    }
                    //Membuat if untuk mencari data tanggal yang sama
                }

            }
            if (ambildate2.equals(tgNow)){
                Cursor c = db.query(DBHelper.TABLE, null, selection, selectionArgs,null, null, null);

                adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
                tasks.setAdapter(adapter);
                deltask.setText("See All");
            }
            else{
                Cursor c = db.query(DBHelper.TABLE, null, selection, selectionArgs,null, null, null);

                adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
                tasks.setAdapter(adapter);
                deltask.setText("See All");
                notask.setText("No Task Today");
            }

        }


        void SearchData(){
            //final Dialog dialog = new Dialog(this);
            dialog = new Dialog(this);
            //Mengeset judul dialog
            dialog.setTitle("Edit Task");

            //Mengeset layout
            dialog.setContentView(R.layout.search_dialog);

            //Membuat agar dialog tidak hilang saat di click di area luar dialog
            dialog.setCanceledOnTouchOutside(false);

            Button cancel = (Button) dialog.findViewById(R.id.button_cancell);
            Button btnSearch = (Button) dialog.findViewById(R.id.button_search);
            tgldari = (EditText) dialog.findViewById(R.id.editTexttg1);
            tglsampai = (EditText) dialog.findViewById(R.id.editTexttg);

            Calendar calendar = Calendar.getInstance();
            hari = calendar.get(Calendar.DAY_OF_MONTH);
            bulan = calendar.get(Calendar.MONTH);
            tahun = calendar.get(Calendar.YEAR);

            //set tanggal sekarang
            tgldari.setText(new StringBuilder()
                    .append(tahun).append("-").append(bulan+1).append("-").append(hari));

            tglsampai.setText(new StringBuilder()
                    .append(tahun).append("-").append(bulan+1).append("-").append(hari));

            //BUTTON UNTUK MENAMBAHKAN TANGGAL
            tgldari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePick2();
                    //tgl.setText(getdate);
                }
            });

            //BUTTON UNTUK MENAMBAHKAN TANGGAL
            tglsampai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePick();
                    //tgl.setText(getdate);
                }
            });
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterData();
                    dialog.dismiss();
                    deltask.setText("See All");
                    //tgl.setText(newDate.getTime().toString());
                    //deltask.setText("See All");
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //tgl.setText(newDate.getTime().toString());
                    //deltask.setText("See All");
                }
            });


            //Menampilkan custom dialog
            dialog.show();
        }


        void DatePick(){
            Calendar newCalendar = Calendar.getInstance();
            //datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            tglsampai = (EditText) dialog.findViewById(R.id.editTexttg);
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    /**
                     * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                     */
                    // INI MAH NGIDE SYNTAX CUY !!!!!!
                    //mencoba mengambil tanggal sekarang
                    String tglsekarang = "";
                    Calendar calendar = Calendar.getInstance();
                    hari = calendar.get(Calendar.DAY_OF_MONTH);
                    bulan = calendar.get(Calendar.MONTH);
                    tahun = calendar.get(Calendar.YEAR);
                    tglsekarang = (new StringBuilder()
                            .append(hari).append("-").append(bulan + 1).append("-").append(tahun).toString());
                    //AKHIR DARI NGIDE SYNTAX
                    /**
                     * Set Calendar untuk menampung tanggal yang dipilih
                     */
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    //getdate = newDate.getTime().toString();
                    String gethari = String.valueOf(dayOfMonth);
                    monthOfYear = monthOfYear+1;
                    String getbulan = String.valueOf(monthOfYear);
                    if (gethari.length() < 2){
                        gethari = "0"+dayOfMonth;
                    }
                    else {
                        gethari = dayOfMonth+"";
                    }
                    if(getbulan.length() < 2){
                        getbulan = "0"+monthOfYear;
                    }
                    else {
                        getbulan = monthOfYear+"";
                    }
                    getdate = year + "-" + (getbulan) + "-" + gethari;
//                if(getdate.equals(tglsekarang)){
//                    tgl.setText("Today");
//                }
//                else {
                    tglsampai.setText(getdate);
//                }
                    //tgl.setText(newDate.getTime().toString());
                }
            },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }

    void DatePick2(){
        Calendar newCalendar = Calendar.getInstance();
        //datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
        tgldari = (EditText) dialog.findViewById(R.id.editTexttg1);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */
                // INI MAH NGIDE SYNTAX CUY !!!!!!
                //mencoba mengambil tanggal sekarang
                String tglsekarang = "";
                Calendar calendar = Calendar.getInstance();
                hari = calendar.get(Calendar.DAY_OF_MONTH);
                bulan = calendar.get(Calendar.MONTH);
                tahun = calendar.get(Calendar.YEAR);
                tglsekarang = (new StringBuilder()
                        .append(hari).append("-").append(bulan + 1).append("-").append(tahun).toString());
                //AKHIR DARI NGIDE SYNTAX
                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //getdate = newDate.getTime().toString();
                String gethari = String.valueOf(dayOfMonth);
                monthOfYear = monthOfYear+1;
                String getbulan = String.valueOf(monthOfYear);
                if (gethari.length() < 2){
                    gethari = "0"+dayOfMonth;
                }
                else {
                    gethari = dayOfMonth+"";
                }
                if(getbulan.length() < 2){
                    getbulan = "0"+monthOfYear;
                }
                else {
                    getbulan = monthOfYear+"";
                }
                getdate = year + "-" + (getbulan) + "-" + gethari;
//                if(getdate.equals(tglsekarang)){
//                    tgl.setText("Today");
//                }
//                else {
                tgldari.setText(getdate);
//                }
                //tgl.setText(newDate.getTime().toString());
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void tambahTugas(View view) {
        showCustomDialog();
    }

    public void Search(View view) {
        //SearchData();
        CariKegiatan();
    }

    public void filterData(){
        String dari="", sampai="", getmonthdr="", getmonthsmp="", dari2="", sampai2="", tgdari="", selection="", getdaysmp;
        int getmonthdr2, getmonthsmp2, i, j, x, getdaysmp2;
        Cursor c;
        SimpleCursorAdapter adapter2;
        dari = tgldari.getText().toString();
        sampai = tglsampai.getText().toString();
        db = helper.getReadableDatabase();
       // " + TABLE_NAME + " WHERE "+DBHelper.C_DATE+" BETWEEN'"+dari+"' AND '"+sampai+"'"
        // === NGIDE MENGGUNAKAN BETWEEN
        //String selection = DBHelper.C_DATE + " BETWEEN CAST('"+dari+"' AS DATE) AND CAST('"+sampai+"' AS DATE)";
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
//            dari = (df2.format(dari));
//            sampai = (df2.format(sampai));
//        } catch (java.text.ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        selection = DBHelper.C_DATE + " BETWEEN '"+dari+"' AND '"+sampai+"'";
        //String selection = DBHelper.C_DATE + " BETWEEN ? AND ?";
        //String selection = DBHelper.C_DATE + " = ?";
        //String[] selectionArgs = {dari, sampai};
        c = db.query(DBHelper.TABLE, null, selection, null,null, null, null);

        if (c.getCount() > 0){
            adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
            tasks.setAdapter(adapter);
        }
        else if (c.getCount() == 0){
            adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
            tasks.setAdapter(adapter);
            if (c.getCount() == 0){
                notask.setText("Cannot Find Task");
            }

//            notask.setText("Cannot Find Task");
//            getmonthdr = dari.substring(3,2);
//            System.out.println("Bulan "+getmonthdr);
//            getmonthdr2 = Integer.valueOf(getmonthdr);
//            getmonthsmp = sampai.substring(3,2);
//            getmonthsmp2 = Integer.valueOf(getmonthsmp);
//            getdaysmp = sampai.substring(0,2);
//            getdaysmp2 = Integer.valueOf(getdaysmp);
//            //getmonthdr2 = getmonthdr2 + getmonthsmp2;
//            i = getmonthdr2+1;
//            j = getmonthsmp2 - getmonthdr2;
//            //while (i <= getmonthsmp2){
//                dari2 = (new StringBuilder()
//                        .append("10").append("-").append("11").append("-").append("2022").toString());
//                sampai2 = (new StringBuilder()
//                        .append(getdaysmp).append("-").append(getmonthsmp2).append("-").append("2022").toString());
//                String selection2 = DBHelper.C_DATE + " BETWEEN '"+dari2+"' AND '"+sampai2+"'";
//                Cursor cursor = db.query(DBHelper.TABLE, null, selection2, null,null, null, null);
//                adapter2 = new SimpleCursorAdapter(this, R.layout.list_item, cursor, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
//                tasks.setAdapter(adapter2);
                //i++;
                //j++;

            //}


//            for (i; i<=getmonthsmp2; i++) {
//                tgdari = (new StringBuilder()
//                        .append("01").append("-").append(i).append("-").append("2022").toString());
//                sampai2 = (new StringBuilder()
//                        .append("01").append("-").append(j).append("-").append("2022").toString());
//                selection = DBHelper.C_DATE + " BETWEEN '"+tgdari+"' AND '"+sampai2+"'";
//                c = db.query(DBHelper.TABLE, null, selection, null,null, null, null);
//                //Membuat if untuk mencari data tanggal yang sama
//            }
        }
        else{
            notask.setText("Cannot Find Task");
        }
        //adapter = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);

        //MENGHITUNG JUMLAH BARIS YANG ADA DI DATABASE
//        if (c.getCount() == 0){
//            notask.setText("Cannot Find Task");
//        }

        //tasks.setAdapter(adapter);
    }

    public void CariKegiatan(){
        //final Dialog dialog = new Dialog(this);
        dialog = new Dialog(this);
        //Mengeset judul dialog
        dialog.setTitle("Edit Task");

        //Mengeset layout
        dialog.setContentView(R.layout.search_kegiatan);

        //Membuat agar dialog tidak hilang saat di click di area luar dialog
        dialog.setCanceledOnTouchOutside(false);

        Button batal = (Button) dialog.findViewById(R.id.button_batal);
        Button btnSearch = (Button) dialog.findViewById(R.id.button_cari);
        etkegiatan = (EditText) dialog.findViewById(R.id.editTextkegiatan);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlsearchData();
                dialog.dismiss();
                if (etkegiatan.getText().toString().equals("")){
                    deltask.setText("Today");
                }
                else{
                    deltask.setText("See All");
                }
                //deltask.setText("See All");
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //tgl.setText(newDate.getTime().toString());
                //deltask.setText("See All");
            }
        });
        //Menampilkan custom dialog
        dialog.show();
    }

    void sqlsearchData(){
        String getkegiatan="";
        getkegiatan = etkegiatan.getText().toString();

        db = helper.getReadableDatabase();
        // " + TABLE_NAME + " WHERE "+DBHelper.C_DATE+" BETWEEN'"+dari+"' AND '"+sampai+"'"
        String selection = DBHelper.C_ENAME + " LIKE'%"+getkegiatan+"%'";
        //String selection = DBHelper.C_DATE + " = ?";
        //String[] selectionArgs = {getkegiatan};
        Cursor cursor = db.query(DBHelper.TABLE, null, selection, null,null, null, null);

        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, new String[]{DBHelper.C_ENAME, DBHelper.C_DATE, DBHelper.C_TIME}, new int[]{R.id.tvtgs, R.id.tvtgl, R.id.tvtm},1);
        if (cursor.getCount() == 0){
            notask.setText("Cannot Find Task");
        }
        tasks.setAdapter(adapter);
    }
        /*
        public void ambildata(){
            db = new DBHelper(cytaty.this);
            myDatabaseHelper.openDataBase();

            String text = myDatabaseHelper.getYourData(); //this is the method to query

            myDatabaseHelper.close();
        }
        */
         

}