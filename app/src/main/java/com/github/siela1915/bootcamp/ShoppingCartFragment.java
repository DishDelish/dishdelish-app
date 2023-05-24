package com.github.siela1915.bootcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ShoppingListManager manager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(String param1, String param2) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        manager = new ShoppingListManager(getContext());
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.shoppingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> list = manager.getShoppingList();
        ShoppingListAdapter adapter = new ShoppingListAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        // Button setup
        Button clearAllButton = view.findViewById(R.id.buttonClearAll);
        Button deleteSelectedButton = view.findViewById(R.id.buttonDeleteSelected);

        clearAllButton.setOnClickListener(v -> {
            clearAllItems(adapter);
        });

        deleteSelectedButton.setOnClickListener(v -> {
            deleteSelectedItems(adapter);
        });

        EditText editTextItem = view.findViewById(R.id.editTextItem);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            String newItem = editTextItem.getText().toString().trim();
            if (!newItem.isEmpty()) {
                manager.addIngredient(newItem);
                adapter.addItem(newItem);
                editTextItem.setText("");
            }
        });

        return view;
    }

    private void clearAllItems(ShoppingListAdapter adapter) {
        List<String> shoppingList = adapter.getShoppingList();
        manager.clearShoppingList();
        shoppingList.clear();
        adapter.notifyDataSetChanged();
    }

    private void deleteSelectedItems(ShoppingListAdapter adapter) {
        adapter.removeSelectedItems();
    }

}