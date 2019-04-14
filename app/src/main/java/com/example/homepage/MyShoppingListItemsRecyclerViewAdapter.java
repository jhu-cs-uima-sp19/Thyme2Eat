package com.example.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.example.homepage.ShoppingListItemsFragment.OnListFragmentInteractionListener;
import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingListItemsRecyclerViewAdapter.ViewHolder> {

//    private final List<DummyItem> mValues;
//    private final OnListFragmentInteractionListener mListener;
    //final MyShoppingListItemsRecyclerViewAdapter rcshopAdapter = new MyShoppingListItemsRecyclerViewAdapter();


    public MyShoppingListItemsRecyclerViewAdapter() {
    }

//    public void getShopDatabase(DatabaseReference shopDatabase) {
//        shopDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.w("data", "in snap");
//                String name;
//                String num;
//                String theunit;
//                String wholeitem;
//
//                for (DataSnapshot items : dataSnapshot.getChildren()) {
//                    name = items.getKey();
//                    num = items.child("amount").getValue().toString();
//                    theunit = items.child("unit").getValue().toString();
//                    wholeitem = name + ": " + num + " " + theunit;
//                    stringShopList.add(wholeitem);
//                }
//                rcshopAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "at holder add");

        //final Button add;
        final View shopview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglistitems, parent, false);

//        add = (Button) parent.findViewById(R.id.addShopItemButton);
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(shopview.getContext());
//                dialog.setTitle("Add your shopping list item:");
//
//                final EditText nameinput = new EditText(shopview.getContext());
//                final EditText unitinput = new EditText(shopview.getContext());
//                final EditText numinput = new EditText(shopview.getContext());
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                nameinput.setLayoutParams(lp);
//                unitinput.setLayoutParams(lp);
//                numinput.setLayoutParams(lp);
//                dialog.setView(nameinput);
//                //nameinput.setText(r.time);
//                nameinput.setGravity(Gravity.CENTER_HORIZONTAL);
//
//                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    public void onClick (DialogInterface dialog, int which) {
//                        String strname = nameinput.getText().toString();
//                        String strunit = unitinput.getText().toString();
//                        String strnum = unitinput.getText().toString();
//                        MainActivity.mDatabase.child("shop").setValue(strname);
//                        MainActivity.mDatabase.child("shop").child(strname).child("unit").setValue(strunit);
//                        MainActivity.mDatabase.child("shop").child(strname).child("unit").setValue(strnum);
//                        MyShoppingListItemsRecyclerViewAdapter.this.notifyDataSetChanged();
//                        dialog.cancel();
//                    }
//                });
//                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick (DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                dialog.show();
//
//            }
//        });
//        Log.w("myApp", "atShopFrag");
//        //View shopview = inflater.inflate(R.layout.fragment_shoppinglistitems_list, container, false);
//        RecyclerView shoprec = (RecyclerView) shopview.findViewById(R.id.shoppingListID);
//        //shoprec.setLayoutManager(new LinearLayoutManager(getActivity()));
//        shoprec.setAdapter(new MyShoppingListItemsRecyclerViewAdapter());
//
//        if (stringShopList == null) {
//            stringShopList = new ArrayList<>();
//        }
//
//        DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
//        getShopDatabase(shopDatabase);
//
//
        return new ViewHolder(shopview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w("myApp", "atBindView");
        ((ViewHolder) holder).bindView(position);


//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        Log.w("here", "" + ShoppingListItemsFragment.stringShopList.size());
        return ShoppingListItemsFragment.stringShopList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView itemDesc;
        //public final TextView mIdView;
        public final TextView mContentView;
        //public DummyItem mItem;
        public final Button deleteButton;
        public final CheckBox check;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //itemDesc = (TextView) view.findViewById(R.id.PUTSOMETHINGHERE);
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.contentitem);

            check = (CheckBox) view.findViewById(R.id.checkbox_made);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deleteKey = ShoppingListItemsFragment.stringShopList.get(getAdapterPosition());
                    deleteKey = deleteKey.substring(0, deleteKey.indexOf(':'));
                    MainActivity.mDatabase.child("shop").child(deleteKey).setValue(null);
                    ShoppingListItemsFragment.stringShopList.remove(getAdapterPosition());
                    MyShoppingListItemsRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                }
            });

            deleteButton = (Button) view.findViewById(R.id.deleteitem);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deleteKey = ShoppingListItemsFragment.stringShopList.get(getAdapterPosition());
                    deleteKey = deleteKey.substring(0, deleteKey.indexOf(':'));
                    MainActivity.mDatabase.child("shop").child(deleteKey).setValue(null);
                    ShoppingListItemsFragment.stringShopList.remove(getAdapterPosition());
                    MyShoppingListItemsRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                }
            });
            if (mContentView == null) {
                Log.w("empty", "content is null");
            }
        }

        public void bindView(int position) {
            String item = ShoppingListItemsFragment.stringShopList.get(position);
            if (item != null) {
                mContentView.setText(item.substring(0, 1).toUpperCase() + item.substring(1));
            }
            //.setText(MainActivity.mealList.get(position).getDate());
            //.setText(MainActivity.mealList.get(position).getTime());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
