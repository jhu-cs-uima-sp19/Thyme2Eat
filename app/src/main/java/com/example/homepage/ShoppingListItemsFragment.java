package com.example.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.util.ArrayList;


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
    public static ArrayList<String> stringShopList;
    final MyShoppingListItemsRecyclerViewAdapter rcshopAdapter = new MyShoppingListItemsRecyclerViewAdapter();
    private OnListFragmentInteractionListener mListener;
    private Button add;

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
                Log.w("data", "in snap");
                String name;
                String num;
                String theunit;
                String wholeitem;

                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    name = items.getKey();
                    num = items.child("amount").getValue().toString();
                    theunit = items.child("unit").getValue().toString();
                    wholeitem = name + ": " + num + " " + theunit;
                    stringShopList.add(wholeitem);
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
        Log.w("myApp", "atShopFrag");
        final View shopview = inflater.inflate(R.layout.fragment_shoppinglistitems_list, container, false);
        RecyclerView shoprec = (RecyclerView) shopview.findViewById(R.id.shoppingListID);
        shoprec.setLayoutManager(new LinearLayoutManager(getActivity()));
        shoprec.setAdapter(rcshopAdapter);

        DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
        getShopDatabase(shopDatabase);

        add = (Button) shopview.findViewById(R.id.addShopItemButton);
        if (add == null) {
            Log.w("add is", "null");
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("in on click", "here");
                AlertDialog.Builder dialog = new AlertDialog.Builder(shopview.getContext());
                dialog.setTitle("Add your shopping list item:");

                final EditText nameinput = new EditText(shopview.getContext());

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                nameinput.setLayoutParams(lp);
                dialog.setView(nameinput);
                nameinput.setGravity(Gravity.CENTER_HORIZONTAL);


                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int which) {
                        String strname = nameinput.getText().toString();
                        MainActivity.mDatabase.child("shop").setValue(strname);
                        rcshopAdapter.notifyDataSetChanged();
                        dialog.cancel();

                        // TO BE USED FOR SECOND SPRINT!!

//                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(shopview.getContext());
//
//                        dialog2.setTitle("What unit?");
//
//                        final EditText unitinput = new EditText(shopview.getContext());
//
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.MATCH_PARENT);
//                        unitinput.setLayoutParams(lp);
//                        dialog2.setView(unitinput);
//                        unitinput.setGravity(Gravity.CENTER_HORIZONTAL);
//
//
//                        dialog2.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                            public void onClick (DialogInterface dialog2, int which) {
//                                String strunit = unitinput.getText().toString();
//                                MainActivity.mDatabase.child("shop").setValue(strunit);
//                                rcshopAdapter.notifyDataSetChanged();
//                                dialog2.cancel();
//                                AlertDialog.Builder dialog3 = new AlertDialog.Builder(shopview.getContext());
//                                dialog3.setTitle("How many?");
//
//                                final EditText numinput = new EditText(shopview.getContext());
//
//                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                        LinearLayout.LayoutParams.MATCH_PARENT,
//                                        LinearLayout.LayoutParams.MATCH_PARENT);
//                                numinput.setLayoutParams(lp);
//                                dialog3.setView(unitinput);
//                                numinput.setGravity(Gravity.CENTER_HORIZONTAL);
//
//
//                                dialog3.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                                    public void onClick (DialogInterface dialog3, int which) {
//                                        String strnum = numinput.getText().toString();
//                                        MainActivity.mDatabase.child("shop").setValue(strnum);
//                                        rcshopAdapter.notifyDataSetChanged();
//                                        dialog3.cancel();
//                                    }
//                                });
//                                dialog3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick (DialogInterface dialog3, int which) {
//                                        dialog3.cancel();
//                                    }
//                                });
//                                dialog3.show();
//
//                            }
//                        });
//                        dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            public void onClick (DialogInterface dialog2, int which) {
//                                dialog2.cancel();
//                            }
//                        });
//                        dialog2.show();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();

                // Second dialog box



                // Third dialog box

                }
            });
        //make method for adding

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
