package com.github.dishdelish;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.github.dishdelish.Recipes.Recipe;
import com.github.dishdelish.Recipes.Utensils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Arrays;
import java.util.HashMap;

public class UploadingRecipeFragment extends Fragment {
    Button chooseImg, uploadImg;
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    String storagepath = "Recipes_image/";

    //creating reference to firebase storage
//    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://dish-delish-recipes.appspot.com");
    DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_recipes, container, false);

        chooseImg = (Button)view.findViewById(R.id.chooseImg);
        uploadImg = (Button)view.findViewById(R.id.uploadButton);
        imgView = (ImageView)view.findViewById(R.id.imgView);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading....");

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    uploadRecipe(filePath);
//                    String path[] = filePath.toString().split("/");
//
//                    StorageReference childRef = storageRef.child(path[path.length - 1] + ".jpg");
//
//                    //uploading the image
//                    UploadTask uploadTask = childRef.putFile(filePath);
//
//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            pd.dismiss();
//                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            pd.dismiss();
//                            Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
                else {
                    Toast.makeText(getActivity(), "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Upload the recipe to Firebase.
    private void uploadRecipe(final Uri recipeImageUri) {
        pd.show();

        EditText recipeName = view.findViewById(R.id.recipeName);
        EditText cookTime = view.findViewById(R.id.cookTime);
        EditText prepTime = view.findViewById(R.id.prepTime);
        EditText servings = view.findViewById(R.id.servings);
        EditText utensils = view.findViewById(R.id.utensils);

//        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = "ZHANG CHi"; // should be firebaseUser.getUid()

        // We are taking the filepath as storagepath + firebaseUser.getUid()+".png"
        String filepathname = storagepath + "_" + userId;
        StorageReference storageReference1 = storageRef.child(filepathname);
        storageReference1.putFile(recipeImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                // We will get the url of our image using uritask
                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {

                    // updating our image url into the realtime database
                    HashMap<String, Object> hashMap = new HashMap<>();
                    Recipe recipe = new Recipe();
                    recipe.setRecipeName(recipeName.getText().toString());
                    recipe.setImage(downloadUri.toString());
                    recipe.setCookTime(Integer.parseInt(cookTime.getText().toString()));
                    recipe.setPrepTime(Integer.parseInt(prepTime.getText().toString()));
                    recipe.setServings(Integer.parseInt(servings.getText().toString()));
                    recipe.setUtensils(new Utensils(Arrays.asList(utensils.getText().toString())));
                    hashMap.put(recipeName.getText().toString(), recipe);
                    // should use firebaseUser.getUid()
                    databaseReference.child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error Uploading ", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        });

    }
}
