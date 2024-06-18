package com.example.apptodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.apptodo.databinding.ActivityMainScreenBinding;

import java.util.ArrayList;

public class main_screen extends AppCompatActivity {

    ActivityMainScreenBinding binding;
    todoAdapter todoAdapter;
    ArrayList<todoData> dataArrayList = new ArrayList<>();
    sharedPreferences sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = getIntent().getStringExtra("Email");
        String registerUsername = getIntent().getStringExtra("Username");



        binding.textView5.setText("ADD TO-DO");

        sharedPrefManager = new sharedPreferences(this);

        // Load saved to-do items
        ArrayList<String> savedItems = sharedPrefManager.getTodoList();
        for (String item : savedItems) {
            todoData todoData = new todoData(item, R.drawable.edit, R.drawable.delete);
            dataArrayList.add(todoData);
        }

        todoAdapter = new todoAdapter(this, dataArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(todoAdapter);

        binding.imgAdd.setOnClickListener(v -> showAddItemDialog());

        binding.imgDevinfo2.setOnClickListener(v -> {
            Intent intent = new Intent(main_screen.this, DevInfo.class);
            startActivity(intent);
        });

        binding.imgUserinfo.setOnClickListener(v -> {
            Intent intent = new Intent(main_screen.this, UserInfo.class);
            intent.putExtra("Email", email);
            intent.putExtra("Username", registerUsername);
            startActivity(intent);
            finish();
        });
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        final View customLayout = getLayoutInflater().inflate(R.layout.add_item_dialog, null);
        builder.setView(customLayout);

        // Remove the default title and buttons
        builder.setTitle("Add an item");
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnOk = customLayout.findViewById(R.id.btn_ok);
        Button btnCancel = customLayout.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(v -> {
            EditText editTextItem = customLayout.findViewById(R.id.edit_text_item);
            String item = editTextItem.getText().toString();

            if (!item.isEmpty()) {
                ArrayList<String> currentList = sharedPrefManager.getTodoList();
                currentList.add(item);
                sharedPrefManager.saveTodoList(currentList);

                todoData newTodoData = new todoData(item, R.drawable.edit, R.drawable.delete);
                dataArrayList.add(newTodoData);
                todoAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Item added: " + item, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Item cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.cancel());


    }

}
