package com.example.tasker;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.example.tasker.Model.ToDoModel;
import com.example.tasker.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private EditText newTaskStartDateTime;
    private EditText newTaskEndDateTime;
    private Button newTaskSaveButton;

    private DatabaseHandler db ;

    boolean checkForText = false;
    boolean checkForStartDateTime = false;
    boolean checkForEndDateTime = false;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.TYPE_INPUT_METHOD_DIALOG);
        db= new  DatabaseHandler(this.getActivity());
        newTaskStartDateTime = view.findViewById(R.id.in_startDateTime);
        newTaskEndDateTime = view.findViewById(R.id.in_endDateTime);

        newTaskStartDateTime.setInputType(InputType.TYPE_NULL);
        newTaskEndDateTime.setInputType(InputType.TYPE_NULL);

        newTaskStartDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {

                }else{
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }

            }
        });

        newTaskEndDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {

                }else{
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }

            }
        });

        newTaskStartDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(newTaskStartDateTime);
            }
        });

        newTaskEndDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(newTaskEndDateTime);
            }
        });

        return view;
    }

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        newTaskStartDateTime = Objects.requireNonNull(getView()).findViewById(R.id.in_startDateTime);
        newTaskEndDateTime = Objects.requireNonNull(getView()).findViewById(R.id.in_endDateTime);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            String startDateTime = bundle.getString("startDateTime");
            String endDateTime = bundle.getString("endDateTime");
            newTaskText.setText(task);
            newTaskStartDateTime.setText(startDateTime);
            newTaskEndDateTime.setText(endDateTime);
            assert task != null;
            assert startDateTime != null;
            assert endDateTime != null;

            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(startDateTime, dateTimeFormat);
            LocalDateTime end = LocalDateTime.parse(endDateTime, dateTimeFormat);


            if (task.length() > 0 && startDateTime.length() > 0 && endDateTime.length() > 0) {
                newTaskSaveButton.setEnabled(true);
                newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white));
            }

        }

        newTaskText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                return false;
            }
        });

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals(""))
                {
                    checkForText = false;
                }
                else
                {
                    checkForText = true;
                }

                if(checkForText==true){
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white));
                }
                else{
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newTaskStartDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals(""))
                {
                    checkForStartDateTime = false;
                }
                else
                {
                    checkForStartDateTime = true;
                }

                if(checkForStartDateTime==true){
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white));
                }
                else{
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newTaskEndDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals(""))
                {
                    checkForEndDateTime = false;
                }
                else
                {
                    checkForEndDateTime = true;
                }

                if(checkForEndDateTime==true){
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.white));
                }
                else{
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String startDateTime = newTaskStartDateTime.getText().toString();
                String endDateTime = newTaskEndDateTime.getText().toString();

                /*if(db!=null)
                {
                    Log.d("TESSSSSSSSSSSS","not nul");
                }
                else
                {
                    Log.d("yESSSSSSSSSSSS","not");
                }*/
                if(finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text, startDateTime, endDateTime);
                }
                else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStartDateTime(startDateTime);
                    task.setEndDateTime(endDateTime);
                    task.setStatus(0);
                    /*System.out.print("------------------");
                    System.out.print(task.getTask());
                    System.out.print(task.getStartDateTime());
                    System.out.print(task.getEndDateTime());
                    System.out.print(task.getStatus());
                    System.out.print("------------------");*/
                    db.insertTask(task);
                }
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }



}
