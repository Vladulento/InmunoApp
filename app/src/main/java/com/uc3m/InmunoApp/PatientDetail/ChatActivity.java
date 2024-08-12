package com.uc3m.InmunoApp.PatientDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc3m.InmunoApp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference chatRef;
    private String currentUserEmail;

    private EditText messageEditText;
    private MessageAdapter messageAdapter;
    private RecyclerView messagesRecyclerView;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Obtener datos de la Intent
        currentUserEmail = getIntent().getStringExtra("currentUserEmail");
        String otherUserEmail = getIntent().getStringExtra("otherUserEmail");
        String chatId = getIntent().getStringExtra("chatId");

        // Inicializar Firebase
        assert chatId != null;
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        // Configurar UI
        messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        // Configurar el RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, currentUserEmail);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Configurar el Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(otherUserEmail);

        // Configurar el botón de enviar
        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });

        // Leer mensajes
        chatRef.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                // Desplazar hacia abajo para mostrar el mensaje más reciente
                messagesRecyclerView.post(() -> messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String text) {
        String messageId = chatRef.child("messages").push().getKey();
        Message message = new Message(currentUserEmail, text, System.currentTimeMillis());
        assert messageId != null;
        chatRef.child("messages").child(messageId).setValue(message);
    }
}