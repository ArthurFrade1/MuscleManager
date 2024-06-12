package com.example.musclemanager.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musclemanager.R;
import com.example.musclemanager.Treino;
import com.example.musclemanager.exercicio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class fragment_exercicios extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_exercicios() {
        // Required empty public constructor
    }

    public static fragment_exercicios newInstance(String param1, String param2) {
        fragment_exercicios fragment = new fragment_exercicios();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private BottomSheetDialog dialog;

    String nome;
    String descricao;
    String imagemUrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_exercicios, container, false);


        FloatingActionButton FloatingButton = view.findViewById(R.id.floatingbutton);

        dialog = new BottomSheetDialog(requireContext(),R.style.MyTransparentBottomSheetDialogTheme);
        View view2 = getLayoutInflater().inflate(R.layout.bottom_sheet_event, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view2);
        Button bt_submit = view2.findViewById(R.id.submitevent);
        Button sendImage = view2.findViewById(R.id.sendImage);
//        sendDate = view.findViewById(R.id.sendDate);
        EditText tituloText=view2.findViewById(R.id.Titulo);
        EditText descricaoText=view2.findViewById(R.id.Descricao);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Configurando animação de entrada do BottomSheetDialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;







        FloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deixa transparente para que só possa se ver o background
                dialog.show();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference treinosRef = db.collection("treinos");

        treinosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Extrair os dados do documento
                        nome= document.getString("nome");
                        descricao = document.getString("descricao");
                        imagemUrl= document.getString("imagem");

                        Log.d(TAG, "Nome: " + nome + ", Descrição: " + descricao  );

                        //crio o cardview de acordo como os dados
                        criaCard(document);
                    }
                } else {
                    Log.d(TAG, "Erro ao obter documentos: ", task.getException());
                }
            }
        });
        exerciciosLinear=view.findViewById(R.id.exerciciosLinear);

        return view;
    }

    LinearLayout exerciciosLinear;

    public void criaCard(QueryDocumentSnapshot document) {
        Context context = requireContext();

        // Criando CardView
        CardView cardView = new CardView(context);
        cardView.setCardElevation(4);
        cardView.setRadius(16f);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white, getContext().getTheme()));

        // Configurando layout params do CardView
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardLayoutParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(cardLayoutParams);

        // Configurando LinearLayout horizontal
        LinearLayout layoutHorizontal = new LinearLayout(context);
        layoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
        layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutHorizontal.setLayoutParams(linearLayoutParams);

        // Criando um ImageView
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(imagemUrl)
                .into(imageView);

        // Configurando o imageView
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                160,
                160
        );
        imageLayoutParams.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(imageLayoutParams);

        // Configurando fonte
        Typeface typeface = ResourcesCompat.getFont(context, R.font.aclonica);

        // Criando textview titulo
        TextView titulo = new TextView(context);
        titulo.setTypeface(typeface);
        titulo.setText(nome);
        titulo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        // Configurando textview titulo
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textLayoutParams.setMargins(16, 16, 16, 8);
        titulo.setLayoutParams(textLayoutParams);

        // Criando textview descricao
        TextView editDescricao = new TextView(context);
        editDescricao.setTypeface(typeface);
        editDescricao.setText(descricao);
        editDescricao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        // Configurando textview descricao
        LinearLayout.LayoutParams textLayoutParamsDesc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textLayoutParamsDesc.setMargins(16, 8, 16, 16);
        editDescricao.setLayoutParams(textLayoutParamsDesc);

        // Configurando LinearLayout vertical
        LinearLayout layoutVertical = new LinearLayout(context);
        layoutVertical.setOrientation(LinearLayout.VERTICAL);
        layoutVertical.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        layoutVertical.addView(titulo);
        layoutVertical.addView(editDescricao);

        // Adicionando o imageview
        layoutHorizontal.addView(imageView);
        // Adicionando o layout vertical ao layout horizontal
        layoutHorizontal.addView(layoutVertical);

        // Adicionando o layout horizontal ao cardView
        cardView.addView(layoutHorizontal);
        // Adicionando o cardView ao layout principal
        exerciciosLinear.addView(cardView);

        ArrayList<String> exercicios = (ArrayList<String>) document.get("exercicios"); // Certifique-se de que "exercicios" é a chave correta

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titulo = document.getString("nome");
                String descricao = document.getString("descricao");
                String urlImagem = document.getString("imagem");

                Intent intent = new Intent(getActivity(), Treino.class);
                intent.putExtra("tituloExercicio", titulo);
                intent.putExtra("descricaoExercicio", descricao);
                intent.putExtra("imagemExercicio", urlImagem);
                intent.putStringArrayListExtra("listaExercicios", exercicios);
                startActivity(intent);
            }
        });
    }

}