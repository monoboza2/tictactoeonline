package th.ac.kmutnb.tictactoe2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import th.ac.kmutnb.tictactoe2.Fragments.GameFragment;

public class MultiplayerActivity extends AppCompatActivity {
    private static final String TAG = "my_app";
    public static DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tictactoe-6a7e1-default-rtdb.firebaseio.com/");
    private static final String Shared_Name="mypref";
    private static final String KEY_NAME="name";

    public static String PlayeruniqueID="0";
    public static String OpponentuniqueID="0";
    public static boolean opponentfound=false;
    private String status="matching";
    public static String Playerturn = "";
    public static String ConnectionID="";
    public static String PlayerNameO = "waiting";
    public static String PlayerNameX = "waiting";
    public static String PlayerName = "";

    //Winning
    public static final List<int[]> combinationList = new ArrayList<>();
    public static final List<String> doneBoxes = new ArrayList<>();

    public static ValueEventListener turnEventListener,wonEventListener, connectionEventListener ,scoreEventListener;

    //selected boxes by players
    private final String[] boxSelectedBy = {"","","","","","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        SharedPreferences sharedPreferences=getSharedPreferences(Shared_Name,MODE_PRIVATE);
        String name =sharedPreferences.getString(KEY_NAME,null);

//        PlayeruniqueID=String.valueOf(System.currentTimeMillis());

//        databaseReference.child("conections").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(opponentfound){
//
//                    if(snapshot.hasChildren()){
//
//                        for(DataSnapshot conections:snapshot.getChildren()){
//                            long conId=Long.parseLong(conections.getKey());
//                            int getplayerCount= (int)conections.getChildrenCount();
//
//                            if(status.equals("waiting")){
//                                if(getplayerCount==2){
//                                    Playerturn=PlayeruniqueID;
//                                    startActivity(new Intent(MultiplayerActivity.this,Test.class));
////                                    applyPlayerturn( Playerturn);
//
//                                }
//                            }
//                        }
//                    }
//                }
//                else{
//                    String connectionUniqueID=String.valueOf(System.currentTimeMillis());
//
//                    snapshot.child(connectionUniqueID).child(PlayeruniqueID).child("player_name").getRef().setValue(name);
//
//                    status="waiting";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void Online(View view){
        ProgressDialog progressDialog=new ProgressDialog(this);

        progressDialog.setCancelable(true);
        progressDialog.setMessage("Waiting for Opponent");
        progressDialog.show();

        PlayeruniqueID=String.valueOf(System.currentTimeMillis());
        SharedPreferences sharedPreferences=getSharedPreferences(Shared_Name,MODE_PRIVATE);
        String name =sharedPreferences.getString(KEY_NAME,null);
        connectionEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Online","hello "+opponentfound);
                if(!opponentfound){
                    Log.i("Online","hellowwww");
                    //if opponent found or not ?:if not then look for the opponent
                    if(snapshot.hasChildren()){

                        //check all connection
                        for(DataSnapshot connections : snapshot.getChildren()){

                            //get connection id
                            String conId = connections.getKey();

                            //2 players or 1 player ?
                            int getPlayerCount = (int)connections.getChildrenCount();

                            //after created new connection and waiting
                            if(status.equals("waiting")){
                                if(getPlayerCount == 2){

                                    //true when found
                                    boolean playerFound = false ;

                                    //getting players connection
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getPlayerUniqueId = players.getKey();

                                        //if player id match with user created connection
                                        if(getPlayerUniqueId.equals(PlayeruniqueID)){
                                            playerFound = true;
                                        }
                                        else if(playerFound){
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            OpponentuniqueID = players.getKey();

                                            Playerturn = PlayeruniqueID;

                                            //set playername to TextView
//                                            player2TV.setText(getOpponentPlayerName);

                                            //assigning connection id
                                            ConnectionID = conId;
                                            opponentfound = true;

                                            //adding turn and won listener to database
//                                            databaseReference.child("turns").child(ConnectionID).addValueEventListener(turnEventListener);
//                                            databaseReference.child("won").child(ConnectionID).addValueEventListener(wonEventListener);

                                            //hide progress
                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }

                                            onMatchPlay(snapshot);


                                            //remove connection database
                                            databaseReference.child("connections").removeEventListener(this);
                                        }
                                    }
                                }
                            }
                            // case user not created connection
                            else {
                                //if connection has 1 player
                                if(getPlayerCount == 1){
                                    //add player to connection
                                    connections.child(PlayeruniqueID).child("player_name").getRef().setValue(name);
                                    connections.child(PlayeruniqueID).child("status").getRef().setValue("connected");


                                    //getting both players
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        OpponentuniqueID = players.getKey();

                                        //first turn will be to who created connection
                                        Playerturn = OpponentuniqueID;
//                                        applyPlayerturn(Playerturn);

                                        //set playername to TextView
//                                            player2TV.setText(getOpponentPlayerName);

                                        //assign connection id
                                        ConnectionID = conId;
                                        opponentfound = true;

                                        //adding turn and won listener to database
//                                        databaseReference.child("turns").child(ConnectionID).addValueEventListener(turnEventListener);
//                                        databaseReference.child("won").child(ConnectionID).addValueEventListener(wonEventListener);

                                        //hide progress
                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                onMatchPlay(snapshot);
                                                databaseReference.child("connections").removeEventListener(this);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        //remove connection database
                                        databaseReference.child("connections").removeEventListener(this);

                                        break;
                                    }
                                }
                            }
                        }
                        // if opponent is not found and user is not waiting then create new connection
                        if(!opponentfound && !status.equals("waiting")){
                            //generate id connection
                            String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                            //add first player to connection and waiting
                            snapshot.child(connectionUniqueId).child(PlayeruniqueID).child("player_name").getRef().setValue(name);
                            snapshot.child(connectionUniqueId).child(PlayeruniqueID).child("status").getRef().setValue("connected");

                            status="waiting";
                        }
                    }
                    //if no connection on database : create new connection
                    else{
                        //generate id connection
                        String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                        //add first player to connection and waiting
                        snapshot.child(connectionUniqueId).child(PlayeruniqueID).child("player_name").getRef().setValue(name);

                        status="waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child("connections").addValueEventListener(connectionEventListener);
    }
    private void applyPlayerturn(String PlayeruniqueID2){
        TextView textViewTurn = findViewById(R.id.textTurn);
        if(PlayeruniqueID2.equals(PlayeruniqueID)){
            textViewTurn.setText("Turn A");
        }
        else{
            textViewTurn.setText("Turn B");
        }
    }

    private void selectBox(Bitmap imageCell, int selectedBoxPosition , String selectedByPlayer ){
        boxSelectedBy[selectedBoxPosition - 1] = selectedByPlayer;

        if(selectedByPlayer.equals(PlayeruniqueID)){
            Playerturn = OpponentuniqueID;
        }
        else{
            Playerturn = PlayeruniqueID;
        }

        applyPlayerturn(Playerturn);

        //checking player has won?
        if(checkPlayerWin(selectedByPlayer)){

            //sending won player id to database
            databaseReference.child("won").child(ConnectionID).child("player_id").setValue(selectedByPlayer);
        }

        if(doneBoxes.size()==9){
            final WinDialog winDialog = new WinDialog(MultiplayerActivity.this,"Draw!");
            winDialog.show();
        }
    }
    private boolean checkPlayerWin(String playerId){
        boolean isPlayerWon = false ;

        //compare player turn every combination
        for(int i=0; i< combinationList.size();i++){
            final int[] comination = combinationList.get(i);

            //check last three turn of user
            if(
                    boxSelectedBy[comination[0]].equals(playerId) &&
                    boxSelectedBy[comination[1]].equals(playerId) &&
                    boxSelectedBy[comination[2]].equals(playerId)
            ){
                isPlayerWon = true ;
            }
        }
        return isPlayerWon;
    }
    private  void  onMatchPlay(DataSnapshot snapshot) {
//        if( ConnectionID != null && PlayeruniqueID != null && OpponentuniqueID != null && Playerturn != null){
           try {
               PlayerName = snapshot.child(ConnectionID).child(PlayeruniqueID).child("player_name").getValue(String.class);
               PlayerNameO = snapshot.child(ConnectionID).child(Playerturn).child("player_name").getValue(String.class);
               if( Playerturn.equals(PlayeruniqueID) ){
                   PlayerNameX = snapshot.child(ConnectionID).child(OpponentuniqueID).child("player_name").getValue(String.class);
               }
               else{
                   PlayerNameX = snapshot.child(ConnectionID).child(PlayeruniqueID).child("player_name").getValue(String.class);
               }
               Playerturn = PlayerNameO ;
           }
           catch (Error error){}

//        }
        Intent Player = new Intent(MultiplayerActivity.this,GameActivity.class);
        Player.putExtra("gameMode",2);
        startActivity(Player);
    }

}