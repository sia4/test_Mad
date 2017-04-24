package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.User;



public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputSurname;
    private Button btnSignIn, btnLogIn, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference Firebase_DB;

    /*

        TODO: PROBLEMA di Gestione di Sessione

        Se vedi, entra erroneamente dentro a user != null
        anche se l'utente è stato cancellato dall'auth di
        FireBase.

        (Provato su Emulatore)

        Stesso problema si riscontra su Login e Main Activity


        */



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        Firebase_DB = FirebaseDatabase.getInstance().getReference();

        CheckLoggedUser();


        /*mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    System.out.println("++++++ OnAUTHSTATECHANGED +++++");

                    System.out.println("++++++SignIn QUI che cazzo succede+++++");
                    System.out.println(user.getEmail());

                    CheckUser(user);
                }

            }

        };*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnLogIn = (Button) findViewById(R.id.log_in_button);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        inputSurname = (EditText) findViewById(R.id.surname);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //TODO dare la possiblità di riottenere la password

        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        /*

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        */
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                final String name = inputName.getText().toString().trim();
                final String surname = inputSurname.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(surname)) {
                    Toast.makeText(getApplicationContext(), "Enter Surname!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), getString(R.string.minimum_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignInActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (!task.isSuccessful() || user == null) {
                                    Toast.makeText(SignInActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    writeNewUser(user.getUid(), name, surname, email);

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();

                                }
                            }

                        });


            }
        });
    }

    // TODO: questa è la funzione che scrive in DB l'utente appena registrato.
    // Notare che utilizzo l'oggetto di tipo User, fondamentale in quanto
    // ci permette di mappare il JSON con la nostra classe in modo automatico

    private void writeNewUser(String userId, String name, String surname, String email) {

        String username = email + "_" + name + surname;
        User user = new User(username, email, name, surname);

        Firebase_DB.child("Users").child(userId).setValue(user);

    }

    protected void CheckUser(FirebaseUser U) {

        // verifica che l'utente sia presente in DB

        final String uID = U.getUid();

        ValueEventListener SingleEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean user_exists = false;

                if (dataSnapshot.getValue() != null) {

                    System.out.println("++++++SignIn UTENTE ESISTENTE: " + dataSnapshot.getValue().toString());
                    user_exists = true;

                } else {

                    user_exists = false;

                }

                if (user_exists) {

                    System.out.println("++++++SignIn UTENTE ESISTENTE E LOGGATO +++++");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class)); //ok
                    finish();

                } else {


                    System.out.println("++++++ NOPE --> UTENTE NON ESISTENTE +++++");

                    mAuth.signOut();

                    startActivity(new Intent(SignInActivity.this, SignInActivity.class)); //refresh
                    finish();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        Firebase_DB.child("Users").child(uID).addListenerForSingleValueEvent(SingleEvent);

    }

    protected void CheckLoggedUser() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            System.out.println("++++++SignIn CHECKLOGGED -- QUI che cazzo succede+++++");
            System.out.println(user.getEmail());

            CheckUser(user);

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    @Override
    protected void onStart(){
        super.onStart();
        //status="OTHER";
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //status="OTHER";
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

