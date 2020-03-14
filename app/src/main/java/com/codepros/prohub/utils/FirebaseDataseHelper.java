package com.codepros.prohub.utils;

import androidx.annotation.NonNull;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Property;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataseHelper {
    private FirebaseDatabase myDatabase;
    private DatabaseReference myUserRef;
    private DatabaseReference myPropRef;
    private DatabaseReference myUnitRef;
    private DatabaseReference myNewsRef;
    private DatabaseReference myChatMessageRef;

    // list of data
    private List<User> users = new ArrayList<>();
    private List<Property> properties = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();

    // interface to load User database
    public interface UserDataStatus{
        void DataIsLoad(List<User> users, List<String> keys);
    }

    // interface to load Property database
    public interface PropDataStatus{
        void DataIsLoad(List<Property> properties, List<String> keys);
    }

    // interface to load Unit database
    public interface UnitDataStatus{
        void DataIsLoad(List<Unit> units, List<String> keys);
    }

    // interface to load ChatMessage database
    public interface ChatMessageDataStatus{
        void DataIsLoad(List<ChatMessage> chatMessages, List<String> keys);
    }

    // interface to load News database
    public interface NewsDataStatus{
        void DataIsLoad(List<News> newsList, List<String> keys);
    }

    public FirebaseDataseHelper(){
        myDatabase = FirebaseDatabase.getInstance();
        myUserRef = myDatabase.getReference("users");
        myPropRef = myDatabase.getReference("properties");
        myUnitRef = myDatabase.getReference("units");
        myNewsRef = myDatabase.getReference("news");
        myChatMessageRef = myDatabase.getReference("chatMessages");
    }

    public void readUsers(final UserDataStatus dataStatus){
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }
                dataStatus.DataIsLoad(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readProperty(final PropDataStatus dataStatus)
    {
        myPropRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                properties.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Property property = keyNode.getValue(Property.class);
                    properties.add(property);
                }
                dataStatus.DataIsLoad(properties, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readUnits(final UnitDataStatus unitDataStatus){
        myUnitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                units.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Unit unit = keyNode.getValue(Unit.class);
                    units.add(unit);
                }
                unitDataStatus.DataIsLoad(units, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readNews(final NewsDataStatus newsDataStatus){
        myNewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    News news = keyNode.getValue(News.class);
                    newsList.add(news);
                }
                newsDataStatus.DataIsLoad(newsList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readChatMessages(final ChatMessageDataStatus chatMessageDataStatus){
        myChatMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    ChatMessage chatMessage = keyNode.getValue(ChatMessage.class);
                    chatMessages.add(chatMessage);
                }
                chatMessageDataStatus.DataIsLoad(chatMessages, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
