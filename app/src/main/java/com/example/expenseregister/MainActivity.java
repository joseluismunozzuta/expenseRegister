package com.example.expenseregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private AtomicReference<String> selectedItem = new AtomicReference<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.my_spinner);
        ArrayList<String> categoriesList = new ArrayList<>();

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                categoriesList.add(document.getData().get("name").toString());
                                Log.d("infoFirebase", "Here");
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        } else {
                            Log.d("infoFirebase", "Error getting documents: ", task.getException());
                        }
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("infoFirebase", "Spinner clicked");
                selectedItem.set(adapterView.getItemAtPosition(i).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    public void registrarGasto(View view){
        Button saveButton = findViewById(R.id.button);
        saveButton.setEnabled(false);
        Map<String, Object> data = new HashMap<>();
        TextView amountText = findViewById(R.id.amountExpense);
        TextView descText = findViewById(R.id.description);
        String category = selectedItem.get();
        Double amount = Double.parseDouble(amountText.getText().toString());
        String desc = descText.getText().toString();
        data.put("amount", amount);
        data.put("description", desc);
        data.put("category", category);
        data.put("timestamp", new Timestamp(new Date()));
        db.collection("expenses")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("infoFirebase", "DocumentSnapshot written with ID: " + documentReference.getId());
                        saveButton.setEnabled(true);
                        Toast.makeText(MainActivity.this,"Expense saved", Toast.LENGTH_LONG).show();
                        amountText.setText("");
                        descText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("infoFirebase", "Error adding document", e);
                        Toast.makeText(MainActivity.this,"Error saving document, try again later.", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(true);
                    }
                });
    }

}