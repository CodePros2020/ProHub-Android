package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepros.prohub.model.Chat;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ToolbarHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //Activity Items

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        TextView timeTextView;
        TextView messengerTextSeen;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v, boolean isSender) {
            super(v);

            if (isSender) {
                messageTextView = itemView.findViewById(R.id.messageTextView);
                messageImageView = itemView.findViewById(R.id.messageImageView);
                messengerTextView = itemView.findViewById(R.id.messengerTextView);
                messengerImageView = itemView.findViewById(R.id.messengerImageView);
                timeTextView = itemView.findViewById(R.id.text_message_time);
                messengerTextSeen = itemView.findViewById(R.id.messengerTextSeen);
            } else {
                messageTextView = itemView.findViewById(R.id.messageTextView);
                messageImageView = itemView.findViewById(R.id.messageImageView);
                messengerTextView = itemView.findViewById(R.id.messengerTextView);
                messengerImageView = itemView.findViewById(R.id.messengerImageView);
                timeTextView = itemView.findViewById(R.id.text_message_time);
                messengerTextSeen = itemView.findViewById(R.id.messengerTextSeen);
            }
        }
    }

    private static final String TAG = "ChatActivity";
    public static final String CHAT_CHILD = "chat";
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername, myRole;
    private String mSenderName;
    private String mReceiverName;
    private String mPhoneNumber;
    private String mPhotoUrl;
    private String chatSeen;
    private SharedPreferences mSharedPreferences;
    private String chatMessageId;
    private String timestamp;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Chat, MessageViewHolder>
            mFirebaseAdapter;
    private String propId;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;

    // for export chat history
    private static final int PERMISSION_REQUEST_CODE = 100;

    // for seen and delivered
    ValueEventListener seenListener;
    DatabaseReference reference;
    List<Chat> allMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSharedPreferences = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mUsername = mSharedPreferences.getString("username", ANONYMOUS);
        mPhoneNumber = mSharedPreferences.getString("phoneNum", "0123456789");
        propId = mSharedPreferences.getString("propId", "");
        chatMessageId = getIntent().getStringExtra("Chat_ID");
        chatSeen = "false";

        myRole = mSharedPreferences.getString("myRole", "");

        String picture = mSharedPreferences.getString("profilePic", null);

        if (picture != null) {
            mPhotoUrl = picture;
        }

        /////////////////////////////////////////////////////
        // declaring the buttons

        // define the actions for each button
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        btnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        //////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////////

