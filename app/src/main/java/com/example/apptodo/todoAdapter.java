package com.example.apptodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class todoAdapter extends RecyclerView.Adapter<todoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<todoData> dataArrayList;
    private sharedPreferences sharedPrefManager;

    public todoAdapter(Context context, ArrayList<todoData> dataArrayList) {
        this.context = context;
        this.dataArrayList = dataArrayList;
        this.sharedPrefManager = new sharedPreferences(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        todoData todoData = dataArrayList.get(position);
        holder.todoItem.setText(todoData.todoItem);
        holder.edit.setImageResource(todoData.edit);
        holder.delete.setImageResource(todoData.delete);

        holder.edit.setOnClickListener(v -> showEditItemDialog(position, todoData));
        holder.delete.setOnClickListener(v -> showDeleteItemDialog(position, todoData));
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    private void showEditItemDialog(int position, todoData todoData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle("Edit " + todoData.todoItem);

        // Inflate the custom layout/view
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.add_item_dialog, null);
        builder.setView(customLayout);

        // Set the current text to the EditText
        EditText editTextItem = customLayout.findViewById(R.id.edit_text_item);
        editTextItem.setText(todoData.todoItem);

        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newItem = editTextItem.getText().toString();

            if (!newItem.isEmpty()) {
                // Update the item in SharedPreferences
                ArrayList<String> currentList = sharedPrefManager.getTodoList();
                currentList.set(currentList.indexOf(todoData.todoItem), newItem);
                todoData.todoItem = newItem;
                dataArrayList.set(position, todoData);
                sharedPrefManager.saveTodoList(currentList);

                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                Toast.makeText(context.getApplicationContext(), "Item updated: " + newItem, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), "Item cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.dialogTitleColor));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.dialogTitleColor));
    }

    private void showDeleteItemDialog(int position, todoData todoData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle("Delete " + todoData.todoItem);
        builder.setMessage("Really want to delete " + todoData.todoItem + " ?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            dataArrayList.remove(position);

            // Remove the item from SharedPreferences
            ArrayList<String> currentList = sharedPrefManager.getTodoList();
            currentList.remove(todoData.todoItem);
            sharedPrefManager.saveTodoList(currentList);

            // Notify the adapter that the data has changed
            notifyDataSetChanged();

            Toast.makeText(context.getApplicationContext(), todoData.todoItem + " deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.dialogTitleColor));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.dialogTitleColor));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView todoItem;
        ImageView edit;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoItem = itemView.findViewById(R.id.txt_todoname);
            edit = itemView.findViewById(R.id.img_edit);
            delete = itemView.findViewById(R.id.img_delete);
        }
    }
}
