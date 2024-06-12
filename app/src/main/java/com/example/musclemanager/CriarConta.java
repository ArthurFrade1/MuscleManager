package com.example.musclemanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CriarConta extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean isAdmin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView nome = findViewById(R.id.nomeEdit);
        TextView email = findViewById(R.id.emailEdit);
        TextView senha = findViewById(R.id.senhaEdit);
        Button button = findViewById(R.id.butLogin);
        SwitchMaterial switchAdmin = findViewById(R.id.switchAdmin);

        //Verifico se esta marcado como administrador
        switchAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isAdmin = true;
            }
        });


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (String.valueOf(email.getText()).isEmpty() || String.valueOf(senha.getText()).isEmpty()) {
                    Toast.makeText(CriarConta.this, "Preencha os campos!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    criarConta(String.valueOf(email.getText()), String.valueOf(senha.getText()), String.valueOf(nome.getText()), isAdmin); // false se não for admin
                }
            }
        });
    }

    private void criarConta(String email, String senha, String nome, boolean isAdmin) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nome)
                                        .build();
                                user.updateProfile(profileUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    adicionarUsuarioAoFirestore(user.getUid(), nome, email, isAdmin);
                                                    Toast.makeText(CriarConta.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(CriarConta.this, "Houve um problema ao criar a conta!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(CriarConta.this, "Houve um problema ao criar a conta!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void adicionarUsuarioAoFirestore(String userId, String nome, String email, boolean isAdmin) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nome", nome);
        userMap.put("email", email);
        userMap.put("isAdmin", isAdmin);

        db.collection("users").document(userId).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("CriarConta", "Usuário adicionado ao Firestore"))
                .addOnFailureListener(e -> Log.e("CriarConta", "Erro ao adicionar usuário ao Firestore", e));
    }
}
