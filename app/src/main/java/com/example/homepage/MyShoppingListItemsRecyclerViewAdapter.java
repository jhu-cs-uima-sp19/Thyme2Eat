package com.example.homepage;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;


public class MyShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingListItemsRecyclerViewAdapter.ViewHolder> {

    public static ArrayList<String> checked;

    public MyShoppingListItemsRecyclerViewAdapter() {
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "at holder add");

        //final Button add;
        final View shopview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglistitems, parent, false);
        return new ViewHolder(shopview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w("myApp", "atBindView");
        ((ViewHolder) holder).bindView(position);

    }

    @Override
    public int getItemCount() {
        Log.w("here", "" + ShoppingListItemsFragment.stringShopList.size());
        return ShoppingListItemsFragment.stringShopList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final Button deleteButton;
        public final CheckBox check;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.contentitem);
            checked = new ArrayList<>();

            check = (CheckBox) view.findViewById(R.id.checkList);
            check.setChecked(false);
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        String deleteKey = ShoppingListItemsFragment.stringShopList.get(getAdapterPosition());
                        deleteKey = deleteKey.substring(0, deleteKey.indexOf(':'));
                        checked.add(deleteKey);
                    } else {
                        String deleteKey = ShoppingListItemsFragment.stringShopList.get(getAdapterPosition());
                        deleteKey = deleteKey.substring(0, deleteKey.indexOf(':'));
                        checked.remove(deleteKey);
                    }
                }
            });

            deleteButton = (Button) view.findViewById(R.id.deleteitem);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != -1) {
                        String deleteKey = ShoppingListItemsFragment.stringShopList.get(getAdapterPosition());
                        deleteKey = deleteKey.substring(0, deleteKey.indexOf(':'));
                        MainActivity.mDatabase.child("shop").child(deleteKey).setValue(null);
                        ShoppingListItemsFragment.stringShopList.remove(getAdapterPosition());
                        MyShoppingListItemsRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                    }
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
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}