//        reference = FirebaseDatabase.getInstance().getReference(CHAT_CHILD);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int unread = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getChatSeen().equals("false")) {
//                        unread++;
//                    }
//                }
//
//                Log.d("UNREAD_MESSAGE", "Unread#: " + unread);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                allMessages = chats;
                int count = 0;
                if (allMessages != null) {
                    for (Chat chat : allMessages)
                    {
                        if (chat.getPhoneNumber().equals(mPhoneNumber))
                        if (chat.getPhoneNumber().equals("6475545687"))
                        {
                            count++;
                        }
                    }
                }

                Log.d("COUNT_MESSAGE", "Count: " + String.valueOf(allMessages.size()));
                Log.d("COUNT_MESSAGE", "Count: " + count);
                Log.d("CheckNumber", "Number: " + mPhoneNumber);
            }
        });

        ////////////////////////////////////////////////////////////////////////

        // for export chat history
        toolbar.setChatHistoryExportInfo(chatMessageId, propId);

        DateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a");
        Date now = Calendar.getInstance().getTime();
        timestamp = dateFormat.format(now);

        // Initialize ProgressBar and RecyclerView.
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final SnapshotParser<Chat> parser = new SnapshotParser<Chat>() {
            @NonNull
            @Override
            public Chat parseSnapshot(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                if (chat != null) {
                    chat.setChatId(snapshot.getKey());
                }
                return chat;
            }
        };

        Query chatRef = mFirebaseDatabaseReference.child(CHAT_CHILD).orderByChild("chatMessageId").equalTo(chatMessageId);

        final FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(chatRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, MessageViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {

                Chat chat = mFirebaseAdapter.getItem(position);

                if (chat.getFullName().equals(mUsername)) {
                    // If the current user is the sender of the message
                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    // If some other user sent the message
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull Chat model) {

                switch (holder.getItemViewType()) {
                    case 0: {
                        //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (model.getMessage() != null) {
                            holder.messageTextView.setText(model.getMessage());
                            holder.timeTextView.setText(model.getTimestamp());
                            holder.messageTextView.setVisibility(TextView.VISIBLE);
                            holder.messageImageView.setVisibility(ImageView.GONE);
                        } else if (model.getImageUrl() != null) {
                            String imageUrl = model.getImageUrl();
                            if (imageUrl.startsWith("gs://")) {
                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReferenceFromUrl(imageUrl);
                                storageReference.getDownloadUrl().addOnCompleteListener(
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String downloadUrl = task.getResult().toString();
                                                    Glide.with(holder.messageImageView.getContext())
                                                            .load(downloadUrl)
                                                            .into(holder.messageImageView);
                                                } else {
                                                    Log.w(TAG, "Getting download url was not successful.", task.getException());
                                                }
                                            }
                                        }
                                );
                            } else {
                                Glide.with(holder.messageImageView.getContext())
                                        .load(model.getImageUrl())
                                        .into(holder.messageImageView);
                            }
                            holder.messageImageView.setVisibility(ImageView.VISIBLE);
                            holder.messageTextView.setVisibility(TextView.GONE);
                        }

                        holder.messengerTextView.setText(model.getFullName());
                        if (model.getPhotoUrl() == null || model.getPhotoUrl().equals("")) {
                            holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(ChatActivity.this)
                                    .load(model.getPhotoUrl())
                                    .into(holder.messengerImageView);
                        }

                        if (position == mFirebaseAdapter.getItemCount() - 1)
                        {
                            if (model.getChatSeen().equals("false")) {
                                holder.messengerTextSeen.setText("Delivered");
                            } else if (model.getChatSeen().equals("true")) {
                                holder.messengerTextSeen.setText("Seen");
                            }
                        }
//                        else {
//                            holder.messengerTextSeen.setVisibility(View.GONE);
//                        }

                    }
                    break;
                    case 1: {
                        //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (model.getMessage() != null) {
                            holder.messageTextView.setText(model.getMessage());
                            holder.messageTextView.setVisibility(TextView.VISIBLE);
                            holder.messageImageView.setVisibility(ImageView.GONE);
                            holder.timeTextView.setText(model.getTimestamp());
                        } else if (model.getImageUrl() != null) {
                            String imageUrl = model.getImageUrl();
                            if (imageUrl.startsWith("gs://")) {
                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReferenceFromUrl(imageUrl);
                                storageReference.getDownloadUrl().addOnCompleteListener(
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String downloadUrl = task.getResult().toString();
                                                    Glide.with(holder.messageImageView.getContext())
                                                            .load(downloadUrl)
                                                            .into(holder.messageImageView);
                                                } else {
                                                    Log.w(TAG, "Getting download url was not successful.", task.getException());
                                                }
                                            }
                                        }
                                );
                            } else {
                                Glide.with(holder.messageImageView.getContext())
                                        .load(model.getImageUrl())
                                        .into(holder.messageImageView);
                            }
                            holder.messageImageView.setVisibility(ImageView.VISIBLE);
                            holder.messageTextView.setVisibility(TextView.GONE);
                        }

                        holder.messengerTextView.setText(model.getFullName());
                        if (model.getPhotoUrl() == null || model.getPhotoUrl().equals("")) {
                        //if (mPhotoUrl == null) {
                            holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(ChatActivity.this)
                                    .load(model.getPhotoUrl())
                                    .into(holder.messengerImageView);
                        }

                        if (position == mFirebaseAdapter.getItemCount() - 1)
                        {
                            if (model.getChatSeen().equals("false")) {
                                holder.messengerTextSeen.setText("Delivered");
                            } else if (model.getChatSeen().equals("true")) {
                                holder.messengerTextSeen.setText("Seen");
                            }
                        }
//                        else {
//                            holder.messengerTextSeen.setVisibility(View.GONE);
//                        }
                    }
                    break;
                    default:
                        break;
                }

            }


            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if (viewType == VIEW_TYPE_MESSAGE_SENT) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_sent, parent, false);
                    return new MessageViewHolder(view, viewType == 1);

                } else {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message, parent, false);

                    return new MessageViewHolder(view, viewType == 0);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send messages on click.

                Chat chat = new Chat(chatMessageId, mUsername, mMessageEditText.getText().toString(),
                        mPhoneNumber,
                        mPhotoUrl,
                        null /* no image */,
                        timestamp,
                        chatSeen);
                mFirebaseDatabaseReference.child(CHAT_CHILD)
                        .push().setValue(chat);
                mMessageEditText.setText("");
            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        seenMessage(chatMessageId, mPhoneNumber);

    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
        reference.removeEventListener(seenListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    Chat tempMessage = new Chat(chatMessageId, mUsername, null, mPhoneNumber,
                            mPhotoUrl,
                            LOADING_IMAGE_URL,
                            timestamp,
                            chatSeen);
                    mFirebaseDatabaseReference.child(CHAT_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mPhoneNumber)
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(ChatActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Chat friendlyMessage =
                                                                new Chat(chatMessageId, mUsername, null, mPhoneNumber,
                                                                        mPhotoUrl,
                                                                        task.getResult().toString(),
                                                                        timestamp,
                                                                        chatSeen);
                                                        mFirebaseDatabaseReference.child(CHAT_CHILD).child(key)
                                                                .setValue(friendlyMessage);
                                                    }
                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    // for export chat history
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    // for seenMessage
    private void seenMessage(final String chatMsgId, final String senderPhoneNumber) {
        reference = FirebaseDatabase.getInstance().getReference("chat");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getChatMessageId().equals(chatMsgId) && !chat.getPhoneNumber().equals(senderPhoneNumber)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("chatSeen", "true");
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countUnreadMessage(final String chatMessageId) {
        reference = FirebaseDatabase.getInstance().getReference(CHAT_CHILD);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getChatMessageId().equals(chatMessageId) && chat.getChatSeen().equals("false")) {
                        unread++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
