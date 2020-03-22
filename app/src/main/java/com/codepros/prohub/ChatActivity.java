package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepros.prohub.model.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;
        LinearLayout layoutSender;
        LinearLayout layoutReceiver;

        public MessageViewHolder(View v, boolean isSender) {
            super(v);

            if (isSender) {
                //layoutSender = (LinearLayout) itemView.findViewById(R.id.layoutSender);
                messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
                messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
                messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
                messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            } else {
                //layoutReceiver = (LinearLayout) itemView.findViewById(R.id.layoutReceiver);
                messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
                messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
                messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
                messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            }
        }
    }

    private static final String TAG = "ChatActivity";
    public static final String CHAT_CHILD = "chat";
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mSenderName;
    private String mReceiverName;
    private String mPhoneNumber;
    private String mPhotoUrl;
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

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSharedPreferences = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mUsername = mSharedPreferences.getString("username", ANONYMOUS);
        mPhoneNumber = mSharedPreferences.getString("phoneNum","0123456789");
        chatMessageId = getIntent().getStringExtra("Chat_ID");
        timestamp = "2020-03-08 12:11 AM";
        //mFirebaseUser = mUsername;

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                    //mUsername = chat.getFullName();
                }
                return chat;
            }
        };

//        DatabaseReference chatRef = mFirebaseDatabaseReference.child(CHAT_CHILD).equalTo(chatMessageId);
        Query chatRef = mFirebaseDatabaseReference.child(CHAT_CHILD).orderByChild("chatMessageId").equalTo(chatMessageId);;

        final FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                    .setQuery(chatRef, parser)
                    .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, MessageViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {

                Chat chat = (Chat) mFirebaseAdapter.getItem(position);

                //if (currentUser == mFirebaseUser.getDisplayName())
                if (chat.getFullName().equals(mUsername)){
                    // If the current user is the sender of the message
                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    // If some other user sent the message
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull Chat model) {

                switch (holder.getItemViewType())
                {
                    case 0:
                    {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (model.getMessage() != null) {
                            holder.messageTextView.setText(model.getMessage());
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
                        if (model.getPhotoUrl() == null) {
                            holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(ChatActivity.this)
                                    .load(model.getPhotoUrl())
                                    .into(holder.messengerImageView);
                        }
                    }
                    break;
                    case 1:
                    {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (model.getMessage() != null) {
                            holder.messageTextView.setText(model.getMessage());
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
                        timestamp);
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
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
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
                            timestamp);
                    mFirebaseDatabaseReference.child(CHAT_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        //.getReference(mFirebaseUser.getUid())
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
                                                                        timestamp);
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
}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
//
//        if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    final Uri uri = data.getData();
//                    Log.d(TAG, "Uri: " + uri.toString());
//
//                    Chat tempMessage = new Chat(mPhoneNumber, null, mUsername, mPhotoUrl,
//                            LOADING_IMAGE_URL);
//                    mFirebaseDatabaseReference.child(CHAT_CHILD).push()
//                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                    if (databaseError == null) {
//                                        String key = databaseReference.getKey();
//                                        StorageReference storageReference = FirebaseStorage.getInstance()
//                                                .getReference()
//                                                .child(key)
//                                                .child(uri.getLastPathSegment());
//
//                                        putImageInStorage(storageReference, uri, key);
//                                    } else {
//                                        Log.w(TAG, "Unable to write message to database.",
//                                        databaseError.toException());
//                                    }
//                                }
//                            });
//                }
//            }
//        }
//    }
//
//    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
//        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
//                new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            task.getResult().getMetadata().getReference().getDownloadUrl()
//                                    .addOnCompleteListener(ChatActivity.this,
//                                            new OnCompleteListener<Uri>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Uri> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Chat chat = new Chat(mPhoneNumber, null,
//                                                                mUsername, mPhotoUrl,
//                                                                task.getResult().toString());
//                                                        mFirebaseDatabaseReference.child(CHAT_CHILD).child(key)
//                                                                .setValue(chat);
//                                                    }
//                                                }
//                                            });
//                        } else {
//                            Log.w(TAG, "Image upload task was not successful.",
//                                    task.getException());
//                        }
//                    }
//                });
//    }
//}
