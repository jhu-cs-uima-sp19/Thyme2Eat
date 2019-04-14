package com.example.homepage;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//import com.example.homepage.ShoppingListItemsFragment.OnListFragmentInteractionListener;
import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
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
        Log.w("myApp", "atList");

        View shopview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglistitems, parent, false);
//
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //itemDesc = (TextView) view.findViewById(R.id.PUTSOMETHINGHERE);
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.contentitem);
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
            mContentView.setText(item.substring(0, 1).toUpperCase() + item.substring(1));
            //.setText(MainActivity.mealList.get(position).getDate());
            //.setText(MainActivity.mealList.get(position).getTime());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
