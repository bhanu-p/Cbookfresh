package com.example.delevere.cbook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ChatsTab extends Fragment {

    ListView lv;
    Cursor cursor1;
    private DataBaseHelper db;
    private SimpleCursorAdapter dataAdapter;
    public static final String PREFS_NAME = "LoginPrefs";
    int RQS_1 = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chats_tab, container, false);
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        lv = (ListView) rootView.findViewById(R.id.list);
        db = new DataBaseHelper(getContext());

        //displayContactold();
        displayContactsNew();


        return rootView;
    }

    private void displayContactsNew() {
        Cursor cursor = db.getAllContacts();

        String[] columns = new String[] {
                DataBaseHelper.KEY_NAME,
                DataBaseHelper.KEY_PHONENUMBER
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.listcontactname,
                R.id.listcontactnumber,

        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this.getContext(), R.layout.contacts_list,
                cursor,
                columns,
                to,
                0);


        // Assign adapter to ListView
        lv.setAdapter(dataAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) lv.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));
                //Toast.makeText(getActivity(),      countryCode, Toast.LENGTH_SHORT).show();




                //Toast.makeText(getContext(), person.toString(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(),ChatPage.class);

                i.putExtra("name",countryCode);

                startActivity(i);



            }
        });
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){


        switch (item.getItemId()){
            case R.id.sms: {
                startActivity(new Intent(getActivity(),SmsActivity.class));
                return true;
            }
            case R.id.event: {
                if (weHavePermissionToReadContacts()) {
                    MyTask myTask = new MyTask(getActivity());
                    myTask.execute("parameter");
                }else {
                    requestReadContactsPermissionFirst();

                }

                //startActivity(new Intent(getActivity(), CreateEventActivity.class));


                return true;
            }
            case R.id.contacts: {
                Toast.makeText(getActivity(), "Contacts", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(getActivity(), ContactsActivity.class));
                }catch (Exception e){
                    Toast.makeText(getActivity(), "some error", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.profile: {
                Toast.makeText(getActivity(), "Profile page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), Profile.class));
                return true;

            }




        }return false;
    }

    public class MyTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;

        public MyTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {



            db.truncatstudent();


            if (weHavePermissionToReadContacts()) {

                readTheContacts();
                cursor1 = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                getActivity().startManagingCursor(cursor1);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Session.PREFS_NAME, 0);
                String mynumber = sharedPreferences.getString(Session.KEY_phone, "9999999999");
                db.inserData("You",mynumber);

                if (cursor1.moveToFirst()) {

                    do {
                        String name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String phone = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String ph = phone.replaceAll("[^0-9]", "");
                        String p;
                        if(ph.length()>10) {
                            p = ph.substring(ph.length() - 10, ph.length());
                        }else {
                            p = ph;

                        }
                        db.inserData(name, p);
                    } while (cursor1.moveToNext());


                }


            } else {
                requestReadContactsPermissionFirst();
            }

            db.close();
            return "finish";
        }


        @Override
        protected void onPostExecute(String result) {
            displayContactsNew();
            getContacts();
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
            //Start other Activity or do whatever you want
        }
    }


    private void displayContactold() {
        if (weHavePermissionToReadContacts()) {
            readTheContacts();
            final ProgressDialog loading = ProgressDialog.show(getContext(), "Getting Contscts", "Please wait ...", false, false);

            cursor1 = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            getActivity().startManagingCursor(cursor1);


            // new listadapter, created to use android checked template
            //SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor1, from, to);


            //lv.setAdapter(listadapter);

            // adds listview so I can get data from it

            //lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            if(cursor1.moveToFirst()){

                do
                {
                    String name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phone = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String ph = phone.replaceAll("[^0-9]", "");
                    db.inserData(name, ph);


                } while (cursor1.moveToNext()) ;


            }
            loading.dismiss();



        } else {
            requestReadContactsPermissionFirst();
        }

    }

    private boolean weHavePermissionToReadContacts() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    private void readTheContacts() {
        ContactsContract.Contacts.getLookupUri(getActivity().getContentResolver(), ContactsContract.Contacts.CONTENT_URI);
    }

    private void requestReadContactsPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this.getContext(), "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            readTheContacts();
        } else {
            Toast.makeText(this.getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    public void getContacts(){
        DataBaseHelper db = new DataBaseHelper(this.getActivity());
        Cursor res = db.getContact();
        if(res.getCount() == 0){

            //show
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            buffer.append(res.getString(0));
            buffer.append(",");
        }
        sendContacts(buffer.toString());
        //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();


    }
    public void sendContacts(String contact){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Session.PREFS_NAME,0);
        String phone = sharedPreferences.getString(Session.KEY_phone,"9999999999");
        String type = "contacts";
        //Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_SHORT).show();
        UserLoginTask userLoginTask = new UserLoginTask(this.getActivity());
        userLoginTask.execute(type, phone, contact);

    }


}

