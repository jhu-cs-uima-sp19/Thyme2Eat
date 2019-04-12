package com.example.homepage;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.homepage.ShoppingListItemsFragment.OnListFragmentInteractionListener;
import com.example.homepage.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingListItemsRecyclerViewAdapter.ViewHolder> {

//    private final List<DummyItem> mValues;
//    private final OnListFragmentInteractionListener mListener;

    public MyShoppingListItemsRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "atList");

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglistitems, parent, false);
        return new ViewHolder(view);
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //itemDesc = (TextView) view.findViewById(R.id.PUTSOMETHINGHERE);
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.contentitem);
        }

        public void bindView(int position) {
            mContentView.setText(ShoppingListItemsFragment.stringShopList.get(position));
            //.setText(MainActivity.mealList.get(position).getDate());
            //.setText(MainActivity.mealList.get(position).getTime());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
