package com.example.musclemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class exercicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tituloText=findViewById(R.id.Titulo);
        TextView observacoesText=findViewById(R.id.Observacoes);
        ImageView imageView=findViewById(R.id.Imagem);
        String titulo="", observacoes="", imagem="";
        Intent intent = getIntent();

        //Recebe informações
        if (intent != null) {
             titulo = ((Intent) intent).getStringExtra("tituloExercicio");
             observacoes = intent.getStringExtra("observacoesExercicio");
             imagem = intent.getStringExtra("imagemExercicio");

        }

        //Insiro elas nos meus componentes
        tituloText.setText(titulo);
        observacoesText.setText(observacoes);

        Glide.with(this)
                .load(imagem)
                .into(imageView);

    }
}