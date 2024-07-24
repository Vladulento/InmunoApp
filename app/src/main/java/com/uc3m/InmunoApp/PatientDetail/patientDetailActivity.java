package com.uc3m.InmunoApp.PatientDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.uc3m.InmunoApp.R;

import java.util.ArrayList;
import java.util.Objects;

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
    TextView symptomsTextView;
    EditText deleteSymptomEditText;

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
        symptomsTextView = findViewById(R.id.symptomsList);
        deleteSymptomEditText = findViewById(R.id.deleteSymptomsEditText);

        // Establecer recursos

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();                                    // Recibir los datos del paciente del Intent anterior
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        int age = intent.getIntExtra("age", 0);
        int weight = intent.getIntExtra("weight", 0);
        int height = intent.getIntExtra("height", 0);
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

        // Listeners

        backButton.setOnClickListener(v -> onBackPressed());        // Botón de atrás

        // Botón de chat
        launchButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(patientDetailActivity.this, ChatActivity.class);
            startActivity(intent1);
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

        // Llamada a funciones

        getTreatmentsP1();      // Leer los valores de los tratamientos del paciente 1

        getMaxTemperatureP1();  // Leer los valores de la temperatura del paciente 1

        getRespRateMP1();       // Leer los valores de las frecuencias respiratorias del paciente 1

        getHeartRateMP1();      // Leer los valores de las frecuencias cardíacas del paciente 1

        getSymptomsP1();        // Leer los síntomas del paciente 1
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
                symptomsTextView.setText(symptomsList);
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
}