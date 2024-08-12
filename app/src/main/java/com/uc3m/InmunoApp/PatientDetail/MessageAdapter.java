package com.uc3m.InmunoApp.PatientDetail;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc3m.InmunoApp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private final Context context;
    private final String currentUserEmail;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    public MessageAdapter(Context context, List<Message> messages, String currentUserEmail) {
        this.context = context;
        this.messages = messages;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == MessageType.SENT ? R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getText());
        holder.timestampTextView.setText(dateFormat.format(new Date(message.getTimestamp())));
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSender().equals(currentUserEmail) ? MessageType.SENT : MessageType.RECEIVED;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.sentMessageTextView) != null) {
                messageTextView = itemView.findViewById(R.id.sentMessageTextView);
                timestampTextView = itemView.findViewById(R.id.sentTimestampTextView);
            } else {
                messageTextView = itemView.findViewById(R.id.receivedMessageTextView);
                timestampTextView = itemView.findViewById(R.id.receivedTimestampTextView);
            }
        }
    }

    private static class MessageType {
        static final int SENT = 0;
        static final int RECEIVED = 1;
    }
}