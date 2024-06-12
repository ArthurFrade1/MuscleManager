package com.example.musclemanager.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musclemanager.MainActivity;
import com.example.musclemanager.R;
import com.example.musclemanager.exercicio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_treino#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_treino extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_treino() {
        // Required empty public constructor
    }

    public static fragment_treino newInstance(String param1, String param2) {
        fragment_treino fragment = new fragment_treino();
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
    private FirebaseFirestore db;

    private BottomSheetDialog dialog;
    String nome;
    String observacoes;
    String imagemUrl;
    View view;
    LinearLayout exerciciosLinear;

    CollectionReference referencia;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view= inflater.inflate(R.layout.fragment_treino, container, false);
        exerciciosLinear=view.findViewById(R.id.exerciciosLinear);

        //Checando se é usuario ou admim
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            checkIfUserIsAdmin(usuario.getUid());
        } else {
            Toast.makeText(getContext(), "Usuário não autenticado.",
                    Toast.LENGTH_SHORT).show();
        }

        //Gerenciando database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         referencia = db.collection("exercicios");


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("exercicios");
        //Pegando dados do firestore


        CollectionReference treinosRef = db.collection("exercicios");

        treinosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Extrair os dados do documento
                        nome= document.getString("nome");
                        observacoes = document.getString("observacoes");
                        imagemUrl= document.getString("imagem");

                        Log.d(TAG, "Nome: " + nome + ", Descrição: " + observacoes  );

                        //crio o cardview de acordo como os dados
                        criaCard(document);
                    }
                } else {
                    Log.d(TAG, "Erro ao obter documentos: ", task.getException());
                }
            }
        });

        configBottomSheet();

        return view;
    }
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    private String linkImagem;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    public void configBottomSheet(){
        //Configura dialog box
        FloatingActionButton FloatingButton = view.findViewById(R.id.floatingbutton);

        ////////////
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




        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pega imagem da galeria
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);

            }
        });
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();

                        // Agora, envie a imagem para o Firebase Storage
                        StorageReference imageRef = imagesRef.child(imageUri.getLastPathSegment());
                        imageRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Upload concluído com sucesso
                                    Toast.makeText(requireContext(), "Upload bem-sucedido!", Toast.LENGTH_SHORT).show();
//                                    preenchido=true;

                                    // Obtém o URL da imagem
                                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        linkImagem = uri.toString();

                                    }).addOnFailureListener(e -> {
                                        // Ocorreu um erro ao obter o URL da imagem
                                        Toast.makeText(requireContext(), "Erro ao obter URL da imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Ocorreu um erro durante o upload
                                    Toast.makeText(requireContext(), "Erro ao fazer upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference exerciciosRef = db.collection("exercicios");

// Ao clicar no botão de submit
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = tituloText.getText().toString();
                String descricao = descricaoText.getText().toString();

                if (!descricao.isEmpty() && !titulo.isEmpty()) {
                    // Ao apertar no botão de submit

                    // Salvar os dados no Firestore
                    Map<String, Object> dados = new HashMap<>();
                    dados.put("obsevacoes", descricao);
                    dados.put("nome", titulo);
                    // Adicionando uma URL de imagem padrão
                    String imagemPadrao = "https://i0.wp.com/blog.portaleducacao.com.br/wp-content/uploads/2022/07/A-classificacao-dos-musculos-do-corpo-humano.jpg";
                    dados.put("imagem", linkImagem);

                    // Adicionando os dados ao Firestore
                    exerciciosRef.document(titulo).set(dados)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Sugestão gravada!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Erro ao gravar sugestão: " + e.getMessage());
                                    Toast.makeText(getContext(), "Erro ao gravar sugestão!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Preencha os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void criaCard(QueryDocumentSnapshot document){
        Context context = requireContext();

        //Criando cardiview

        CardView cardView = new CardView(requireContext());
        cardView.setCardElevation(4);
        cardView.setRadius(16f);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white, getContext().getTheme()));

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

                Intent intent = new Intent(getActivity(), exercicio.class);
                intent.putExtra("tituloExercicio", titulo);
                intent.putExtra("observacoesExercicio", observacoes);
                intent.putExtra("imagemExercicio", urlImagem);
                startActivity(intent);
            }
        });
    }
    private boolean isAdmin=false;
    private FirebaseAuth mAuth;
    private void checkIfUserIsAdmin(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                isAdmin = document.getBoolean("isAdmin");
                                ajustarInterfaceParaPrivilegios();
                            } else {
                                Toast.makeText(getContext(), "Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("MainActivity", "Erro ao verificar privilégios do usuário", task.getException());
                        }
                    }
                });
    }
    private void ajustarInterfaceParaPrivilegios() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        String nomeUsuario = usuario.getDisplayName(); //NOME USUARIO

        if (isAdmin) {

        } else {
            FloatingActionButton show = view.findViewById(R.id.floatingbutton);
            show.setVisibility(View.INVISIBLE);
        }
    }

}
