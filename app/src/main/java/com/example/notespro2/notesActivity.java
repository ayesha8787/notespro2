package com.example.notespro2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class notesActivity extends AppCompatActivity {
     FloatingActionButton mcreatenotefab;
    FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mcreatenotefab = findViewById(R.id.createnotefab);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("All Notes");
        }


         mcreatenotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(notesActivity.this,Createnote.class));

            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title", Query.Direction.ASCENDING);
       FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();
    noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
        @Override
        protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {
            ImageView popupbutton = noteViewHolder.itemView.findViewById(R.id.menupopupbutton);
            int colourcode = getRandomColor();
            noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));


      noteViewHolder.notetitle.setText(firebasemodel.getTitle());
      noteViewHolder.notecontent.setText(firebasemodel.getContent());


      String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

       noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // we have to open note detail activity
               Intent intent = new Intent(view.getContext(),notedetails.class);


               intent.putExtra("title",firebasemodel.getTitle());
               intent.putExtra("content",firebasemodel.getContent());
               intent.putExtra("noteId",docId);


               view.getContext().startActivity(intent);

             //  Toast.makeText(getApplicationContext(), "This is clicked", Toast.LENGTH_SHORT).show();
           }
       });

       popupbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
               popupMenu.setGravity(Gravity.END);
               popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
              Intent intent = new Intent(view.getContext(),editnoteactivity.class);


                       intent.putExtra("title",firebasemodel.getTitle());
                       intent.putExtra("content",firebasemodel.getContent());
                       intent.putExtra("noteId",docId);

              view.getContext().startActivity(intent);
                       return false;
                   }
               });

               popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    //   Toast.makeText(view.getContext(), "This note is deleted", Toast.LENGTH_SHORT).show();
                       DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").document(docId);
                       documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(view.getContext(), "This note is deleted", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(view.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                           }
                       });


                       return false;
                   }
               });
               popupMenu.show();
           }
       });

        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
          return new NoteViewHolder(view);
        }
    };

     mrecyclerview = findViewById(R.id.recyclerview);
     mrecyclerview.setHasFixedSize(true);
      staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,staggeredGridLayoutManager.VERTICAL);
      mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
      mrecyclerview.setAdapter(noteAdapter);



    }

    public class NoteViewHolder extends  RecyclerView.ViewHolder
    {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(notesActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

        protected void onStart() {

            super.onStart();
            noteAdapter.startListening();
        }
        protected  void onStop() {

            super.onStop();
            noteAdapter.stopListening();
        }
 private int getRandomColor(){
     List<Integer> colourecode = new ArrayList<>();
     colourecode.add(R.color.color1);
     colourecode.add(R.color.gray);
     colourecode.add(R.color.green);
     colourecode.add(R.color.lightgreen);
     colourecode.add(R.color.pink);
     colourecode.add(R.color.skyblue);
     colourecode.add(R.color.teal_200);
     colourecode.add(R.color.teal_700);
     colourecode.add(R.color.purple_200);

     Random random = new Random();
     int number = random.nextInt(colourecode.size());
     return colourecode.get(number);

 }

}
