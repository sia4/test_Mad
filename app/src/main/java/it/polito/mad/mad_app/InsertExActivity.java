package it.polito.mad.mad_app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;


public class InsertExActivity extends AppCompatActivity {

    private String name;
    private String description;
    private String category;
    private String currency;
    private float value;
    private String algorithm;
    private TreeMap<Integer, UserData> users = new TreeMap<>();
    static private RecyclerView userRecyclerView;
    static private AlgorithmParametersAdapter uAdapter;
    static final int REQUEST_TAKE_PHOTO = 1;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private String tmp;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_ex_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Expense");
        ImageButton bill = (ImageButton)findViewById(R.id.AddPhotoBill);





        bill.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //galleryAddPic();
                }

        } );

        userRecyclerView = (RecyclerView) findViewById(R.id.algorithmParameters);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertExActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        final String Gname = intent.getStringExtra("GroupName");
        int i = 0;
        for(UserData u :MainData.getInstance().getGroup(Gname).getlUsers()) {
            users.put(i,u);
            i++;
        }
        users.put(i, MainData.getInstance().returnMyData());

        Spinner spinner = (Spinner) findViewById(R.id.Currency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        Set<String> CS = MainData.getInstance().getGroup(Gname).getCurrencies().keySet();
        List<String> Currencies = new ArrayList<>();

        Currencies.addAll(CS);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.currency_item, Currencies);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //users.add(0, new UserData("null", "Me", "", 000));
        final Spinner Talgorithm = (Spinner)findViewById(R.id.ChooseAlgorithm);
        final EditText Tvalue = (EditText) findViewById(R.id.value);
        final Spinner Tcurrency = (Spinner) findViewById(R.id.Currency);
        final TextView algInfo = (TextView) findViewById(R.id.alg_info);
        final TextView algInfoSmall = (TextView) findViewById(R.id.alg_info_small);
        Talgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==1) {
                    if(Tvalue.getText().toString().equals("")) {
                        Toast.makeText(InsertExActivity.this, "Please insert the expense value.", Toast.LENGTH_LONG).show();
                        Talgorithm.setSelection(0);
                    } else {
                        uAdapter = new AlgorithmParametersAdapter(new ArrayList<>(users.values()), position, 10, algInfo, algInfoSmall);
                        userRecyclerView.setAdapter(uAdapter);
                    }

                }
                else if(position == 2) {
                    if (Tvalue.getText().toString().equals("")) {
                        Toast.makeText(InsertExActivity.this, "Please insert the expense value.", Toast.LENGTH_LONG).show();
                        Talgorithm.setSelection(0);
                        /*
                        uAdapter = new AlgorithmParametersAdapter(users, position, 0);
                        userRecyclerView.setAdapter(uAdapter);
                        */
                    } else if( Tcurrency.getSelectedItem().toString().equals("Select currency")) {
                        Toast.makeText(InsertExActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();
                    } else {
                        uAdapter = new AlgorithmParametersAdapter(new ArrayList<UserData>(users.values()), position, Float.parseFloat(Tvalue.getText().toString()), Tcurrency.getSelectedItem().toString(), algInfo, algInfoSmall);
                        userRecyclerView.setAdapter(uAdapter);
                    }
                } else {
                    userRecyclerView.setAdapter(new AlgorithmParametersAdapter(new ArrayList<UserData>(), position, 10, algInfo, algInfoSmall));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );
       Tvalue.addTextChangedListener(new TextWatcher() {
           int p;
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               if(Talgorithm.getSelectedItem().toString().equals("by import") && !Tcurrency.getSelectedItem().toString().equals("Select currency")) {
                   if(!Tvalue.getText().toString().isEmpty()) {
                       uAdapter = new AlgorithmParametersAdapter(new ArrayList<UserData>(users.values()), 2, Float.parseFloat(Tvalue.getText().toString()), Tcurrency.getSelectedItem().toString(), algInfo, algInfoSmall);
                       userRecyclerView.setAdapter(uAdapter);
                   }
                   else{
                       uAdapter = new AlgorithmParametersAdapter(new ArrayList<UserData>(users.values()), 2, 0, Tcurrency.getSelectedItem().toString(), algInfo, algInfoSmall);
                       userRecyclerView.setAdapter(uAdapter);
                   }
               }
           }
       });

        String s = "";
    }
    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(InsertExActivity.this, "Error creating photo file!", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                //Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            c.moveToFirst();
            int cIndex = c.getColumnIndex(filePathColumn[0]);
            String picturePath = c.getString(cIndex);
            c.close();
            ImageButton bill = (ImageButton) findViewById(R.id.AddPhotoBill);
            bill.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            flag_img_edited = true;
            tmp = picturePath;
            //Toast.makeText(this, "path:"+ tmp, Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_menu_done:
                Intent i1 = getIntent();
                final String Gname = i1.getStringExtra("GroupName");

                final EditText Tname = (EditText) findViewById(R.id.Name);
                final EditText Tdescription = (EditText) findViewById(R.id.Description);
                final Spinner Tcategory = (Spinner) findViewById(R.id.Category);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.Currency);
                final Spinner Talgorithm = (Spinner) findViewById(R.id.ChooseAlgorithm);
                final EditText Tvalue = (EditText) findViewById(R.id.value);

                FirebaseDatabase database = FirebaseDatabase.getInstance();


                int flagok = 1;
                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                algorithm = Talgorithm.getSelectedItem().toString();
                if(name.equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert name.", Toast.LENGTH_LONG).show();
                } else if(currency.equals("Select currency")) {
                    Toast.makeText(InsertExActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();
                } else if(Tvalue.getText().toString().equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert value.", Toast.LENGTH_LONG).show();
                } else if(category.equals("Select category")) {
                    Toast.makeText(InsertExActivity.this, "Please insert category.", Toast.LENGTH_LONG).show();
                } else {
                    value = Float.parseFloat(Tvalue.getText().toString());

                   if (algorithm.equals("equally")) {
                        MainData.getInstance().getGroup(Gname).allaRomana(value, currency);
                        DatabaseReference myRef = database.getReference("/Expenses/" + Gname).push();
                        myRef.setValue(MainData.getInstance().getGroup(Gname).addExpensive(name, description, category, currency, value, value - (value / (MainData.getInstance().getGroup(Gname).getlUsers().size() + 1)), algorithm ));

                        flagok = 1;
                    }


                else{
                    float algValue, algSum=0, meValue=0;
                    int i;
                    for(i = 0; i< uAdapter.getItemCount(); i++){
                        View view = userRecyclerView.getChildAt(i);
                        EditText EditValue = (EditText)view.findViewById(R.id.alg_value);
                        if(EditValue.getText().toString().equals(""))
                            algValue = 0;
                        else
                            algValue = Float.parseFloat(EditValue.getText().toString());
                        if(i==uAdapter.getItemCount()-1)
                            meValue = algValue;
                        else {
                            if (algorithm.equals("by percentuage"))
                                MainData.getInstance().getGroup(Gname).addTouPercentuageMap(users.get(i).getEmail(), (int)algValue);
                            else
                                MainData.getInstance().getGroup(Gname).addTouImportMap(users.get(i).getEmail(), (int)algValue);
                        }
                        algSum += algValue;
                    }

                    if((algorithm.equals("by percentuage") && algSum==100)) {
                        //MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, value - (value*meValue/100), algorithm);
                        MainData.getInstance().getGroup(Gname).byPercentuage(value, currency);
                        DatabaseReference myRef = database.getReference("/Expenses/" + Gname).push();
                        myRef.setValue(MainData.getInstance().getGroup(Gname).addExpensive(name, description, category, currency, value, value - (value*meValue/100), algorithm));
                        flagok = 1;
                    }

                    if((algorithm.equals("by import") && algSum==value)) {
                        //MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, (value- meValue) , algorithm);
                        MainData.getInstance().getGroup(Gname).byImport(value, currency);
                        DatabaseReference myRef = database.getReference("/Expenses/" + Gname).push();
                        myRef.setValue(MainData.getInstance().getGroup(Gname).addExpensive(name, description, category, currency, value, (value- meValue) , algorithm));
                        flagok = 1;
                    }

                    if((algorithm.equals("by percentuage") && algSum!=100)){
                        flagok = 0;
                        String text = String.format("Percentuage sum values must be equal to 100!", algSum, i);
                        Toast.makeText(InsertExActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                    if((algorithm.equals("by import") && algSum!=value)){
                        flagok = 0;
                        Toast.makeText(InsertExActivity.this, "Import sum values must be equal to the total value!", Toast.LENGTH_LONG).show();
                    }

                }

                    if (flagok == 1) {
                        Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                        i2.putExtra("name", Gname);
                        setResult(RESULT_OK, i2);
                        finish();

                        return true;
                    } else {
                        return super.onOptionsItemSelected(item);
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
