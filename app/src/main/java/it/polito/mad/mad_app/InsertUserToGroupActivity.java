package it.polito.mad.mad_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.User;

/**
 * Created by Silvia Vitali on 27/04/2017.
 */

public class InsertUserToGroupActivity extends AppCompatActivity {

    private static final int REQUEST_INVITE = 0;
    public String gId = null;
    public User ud;
    public String key;
    public String gName;
    public String gPath;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_to_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.insert_user_to_group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        gId = i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        gPath = i.getStringExtra("groupPath");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add user");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case android.R.id.home: //farlo tornare a GroupInfoActivity
                setResult(RESULT_OK, null);
                finish();
                return true;

            case R.id.action_menu_done:
                final EditText uEmail = (EditText) findViewById(R.id.uEmail);

                if(uEmail.getText().toString().equals("")) {
                    Toast.makeText(InsertUserToGroupActivity.this, "Please insert an email!", Toast.LENGTH_LONG).show();
                } else {
                    final DatabaseReference mTest = FirebaseDatabase.getInstance().getReference();
                    final Query quer=mTest.child("Users").orderByChild("email");
                    quer.equalTo(uEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                ud = userSnapshot.getValue(User.class);
                                key=userSnapshot.getKey(); //ritorna la chive dell'utente che quindi
                                // poi va inserito nell'oggetto gruppo come chiave:true
                                Toast.makeText(InsertUserToGroupActivity.this, key, Toast.LENGTH_LONG).show();
                            }
                            if(key == null) {
                                Toast.makeText(InsertUserToGroupActivity.this, "This user is not registred to the service!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(InsertUserToGroupActivity.this)
                                    .setTitle("You friend has not downloaded the app, yet!")
                                    .setMessage("Do you want to invite him to use the app?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            onInviteClicked("codetest");
                                            //onInviteClicked("nome", "cognome", "groupname", "identificativo");
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();

                            } else {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/name/");
                                myRef.setValue(gName);
                                myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/imagePath/");
                                myRef.setValue(gPath);
                                myRef = database.getReference("/Groups/"+gId+"/members/"+key);
                                myRef.setValue(true);

                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError eError) {

                        }
                    });
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("INFO", "onActivityResult: sent invitation " + id);
                }
                finish();
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }

    }


    //Using an app as client
    /*
    private void sendEmail(String email) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , email);
        i.putExtra(Intent.EXTRA_SUBJECT, "Someone added you to a group on AllaRomana");
        i.putExtra(Intent.EXTRA_TEXT   , "Hi! You have been invited to join to a group on AllaRomana. Download the app to start manage you expenses.");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(InsertGroupActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
*/

    private void onInviteClicked(String code) {


        Log.d("INFO", "sono qui");
        String msg = "I invite you to join a group on AllaRomana. Insert this code in the settings: " +
                code + ".";

        Intent intent = new AppInviteInvitation.IntentBuilder("Invite your friends!")
                .setMessage("AllaRomana")
                .setEmailHtmlContent("Hi! I invited you to join a group on AllaRomana. Download the app and insert the invitation code: "+ code
                        + ". See you on AllaRomana!")
                .setDeepLink(Uri.EMPTY)
                .setEmailSubject("Invite you on AllaRomana")
                .build();


        startActivityForResult(intent, REQUEST_INVITE);
    }



}