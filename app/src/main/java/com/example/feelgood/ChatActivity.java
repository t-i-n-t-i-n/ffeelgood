package com.example.helpit;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpit.databinding.ActivityMainBinding;

public class ChatActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //code to check the user signin 

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
         
            // Load chat room contents
            displayChatMessages();
        }


        FloatingActionButton fab = 
        (FloatingActionButton)findViewById(R.id.fab);
 
fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        EditText input = (EditText)findViewById(R.id.input);
 
        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName())
                );
 
        // Clear the input
        input.setText("");
    }
});

            }
        
     
    }

    @Override
protected void onActivityResult(int requestCode, int resultCode, 
                                            Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
 
    if(requestCode == SIGN_IN_REQUEST_CODE) {
        if(resultCode == RESULT_OK) {
            Toast.makeText(this,
                    "Successfully signed in. Welcome!",
                    Toast.LENGTH_LONG)
                    .show();
            displayChatMessages();
        } else {
            Toast.makeText(this,
                    "We couldn't sign you in. Please try again later.",
                    Toast.LENGTH_LONG)
                    .show();
 
            // Close the app
            finish();
        }
    }
 
}

private void displayChatMessages() { //function to display the chat message
 


ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
 
adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
        R.layout.message, FirebaseDatabase.getInstance().getReference()) {
    @Override
    protected void populateView(View v, ChatMessage model, int position) {
        // Get references to the views of message.xml
        TextView messageText = (TextView)v.findViewById(R.id.message_text);
        TextView messageUser = (TextView)v.findViewById(R.id.message_user);
        TextView messageTime = (TextView)v.findViewById(R.id.message_time);
 
        // Set their text
        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());
 
        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                model.getMessageTime()));
    }
};
 
listOfMessages.setAdapter(adapter);
}

//chat message class
public class ChatMessage {
 
    private String messageText;
    private String messageUser;
    private long messageTime;
 
    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
 
        // Initialize to current time
        messageTime = new Date().getTime();
    }
 
    public ChatMessage(){
 
    }
 
    public String getMessageText() {
        return messageText;
    }
 
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
 
    public String getMessageUser() {
        return messageUser;
    }
 
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
 
    public long getMessageTime() {
        return messageTime;
    }
 
    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}

