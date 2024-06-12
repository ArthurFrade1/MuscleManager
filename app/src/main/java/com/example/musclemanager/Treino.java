package com.example.musclemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Treino extends AppCompatActivity {
    private ArrayList<String> exercicios = new ArrayList<>();
    LinearLayout exerciciosLinear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treino);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        exerciciosLinear = findViewById(R.id.exerciciosLinear);

        TextView tituloText=findViewById(R.id.Titulo);
        TextView descricaoText=findViewById(R.id.Descricao);
        ImageView imageView=findViewById(R.id.Imagem);
        String titulo="", descricao="", imagem="";
        Intent intent = getIntent();

        //Recebe informações
        if (intent != null) {
            titulo = ((Intent) intent).getStringExtra("tituloExercicio");
            descricao = intent.getStringExtra("descricaoExercicio");
            imagem = intent.getStringExtra("imagemExercicio");
            exercicios = intent.getStringArrayListExtra("listaExercicios");
        }

        //Insiro elas nos meus componentes
        tituloText.setText(titulo);
        descricaoText.setText(descricao);

        Glide.with(this)
                .load(imagem)
                .into(imageView);

        //Acessando os exercicios
        if (exercicios != null && !exercicios.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference exerciciosRef = db.collection("exercicios");

            exerciciosRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String nomeExercicio = document.getString("nome");
                            if (exercicios.contains(nomeExercicio)) {
                                Log.d("Treino", "Exercício encontrado: " + nomeExercicio);

                                nome= document.getString("nome");
                                observacoes = document.getString("observacoes");
                                imagemUrl= document.getString("imagem");

                                criaCard(document);
                            }
                        }
                    }
                } else {

                }
            });
        }



    }

    String nome;
    String observacoes;
    String imagemUrl;

    public void criaCard(QueryDocumentSnapshot document){
        Context context = Treino.this;

        //Criando cardiview

        CardView cardView = new CardView(context);
        cardView.setCardElevation(4);
        cardView.setRadius(16f);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white, context.getTheme()));

        //Configurando cardview
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardLayoutParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(cardLayoutParams);

        //Configurando LinearLayout horizontal
        LinearLayout layoutHorizontal = new LinearLayout(context);
        layoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
        layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutHorizontal.setLayoutParams(linearLayoutParams);

        //Criando um ImageView
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(imagemUrl)
                .into(imageView);

        //Configurando o imageView
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                160,
                160
        );
        imageLayoutParams.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(imageLayoutParams);

        //Configurando fonte
        Typeface typeface = ResourcesCompat.getFont(context, R.font.aclonica);

        //Criando textview
        TextView titulo = new TextView(context);
        titulo.setTypeface(typeface);
        titulo.setText(nome);
        titulo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        //Configurando textview
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        textLayoutParams.setMargins(16, 16, 16, 16);
        titulo.setLayoutParams(textLayoutParams);

        //Adicionando o textview
        layoutHorizontal.addView(titulo);
        //Adicionando o imageview
        layoutHorizontal.addView(imageView);

        //Adicionando o layout horizontal ao cardView
        cardView.addView(layoutHorizontal);

        //Adicionando o cardView ao layout principal
        exerciciosLinear.addView(cardView);

        //Ao clicar no cardview
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titulo = document.getString("nome");
                String observacoes = document.getString("observacoes");
                String urlImagem = document.getString("imagem");

                Intent intent = new Intent(Treino.this, exercicio.class);
                intent.putExtra("tituloExercicio", titulo);
                intent.putExtra("observacoesExercicio", observacoes);
                intent.putExtra("imagemExercicio", urlImagem);
                startActivity(intent);
            }
        });
    }
}