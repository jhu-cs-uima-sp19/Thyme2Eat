package com.example.homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        View shopview = inflater.inflate(R.layout.fragment_shoppinglistitems_list, container, false);
        RecyclerView shoprec = (RecyclerView) shopview.findViewById(R.id.shoppingListID);
        shoprec.setLayoutManager(new LinearLayoutManager(getActivity()));
        shoprec.setAdapter(rcshopAdapter);

        DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
        getShopDatabase(shopDatabase);

        add = (Button) getView().findViewById(R.id.addShopItemButton);

        // make method for adding

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                // OPEN DIALOG BOX TO ADD THE ITEM
                // MAKE DATABASE EDITS
                stringShopList.add(str);

            }
        });

        return shopview;
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

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
