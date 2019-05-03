package com.example.homepage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShoppingListItemsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    public static ArrayList<Ingredient> stringShopList;
    final MyShoppingListItemsRecyclerViewAdapter rcshopAdapter = new MyShoppingListItemsRecyclerViewAdapter();
    private OnListFragmentInteractionListener mListener;
    private FloatingActionButton add;
    private Button del;
    public static ArrayList<String> haveBeenDel = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingListItemsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingListItemsFragment newInstance(int columnCount) {
        ShoppingListItemsFragment fragment = new ShoppingListItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    public void getShopDatabase(DatabaseReference shopDatabase) {
        if (stringShopList == null) {
            stringShopList = new ArrayList<>();
        }

        shopDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stringShopList = new ArrayList<>();
                String name;
                double num;
                String theunit = "";
                String wholeitem;

                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    name = items.getKey();
                    if (name.indexOf(':') != -1) {
                        name = name.substring(0, name.indexOf(':') - 1);
                    }
                    num = 0;
                    if (items.child("amount").exists())
                        num = Double.valueOf(items.child("amount").getValue().toString());

                    if (items.child("unit").exists())
                        theunit = items.child("unit").getValue().toString();
                    if (num % 1 == 0) {
                        wholeitem = name + ": " + (int) num + " " + theunit;
                    } else {
                        num = ((int) (num * 100)) / (double) 100;
                        wholeitem = name + ": " + num + " " + theunit;
                    }
                    Ingredient i = new Ingredient(wholeitem, num, theunit);
                    i.key = items.getKey();
                    stringShopList.add(i);
                }
                rcshopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View shopview = inflater.inflate(R.layout.fragment_shoppinglistitems_list, container, false);
        RecyclerView shoprec = (RecyclerView) shopview.findViewById(R.id.shoppingListID);
        shoprec.setLayoutManager(new LinearLayoutManager(getActivity()));
        shoprec.setAdapter(rcshopAdapter);

        final DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
        getShopDatabase(shopDatabase);

        add = (FloatingActionButton) shopview.findViewById(R.id.addShopItemButton);
        if (add == null) {
            Log.w("add is", "null");
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.edit_time);
                final EditText nameText = (EditText) dialog.findViewById(R.id.name_choice);
                final EditText quantText = (EditText) dialog.findViewById(R.id.quant_choice);
                final Spinner unitChoice = (Spinner) dialog.findViewById(R.id.unit_choice);
                TextView confirm = (TextView) dialog.findViewById(R.id.confirmText);
                dialog.show();
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean valid = true;
                        if (TextUtils.isEmpty(nameText.getText())) {
                            valid = false;
                            nameText.setError("Ingredient name is required!");
                        }
                        if (TextUtils.isEmpty(quantText.getText())) {
                            valid = false;
                            quantText.setError("Ingredient quantity is required!");
                        }
                        if (valid) {
                            String name = nameText.getText().toString().toLowerCase();
                            Double quant = Double.parseDouble(quantText.getText().toString());
                            String unit = unitChoice.getSelectedItem().toString();
                            if (unit.equals("No Unit")) {
                                unit = "";
                            }
                            final Ingredient i = new Ingredient(name, quant, unit);
                            shopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(i.name).exists()) {
                                        double existingVal = Double.parseDouble(dataSnapshot.child(i.name).child("amount").getValue().toString());
                                        String dbUnit = dataSnapshot.child(i.name).child("unit").toString();
                                        double addVal;
                                        if (i.unit.equals(dbUnit) || i.unit.contains(dbUnit) || dbUnit.contains(i.unit)) {
                                            shopDatabase.child(i.name).child("amount").setValue(existingVal + i.amount);
                                        } else {
                                            Spoonacular.skip = true;
                                            try {
                                                new Spoonacular(getContext()).execute("convert", String.valueOf(i.amount), i.unit, dbUnit, i.name).get();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            long startTime = System.currentTimeMillis();
                                            Spoonacular.wentThrough = false;
                                            addVal = RecipesRecyclerViewAdapter.convertedAmount;
                                            if (addVal != -1)
                                                shopDatabase.child(i.name).child("amount").setValue(existingVal + addVal);
                                            else {
                                                i.name += " :" + i.unit;
                                                shopDatabase.child(i.name).child("amount").setValue(i.amount);
                                                shopDatabase.child(i.name).child("unit").setValue(i.unit);
                                            }
                                        }
                                    } else {
                                        shopDatabase.child(i.name).child("amount").setValue(i.amount);
                                        shopDatabase.child(i.name).child("unit").setValue(i.unit);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });

                    }
                });

        del = (Button) shopview.findViewById(R.id.deleteCheckedButton);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < MyShoppingListItemsRecyclerViewAdapter.checked.size(); i++) {
                    String toDel = MyShoppingListItemsRecyclerViewAdapter.checked.get(i);
                    MainActivity.mDatabase.child("shop").child(toDel).setValue(null);
                    ShoppingListItemsFragment.stringShopList.remove(toDel);
                    haveBeenDel.add(toDel);
                }
                MyShoppingListItemsRecyclerViewAdapter.checked = new ArrayList<>();
                haveBeenDel = new ArrayList<>();
                rcshopAdapter.notifyDataSetChanged();
            }
        });

        return shopview;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
