package com.uc3m.InmunoApp.PatientDetail;

import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;

import android.util.Log;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import java.nio.file.Files;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Calendar;
import java.util.Set;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class patientDetailActivity extends AppCompatActivity {
    TextView immunotherapyTextView;
    TextView pastImmunotherapyTextView;
    String newTreatment;
    EditText temperatureEditText;
    EditText heartRateMax;
    EditText heartRateMin;
    EditText respRateMax;
    EditText respRateMin;
    EditText symptomsEditText;
    TextView adverseEventsTextView;
    EditText deleteSymptomEditText;
    private LineChart chart;
    private TextView symptomsTextView;
    private String startDate;
    private String endDate;
    private int selectedMeasure;
    private final Set<String> sentNotifications = new HashSet<>();
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    String name, gender;
    int age, weight, height;
    private RecyclerView recyclerViewTox;
    String  patName, patGender, actualImmunotherapy, docName, docEmail;
    Integer patAge, patHeight, patWeight, docPhone;
    ArrayList<String> patTreatments = new ArrayList<>();
    ArrayList<String> patToxicities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        // Inicializar vistas
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = findViewById(R.id.back_button);
        ImageView launchButton = findViewById(R.id.chat_button);

        TextView nameTextView = findViewById(R.id.detailName);
        TextView genderTextView = findViewById(R.id.detailGender);
        TextView ageTextView = findViewById(R.id.detailAge);
        TextView weightTextView = findViewById(R.id.detailWeight);
        TextView heightTextView = findViewById(R.id.detailHeight);

        immunotherapyTextView = findViewById(R.id.immunotherapyFollowing);
        pastImmunotherapyTextView = findViewById(R.id.immunotherapyFollowed);
        Spinner immunoTherapySpinner = findViewById(R.id.immunotherapySpinner);
        Button addTreatmentButton = findViewById(R.id.immunotherapyButton);

        temperatureEditText = findViewById(R.id.temperatureEditText);
        respRateMin = findViewById(R.id.respFreqMin);
        respRateMax = findViewById(R.id.respFreqMax);
        heartRateMin = findViewById(R.id.cardFreqMin);
        heartRateMax = findViewById(R.id.cardFreqMax);
        Button confirmThresholdtButton = findViewById(R.id.thresholdButton);

        Button addSymptomButton = findViewById(R.id.symptomsButton);
        Button deleteSymptomButton = findViewById(R.id.deleteSymptomsButton);
        symptomsEditText = findViewById(R.id.symptomsEditText);
        adverseEventsTextView = findViewById(R.id.symptomsList);
        deleteSymptomEditText = findViewById(R.id.deleteSymptomsEditText);

        chart = findViewById(R.id.chart);
        Spinner spinner = findViewById(R.id.spinner);
        Button buttonStartDate = findViewById(R.id.start_date);
        Button buttonEndDate = findViewById(R.id.end_date);
        symptomsTextView = findViewById(R.id.symptomsTextView);

        Button buttonPDF = findViewById(R.id.generatePDF);

        recyclerViewTox = findViewById(R.id.toxDataRecyclerView);

        // Establecer recursos

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();                                    // Recibir los datos del paciente del Intent anterior
        name = intent.getStringExtra("name");
        gender = intent.getStringExtra("gender");
        age = intent.getIntExtra("age", 0);
        weight = intent.getIntExtra("weight", 0);
        height = intent.getIntExtra("height", 0);
                                                                        // Mostrar en el cardView de información general
        nameTextView.setText(name);
        genderTextView.setText(gender);
        ageTextView.setText(String.valueOf(age));
        weightTextView.setText(getString(R.string.setWeightView, weight));
        heightTextView.setText(getString(R.string.setHeightView, height));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  // Spinner de tratamientos
                R.array.immunotherapyTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        immunoTherapySpinner.setAdapter(adapter);

        setupCardView(R.id.registerCard, R.id.expandRegister);                              // CardViews
        setupCardView(R.id.treatmentCard, R.id.expandTreatment);
        setupCardView(R.id.graphicsCard, R.id.expandGraphics);

        setupLinearLayout(R.id.immunotherapyLayout, R.id.expandImmunotherapy);              // LinearLayouts
        setupLinearLayout(R.id.thresholdLayout, R.id.expandThreshold);
        setupLinearLayout(R.id.symptomsLayout, R.id.expandSymptoms);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.measuresTypes, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        // Listeners

        backButton.setOnClickListener(v -> onBackPressed());        // Botón de atrás

        // Botón de chat
        launchButton.setOnClickListener(v -> {
            Intent chat = new Intent(patientDetailActivity.this, ChatActivity.class);
            String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
            if(email != null && email.equals("100429327@alumnos.uc3m.es")){
                chat.putExtra("currentUserEmail", "100429327@alumnos.uc3m.es");
                chat.putExtra("otherUserEmail", name);
            }else{
                chat.putExtra("currentUserEmail", "vladrak11@gmail.com");
                chat.putExtra("otherUserEmail", name);
            }
            chat.putExtra("chatId", "chatId1");
            startActivity(chat);
        });

        // Encontrar el valor del spinner anterior
        immunoTherapySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el valor seleccionado
                newTreatment = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        addTreatmentButton.setOnClickListener(v -> addTreatmentsP1(newTreatment));    // Añadir tratamiento

        confirmThresholdtButton.setOnClickListener(v -> confirmThresholdsP1());      // Confirmar valores umbral

        addSymptomButton.setOnClickListener(v -> addSymptomsP1());                    // Añadir síntoma

        deleteSymptomButton.setOnClickListener(v -> {                               // Borrar síntoma leído en el editText
            String searchText = deleteSymptomEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(searchText)) {
                searchAndDelete(searchText);
            } else {
                Toast.makeText(getApplicationContext(), R.string.enterSymptom, Toast.LENGTH_SHORT).show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeasure = position;

                if(startDate != null && endDate != null){

                    printSymptoms(startDate,endDate);

                    if(selectedMeasure == 0) {
                        plotBloodPressure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Blood pressure"),startDate, endDate, getString(R.string.bloodPessure));
                    }else if(selectedMeasure == 1) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Breath rate"), startDate, endDate, getString(R.string.respFreq));
                    }else if(selectedMeasure == 2) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Heart rate"), startDate, endDate, getString(R.string.cardFreq));
                    }else if(selectedMeasure == 3) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Oxygen saturation"), startDate, endDate, getString(R.string.satOxygen));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonStartDate.setOnClickListener(v -> showDatePickerDialog((date) -> {
            startDate = date;

            if (startDate != null) {
                buttonStartDate.setText(startDate);
                if(endDate != null){

                    printSymptoms(startDate,endDate);

                    if(selectedMeasure == 0) {
                        plotBloodPressure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Blood pressure"),startDate, endDate, getString(R.string.bloodPessure));
                    }else if(selectedMeasure == 1) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Breath rate"), startDate, endDate, getString(R.string.respFreq));
                    }else if(selectedMeasure == 2) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Heart rate"), startDate, endDate, getString(R.string.cardFreq));
                    }else if(selectedMeasure == 3) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Oxygen saturation"), startDate, endDate, getString(R.string.satOxygen));
                    }
                }
            }
        }));

        buttonEndDate.setOnClickListener(v -> showDatePickerDialog((date) -> {
            endDate = date;

            if (endDate != null) {
                buttonEndDate.setText(endDate);
                if(startDate != null){

                    printSymptoms(startDate,endDate);

                    if(selectedMeasure == 0) {
                        plotBloodPressure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Blood pressure"),startDate, endDate, getString(R.string.bloodPessure));
                    }else if(selectedMeasure == 1) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Breath rate"), startDate, endDate, getString(R.string.respFreq));
                    }else if(selectedMeasure == 2) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Heart rate"), startDate, endDate, getString(R.string.cardFreq));
                    }else if(selectedMeasure == 3) {
                        plotMeasure(FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Oxygen saturation"), startDate, endDate, getString(R.string.satOxygen));
                    }
                }
            }
        }));

        buttonPDF.setOnClickListener(v -> createPdf());

        // Llamada a funciones

        getTreatmentsP1();      // Leer los valores de los tratamientos del paciente 1

        getMaxTemperatureP1();  // Leer los valores de la temperatura del paciente 1

        getRespRateMP1();       // Leer los valores de las frecuencias respiratorias del paciente 1

        getHeartRateMP1();      // Leer los valores de las frecuencias cardíacas del paciente 1

        getSymptomsP1();        // Leer los síntomas del paciente 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_PERMISSIONS);
            } else {
                notificationListener(FirebaseDatabase.getInstance().getReference("patients").child("patient 1"));
            }
        } else {
            notificationListener(FirebaseDatabase.getInstance().getReference("patients").child("patient 1"));
        }

        fetchPatientData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Notificaciones
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificationListener(FirebaseDatabase.getInstance().getReference("patients").child("patient 1"));
            } else {
                Toast.makeText(this, R.string.noPermission, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Función para gestionar la dinámica de los CardViews
    private void setupCardView(int cardViewId, int buttonToggleId) {
        CardView content = findViewById(cardViewId);
        ImageButton buttonToggle = findViewById(buttonToggleId);

        buttonToggle.setOnClickListener(v -> {
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                buttonToggle.setImageResource(R.drawable.baseline_expand_less_24);
            } else {
                content.setVisibility(View.GONE);
                buttonToggle.setImageResource(R.drawable.baseline_expand_more_24);
            }
        });
    }

    // Función para gestionar la dinámica de los LinearLayout
    private void setupLinearLayout(int linearLayoutId, int buttonToggleId) {
        LinearLayout content = findViewById(linearLayoutId);
        ImageButton buttonToggle = findViewById(buttonToggleId);

        buttonToggle.setOnClickListener(v -> {
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                buttonToggle.setImageResource(R.drawable.baseline_expand_less_24);
            } else {
                content.setVisibility(View.GONE);
                buttonToggle.setImageResource(R.drawable.baseline_expand_more_24);
            }
        });
    }

    // Función para leer los tratamientos del paciente 1
    private void getTreatmentsP1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data").child("Treatment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> treatments = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String treatment = snapshot.getValue(String.class);
                    treatments.add(treatment);
                }
                // Mostramos por pantalla el tratamiento actual
                immunotherapyTextView.setText(getString(R.string.currentFollowing, treatments.get(treatments.size()-1)));

                // Mostramos los tratamientos seguidos anteriormente
                String pastTreatments = getString(R.string.hasFollowed);

                for(int i = treatments.size()-2; i >= 0; i--){

                    if(i == 0){
                        pastTreatments = pastTreatments.concat(" ").concat(treatments.get(i)).concat(".");
                    }else{
                        pastTreatments = pastTreatments.concat(" ").concat(treatments.get(i)).concat(",");
                    }
                }

                // Cambiar mensaje en caso de no tener tratamientos anteriores
                if(treatments.size() == 1){
                    pastImmunotherapyTextView.setVisibility(View.GONE);
                }else{
                    pastImmunotherapyTextView.setVisibility(View.VISIBLE);
                    pastImmunotherapyTextView.setText(pastTreatments);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar los errores de lectura de la base de datos
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    // Función para añadir tratamientos al paciente 1
    private void addTreatmentsP1(String treatment) {
        DatabaseReference newChildRef = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data").child("Treatment");
        DatabaseReference newChild = newChildRef.push();

        // Añadir el valor al nuevo hijo
        newChild.setValue(treatment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseError", "New child added successfully.");
            } else {
                Log.e("DatabaseError", "Failed to add new child.", task.getException());
            }
        });
    }

    // Leemos la temperatura máxima del paciente 1
    private void getMaxTemperatureP1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Vital sign margins").child("Temperature");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mostrar por pantalla el valor de la base de datos
                    Double maxTemperature = dataSnapshot.child("Max").getValue(Double.class);
                    temperatureEditText.setText(String.valueOf(maxTemperature));
                } else {
                    temperatureEditText.setText(" ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar los errores de lectura de la base de datos
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    // Leemos la frecuencia respiratoria máxima y mínima del paciente 1
    private void getRespRateMP1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Vital sign margins").child("Respiration rate");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mostrar por pantalla el valor de la base de datos
                    Integer minRespRate = dataSnapshot.child("Min").getValue(Integer.class);
                    Integer maxRespRate = dataSnapshot.child("Max").getValue(Integer.class);

                    respRateMin.setText(String.valueOf(minRespRate));
                    respRateMax.setText(String.valueOf(maxRespRate));
                } else {
                    respRateMin.setText(" ");
                    respRateMax.setText(" ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    // Leemos la frecuencia cardiaca máxima y mínima del paciente 1
    private void getHeartRateMP1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Vital sign margins").child("Heart rate");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mostrar por pantalla el valor de la base de datos
                    Integer minHeartRate = dataSnapshot.child("Min").getValue(Integer.class);
                    Integer maxHeartRate = dataSnapshot.child("Max").getValue(Integer.class);

                    heartRateMin.setText(String.valueOf(minHeartRate));
                    heartRateMax.setText(String.valueOf(maxHeartRate));
                } else {
                    heartRateMin.setText(" ");
                    heartRateMax.setText(" ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    // Confirmas los valores umbrales de los editText en la BBDD del paciente 1
    private void confirmThresholdsP1() {
        // Leer los valores de los EditText
        Double temperature = ParseDouble(temperatureEditText.getText().toString());
        Integer minRespRate = Integer.parseInt(respRateMin.getText().toString());
        Integer maxRespRate = Integer.parseInt(respRateMax.getText().toString());
        Integer minHeartRate = Integer.parseInt(heartRateMin.getText().toString());
        Integer maxHeartRate = Integer.parseInt(heartRateMax.getText().toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Vital sign margins");

        // Temperatura
        DatabaseReference tempField = reference.child("Temperature").child("Max");
        tempField.setValue(temperature).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Value updated successfully.");
            } else {
                Log.e("DatabaseError", "Failed to update value.", task.getException());
            }
        });

        // Frecuencia respiratoria mínima y máxima
        DatabaseReference RRmaxField = reference.child("Respiration rate").child("Max");
        DatabaseReference RRminField = reference.child("Respiration rate").child("Min");

        RRmaxField.setValue(maxRespRate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Value updated successfully.");
            } else {
                Log.e("DatabaseError", "Failed to update value.", task.getException());
            }
        });
        RRminField.setValue(minRespRate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Value updated successfully.");
            } else {
                Log.e("DatabaseError", "Failed to update value.", task.getException());
            }
        });

        // Frecuencia cardíaca mínima y máxima
        DatabaseReference HRmaxField = reference.child("Heart rate").child("Max");
        DatabaseReference HRminField = reference.child("Heart rate").child("Min");

        HRmaxField.setValue(maxHeartRate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Value updated successfully.");
            } else {
                Log.e("DatabaseError", "Failed to update value.", task.getException());
            }
        });
        HRminField.setValue(minHeartRate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Value updated successfully.");
            } else {
                Log.e("DatabaseError", "Failed to update value.", task.getException());
            }
        });
        Toast.makeText(getApplicationContext(), R.string.thresholdSuccessful, Toast.LENGTH_LONG).show();
    }

    // Función para parsear el double de temperatura
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

    // Añadir síntomas al paciente 1
    private void addSymptomsP1() {
        String symptom = symptomsEditText.getText().toString();

        if(symptom.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.emptySymptom, Toast.LENGTH_LONG).show();
        }else{
            // Definir la ruta a la ubicación donde quieres añadir el nuevo hijo
            DatabaseReference newChildRef = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data").child("Adverse events");

            // Crear un nuevo hijo con un valor
            // Utilizando push() para generar una clave única automáticamente
            DatabaseReference newChild = newChildRef.push();

            // Añadir el valor al nuevo hijo
            newChild.setValue(symptom).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DatabaseError", "New child added successfully.");
                } else {
                    Log.e("DatabaseError", "Failed to add new child.", task.getException());
                }
            });
        }
    }

    // Leer los síntomas del paciente 1
    private void getSymptomsP1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data").child("Adverse events");
        // Leemos los hijos de la rama "Treatment"
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> symptoms = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String symptom = snapshot.getValue(String.class);
                    symptoms.add(symptom);
                }
                String symptomsList = getString(R.string.listedSymptoms).concat(" ");

                for(int i = 0; i < symptoms.size(); i++){
                    if(i == symptoms.size() - 1){
                        symptomsList = symptomsList.concat(symptoms.get(i)).concat(".");
                    }else{
                        symptomsList = symptomsList.concat(symptoms.get(i)).concat(",").concat(" ");
                    }
                }
                adverseEventsTextView.setText(symptomsList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar los errores de lectura de la base de datos
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    // Buscar y borrar un síntoma dado en la BBDD del paciente 1
    private void searchAndDelete(String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data").child("Adverse events");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);

                    if (searchText.equalsIgnoreCase(value != null ? value.trim() : "")) {
                        found = true;
                        childSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.deletedSymptom, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.notDeletedSymptom, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(getApplicationContext(), R.string.notFoundSymptom, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar los errores de lectura de la base de datos
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    private void notificationListener(DatabaseReference patientRef) {
        DatabaseReference measuresRef = patientRef.child("Measures");
        DatabaseReference marginsRef = patientRef.child("Medical information").child("Vital sign margins");

        marginsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer heartRateMin = dataSnapshot.child("Heart rate").child("Min").getValue(Integer.class);
                Integer heartRateMax = dataSnapshot.child("Heart rate").child("Max").getValue(Integer.class);
                Integer respRateMin = dataSnapshot.child("Respiration rate").child("Min").getValue(Integer.class);
                Integer respRateMax = dataSnapshot.child("Respiration rate").child("Max").getValue(Integer.class);
                Double tempMax = dataSnapshot.child("Temperature").child("Max").getValue(Double.class);

                // Ahora configura el listener para las medidas de Heart rate y Respiration rate
                if (heartRateMin != null && heartRateMax != null && respRateMin != null && respRateMax != null) {
                    // Configurar el listener para las medidas de Heart rate y Respiration rate
                    addMeasureListener(measuresRef.child("Heart rate"), heartRateMin, heartRateMax, getString(R.string.cardFreq));
                    addMeasureListener(measuresRef.child("Breath rate"), respRateMin, respRateMax, getString(R.string.respFreq));
                    addTemperatureListener(measuresRef.child("Sympthoms"), tempMax);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar los errores de lectura de la base de datos
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }

    private void addMeasureListener(DatabaseReference measureRef, double min, double max, String measureType) {
        measureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String channelId = "vital_sign_alert_channel";
                String channelName = "Vital Sign Alert Channel";

                // Crear canal de notificaciones si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                for (DataSnapshot measureSnapshot : dataSnapshot.getChildren()) {
                    Integer value = measureSnapshot.child("Value").getValue(Integer.class);
                    String timestamp = measureSnapshot.child("Timestampt").getValue(String.class);

                    if (value != null && (value < min || value > max)) {
                        String notificationKey = timestamp + "_" + value;

                        // Verificar si ya se ha enviado una notificación para este valor y timestamp
                        if (!sentNotifications.contains(notificationKey)) {
                            // Crear notificación
                            String contentTitle = getString(R.string.measure_alert_title, measureType);
                            String contentText = getString(R.string.measure_alert_content, measureType, value, timestamp);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(patientDetailActivity.this, channelId)
                                    .setSmallIcon(R.drawable.baseline_person_search_24) // Asegúrate de tener este recurso en tu proyecto
                                    .setContentTitle(contentTitle)
                                    .setContentText(contentText)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true); // La notificación se eliminará cuando se toque

                            // Mostrar notificación
                            int notificationId = (int) System.currentTimeMillis(); // O cualquier otro mecanismo único
                            notificationManager.notify(notificationId, builder.build());

                            // Registrar la notificación como enviada
                            sentNotifications.add(notificationKey);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar posibles errores aquí
                Log.e("DatabaseError", "Error al leer datos: " + databaseError.getMessage());
            }
        });
    }

    private void addTemperatureListener(DatabaseReference measureRef, double max) {
        measureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String channelId = "temperature_alert_channel";
                String channelName = "Temperature Alert Channel";

                // Crear canal de notificaciones si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                for (DataSnapshot measureSnapshot : dataSnapshot.getChildren()) {
                    Double temperature = measureSnapshot.child("Temperature").getValue(Double.class);
                    String timestamp = measureSnapshot.child("Timestampt").getValue(String.class);

                    if (temperature != null && temperature > max) {
                        String notificationKey = timestamp + "_" + temperature;

                        // Verificar si ya se ha enviado una notificación para este valor y timestamp
                        if (!sentNotifications.contains(notificationKey)) {
                            // Crear notificación
                            String contentText = getString(R.string.temperature_alert_content, temperature, timestamp);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(patientDetailActivity.this, channelId)
                                    .setSmallIcon(R.drawable.baseline_person_search_24) // Asegúrate de tener este recurso en tu proyecto
                                    .setContentTitle(getString(R.string.temperature_alert_title))
                                    .setContentText(contentText)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true); // La notificación se eliminará cuando se toque

                            // Mostrar notificación
                            int notificationId = (int) System.currentTimeMillis(); // O cualquier otro mecanismo único
                            notificationManager.notify(notificationId, builder.build());

                            // Registrar la notificación como enviada
                            sentNotifications.add(notificationKey);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar posibles errores aquí
                Log.e("DatabaseError", "Error al leer datos: " + databaseError.getMessage());
            }
        });
    }

    private void showDatePickerDialog(OnDateSelectedListener listener) {
        // Obtener la fecha actual
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                patientDetailActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
                    listener.onDateSelected(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

   private void plotMeasure(DatabaseReference reference, String startDate, String endDate, String label) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> measuresEntries = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                boolean missingData = false;

                try {
                    Date start = dateFormat.parse(startDate);
                    Date end = dateFormat.parse(endDate);

                    if (start == null || end == null) {
                        Log.e("plotMeasure", "Start or end date is null");
                        return;
                    }

                    // Create a set of all dates within the range
                    Set<String> expectedDates = new HashSet<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(start);

                    while (!calendar.getTime().after(end)) {
                        expectedDates.add(dateFormat.format(calendar.getTime()));
                        calendar.add(Calendar.DATE, 1);
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer value = snapshot.child("Value").getValue(Integer.class);
                        String timestampStr = snapshot.child("Timestampt").getValue(String.class);

                        if (value != null && timestampStr != null) {
                            Date timestamp = dateFormat.parse(timestampStr);
                            if (timestamp != null && !timestamp.before(start) && !timestamp.after(end)) {
                                measuresEntries.add(new Entry(timestamp.getTime(), value));
                                expectedDates.remove(timestampStr);
                            }
                        }
                    }

                    if (!expectedDates.isEmpty()) {
                        missingData = true;
                    }

                    if (missingData || measuresEntries.isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.wrongFormatDate, Toast.LENGTH_LONG).show();
                        chart.clear();
                        return;
                    }

                    LineDataSet dataSet = new LineDataSet(measuresEntries, label);
                    dataSet.setColor(Color.RED);
                    LineData lineData = new LineData(dataSet);

                    chart.setData(lineData);
                    chart.getAxisRight().setEnabled(false);
                    chart.getXAxis().setEnabled(false);
                    chart.getDescription().setEnabled(false);
                    chart.setTouchEnabled(true);
                    chart.setPinchZoom(true);
                    chart.invalidate(); // Refresh chart

                } catch (Exception e) {
                    Log.e("plotMeasure", "Error parsing dates or setting up the chart", e);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void plotBloodPressure(DatabaseReference reference, String startDate, String endDate, String label){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> spEntries = new ArrayList<>();
                ArrayList<Entry> dpEntries = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                boolean missingData = false;

                try {
                    Date start = dateFormat.parse(startDate);
                    Date end = dateFormat.parse(endDate);

                    if (start == null || end == null) {
                        Log.e("plotBloodPressure", "Start or end date is null");
                        return;
                    }

                    // Create a set of all dates within the range
                    Set<String> expectedDates = new HashSet<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(start);

                    while (!calendar.getTime().after(end)) {
                        expectedDates.add(dateFormat.format(calendar.getTime()));
                        calendar.add(Calendar.DATE, 1);
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer valueSP = snapshot.child("Value SP").getValue(Integer.class);
                        Integer valueDP = snapshot.child("Value DP").getValue(Integer.class);
                        String timestampStr = snapshot.child("Timestampt").getValue(String.class);

                        if (valueSP != null && valueDP != null && timestampStr != null) {
                            Date timestamp = dateFormat.parse(timestampStr);
                            if (timestamp != null && !timestamp.before(start) && !timestamp.after(end)) {
                                spEntries.add(new Entry(timestamp.getTime(), valueSP));
                                dpEntries.add(new Entry(timestamp.getTime(), valueDP));
                                expectedDates.remove(timestampStr);
                            }
                        }
                    }

                    if (!expectedDates.isEmpty()) {
                        missingData = true;
                    }

                    if (missingData || spEntries.isEmpty() || dpEntries.isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.wrongFormatDate, Toast.LENGTH_LONG).show();
                        chart.clear();
                        return;
                    }

                    LineDataSet dataSet1 = new LineDataSet(spEntries, label + " SP");
                    LineDataSet dataSet2 = new LineDataSet(dpEntries, label + " DP");
                    dataSet1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
                    dataSet1.setValueTextColor(ColorTemplate.COLORFUL_COLORS[0]);
                    dataSet2.setColor(ColorTemplate.COLORFUL_COLORS[1]);
                    dataSet2.setValueTextColor(ColorTemplate.COLORFUL_COLORS[1]);
                    LineData lineData = new LineData(dataSet1, dataSet2);
                    chart.setData(lineData);

                    chart.getAxisRight().setEnabled(false);
                    chart.getXAxis().setEnabled(false);
                    chart.getDescription().setEnabled(false);
                    chart.setTouchEnabled(true);
                    chart.setPinchZoom(true);
                    chart.invalidate(); // Refresh chart

                } catch (Exception e) {
                    Log.e("plotBloodPressure", "Error parsing dates or setting up the chart", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void printSymptoms(String startDate, String endDate){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Measures").child("Sympthoms");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Integer> symptomsCount = new HashMap<>();
                Map<String, Integer> consistencyCount = new HashMap<>();
                Map<String, Integer> compositionCount = new HashMap<>();
                int totalDepositions = 0;
                int depositionsCount = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                boolean missingData = false;

                try {
                    Date start = dateFormat.parse(startDate);
                    Date end = dateFormat.parse(endDate);

                    if (start == null || end == null) {
                        Log.e("printSymptoms", "Start or end date is null");
                        return;
                    }

                    // Create a set of all dates within the range
                    Set<String> expectedDates = new HashSet<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(start);

                    while (!calendar.getTime().after(end)) {
                        expectedDates.add(dateFormat.format(calendar.getTime()));
                        calendar.add(Calendar.DATE, 1);
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String timestampStr = snapshot.child("Timestampt").getValue(String.class);
                        if (timestampStr != null) {
                            Date timestamp = dateFormat.parse(timestampStr);
                            if (timestamp != null && !timestamp.before(start) && !timestamp.after(end)) {
                                for (DataSnapshot symptomSnapshot : snapshot.getChildren()) {
                                    String key = symptomSnapshot.getKey();
                                    Object value = symptomSnapshot.getValue();

                                    if (value instanceof Boolean && (Boolean) value) {
                                        if (key != null) {
                                            symptomsCount.put(key, symptomsCount.getOrDefault(key, 0) + 1);
                                        }
                                    }

                                    if ("Bowel movements".equals(key)) {
                                        Integer numberOfDepositions = symptomSnapshot.child("Number of depositions").getValue(Integer.class);
                                        String consistency = symptomSnapshot.child("Consistency").getValue(String.class);
                                        String composition = symptomSnapshot.child("Composition").getValue(String.class);

                                        if (numberOfDepositions != null) {
                                            totalDepositions += numberOfDepositions;
                                            depositionsCount++;
                                        }

                                        if (consistency != null) {
                                            consistencyCount.put(consistency, consistencyCount.getOrDefault(consistency, 0) + 1);
                                        }

                                        if (composition != null) {
                                            compositionCount.put(composition, compositionCount.getOrDefault(composition, 0) + 1);
                                        }
                                    }
                                }
                                expectedDates.remove(timestampStr);
                            }
                        }
                    }

                    if (!expectedDates.isEmpty()) {
                        missingData = true;
                    }

                    if (missingData) {
                        Log.w("printSymptoms", "Missing data entries for some dates within the given range.");
                        symptomsTextView.setText(""); // Clear the text view if data is missing
                        return;
                    }

                    StringBuilder displayText = new StringBuilder();
                    // Filtrar y mostrar síntomas booleanos que se repiten al menos 3 veces
                    for (Map.Entry<String, Integer> entry : symptomsCount.entrySet()) {
                        if (entry.getValue() >= 3) {
                            int symptomStringId = getResources().getIdentifier(entry.getKey().toLowerCase(Locale.ROOT).replace(" ", "_"), "string", getPackageName());
                            if (symptomStringId != 0) {
                                displayText.append(getString(symptomStringId)).append("\n");
                            } else {
                                displayText.append(entry.getKey()).append("\n");
                            }
                        }
                    }

                    // Filtrar y mostrar consistencias que se repiten al menos 3 veces
                    for (Map.Entry<String, Integer> entry : consistencyCount.entrySet()) {
                        if (entry.getValue() >= 3) {
                            int consistencyStringId = getResources().getIdentifier(entry.getKey().toLowerCase(Locale.ROOT), "string", getPackageName());
                            if (consistencyStringId != 0) {
                                displayText.append(getString(R.string.consistency)).append(": ").append(getString(consistencyStringId)).append("\n");
                            } else {
                                displayText.append(getString(R.string.consistency)).append(": ").append(entry.getKey()).append("\n");
                            }
                        }
                    }

                    // Filtrar y mostrar composiciones que se repiten al menos 3 veces
                    for (Map.Entry<String, Integer> entry : compositionCount.entrySet()) {
                        if (entry.getValue() >= 3) {
                            int compositionStringId = getResources().getIdentifier(entry.getKey().toLowerCase(Locale.ROOT), "string", getPackageName());
                            if (compositionStringId != 0) {
                                displayText.append(getString(R.string.composition)).append(": ").append(getString(compositionStringId)).append("\n");
                            } else {
                                displayText.append(getString(R.string.composition)).append(": ").append(entry.getKey()).append("\n");
                            }
                        }
                    }

                    // Calcular y mostrar la media de "Number of depositions"
                    if (depositionsCount > 0) {
                        double averageDepositions = (double) totalDepositions / depositionsCount;
                        displayText.append(getString(R.string.bowel_movements_average, String.format(Locale.getDefault(), "%.2f", averageDepositions))).append("\n");
                    }

                    symptomsTextView.setText(displayText.toString());

                } catch (Exception e) {
                    Log.e("printSymptoms", "Error parsing dates or setting up the text view", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });
    }

    private void createPdf() {
        // Leer los datos del paciente 1
        DatabaseReference personalRoute = FirebaseDatabase.getInstance().getReference("patients").child("patient 1");
        DatabaseReference clinicalRoute = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Clinical data");
        DatabaseReference doctorRoute = FirebaseDatabase.getInstance().getReference("patients").child("patient 1").child("Medical information").child("Oncology team contact").child("Doctor 1");
        personalRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patName = dataSnapshot.child("Name").getValue(String.class);
                patGender = dataSnapshot.child("Gender").getValue(String.class);
                patAge = dataSnapshot.child("Age").getValue(Integer.class);
                patHeight = dataSnapshot.child("Height").getValue(Integer.class);
                patWeight = dataSnapshot.child("Weight").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });

        clinicalRoute.child("Adverse events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String toxicity = snapshot.getValue(String.class);
                    patToxicities.add(toxicity);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });

        clinicalRoute.child("Treatment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String immunotherapy = snapshot.getValue(String.class);
                    patTreatments.add(immunotherapy);
                }
                actualImmunotherapy = patTreatments.get(patTreatments.size() - 1);
                patTreatments.remove(patTreatments.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });

        doctorRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docName = dataSnapshot.child("Name").getValue(String.class);
                docEmail = dataSnapshot.child("Mail").getValue(String.class);
                docPhone = dataSnapshot.child("Phone").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });

        // Creamos el pdf

        try {
            // Crear el archivo PDF
            File pdfFile = new File(getExternalFilesDir(null), "HCE_" + patName + ".pdf");
            PdfWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                writer = new PdfWriter(Files.newOutputStream(pdfFile.toPath()));
            }
            assert writer != null;
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // Ajuste del margen
            document.setMargins(20, 20, 20, 20);

            // Título centrado
            Paragraph title = new Paragraph("Historia Clínica Electrónica (HCE)")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Subtítulo centrado
            Paragraph subtitle = new Paragraph("Generado por ImmunoApp")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitle);

            document.add(new Paragraph("\n"));

            // Información personal
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addCell(new Cell(1, 2).add(new Paragraph("Información Personal").setTextAlignment(TextAlignment.CENTER).setBold()));
            table.addCell("Nombre");
            table.addCell(patName);
            table.addCell("Género");
            table.addCell(patGender);
            table.addCell("Edad");
            table.addCell(String.valueOf(patAge));
            table.addCell("Altura");
            table.addCell(String.valueOf(patHeight));
            table.addCell("Peso");
            table.addCell(String.valueOf(patWeight));
            document.add(table);

            document.add(new Paragraph("\n"));

            // Información clínica
            table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addCell(new Cell(1, 2).add(new Paragraph("Información Clínica").setTextAlignment(TextAlignment.CENTER).setBold()));
            table.addCell("Inmunoterapia Actual");
            table.addCell(actualImmunotherapy);
            table.addCell("Inmunoterapias Pasadas");
            table.addCell(patTreatments != null ? String.valueOf(patTreatments) : "Ninguno");
            table.addCell("Toxicidades");
            table.addCell(String.valueOf(patToxicities));
            document.add(table);

            document.add(new Paragraph("\n"));

            // Información del equipo
            table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addCell(new Cell(1, 2).add(new Paragraph("Información del Equipo").setTextAlignment(TextAlignment.CENTER).setBold()));
            table.addCell("Nombre");
            table.addCell(docName);
            table.addCell("Correo");
            table.addCell(docEmail);
            table.addCell("Teléfono");
            table.addCell(String.valueOf(docPhone));
            document.add(table);

            document.add(new Paragraph("\n"));

            document.close();

            openPdf(pdfFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPdf(String path) {
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(this, "com.example.myapp.fileprovider", file);  // Reemplaza con tu nombre de paquete
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    public void fetchPatientData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("patients").child("patient 1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el último tratamiento
                String lastTreatmentKey = "";
                for (DataSnapshot treatmentSnapshot : dataSnapshot.child("Medical information").child("Clinical data").child("Treatment").getChildren()) {
                    lastTreatmentKey = treatmentSnapshot.getValue(String.class);
                }

                // Buscar en immunotherapies
                databaseReference.child("immunotherapies").orderByChild("id").equalTo(lastTreatmentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot immSnapshot) {
                        if (immSnapshot.exists()) {
                            Map<String, String> immData = new HashMap<>();
                            for (DataSnapshot immChildSnapshot : immSnapshot.getChildren()) {
                                // Almacenar datos de immunotherapies en el map
                                immData.put(getString(R.string.Generalsymptoms), immChildSnapshot.child("General symptoms").getValue(String.class));
                                immData.put(getString(R.string.Endocrinopathy), immChildSnapshot.child("Endocrinopathy").getValue(String.class));
                                immData.put(getString(R.string.GItoxicity), immChildSnapshot.child("GI toxicity").getValue(String.class));
                                immData.put(getString(R.string.Hepatotoxicity), immChildSnapshot.child("Hepatotoxicity").getValue(String.class));
                                immData.put(getString(R.string.Skintoxicity), immChildSnapshot.child("Skin toxicity").getValue(String.class));
                                immData.put(getString(R.string.Othertoxicities), immChildSnapshot.child("Oher toxicities").getValue(String.class));
                            }
                            // Mostrar la información en pantalla
                            TextView textViewTImm = findViewById(R.id.immDataTextView);
                            displayData(immData, textViewTImm);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Error al leer los datos
                    }
                });

                // Obtener el último evento adverso
                List<String> adverseEventKeys = new ArrayList<>();

                recyclerViewTox.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                List<Map<String, String>> toxList = new ArrayList<>();
                ToxAdapter adapter = new ToxAdapter(toxList);
                recyclerViewTox.setAdapter(adapter);

                for (DataSnapshot adverseEventSnapshot : dataSnapshot.child("Medical information").child("Clinical data").child("Adverse events").getChildren()) {
                    String adverseEventKey = adverseEventSnapshot.getValue(String.class);
                    if (adverseEventKey != null) {
                        adverseEventKeys.add(adverseEventKey);
                    }
                }

                // Buscar en tox para cada evento adverso
                for (String adverseEventKey : adverseEventKeys) {
                    databaseReference.child("toxicities").orderByChild("id").equalTo(adverseEventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot toxSnapshot) {
                            if (toxSnapshot.exists()) {
                                Map<String, String> toxData = new HashMap<>();
                                for (DataSnapshot toxChildSnapshot : toxSnapshot.getChildren()) {
                                    toxData.put("Toxicidad", toxChildSnapshot.child("id").getValue(String.class));
                                    toxData.put("Baseline monitoring", toxChildSnapshot.child("Baseline monitoring").getValue(String.class));
                                    toxData.put("Diagnosis", toxChildSnapshot.child("Diagnosis").getValue(String.class));
                                    toxData.put("Presentation", toxChildSnapshot.child("Presentation").getValue(String.class));
                                }

                                // Añadir datos al adaptador
                                toxList.add(toxData);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejar errores
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error al leer los datos
            }
        });
    }

    public void displayData(Map<String, String> data, TextView textView) {
        StringBuilder displayText = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            displayText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        textView.setText(displayText.toString());
    }
}