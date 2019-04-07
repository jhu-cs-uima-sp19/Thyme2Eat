package com.example.homepage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    public RecipesRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "atList");
        /*
        mealList = new ArrayList<Meal>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("plan");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date;
                String time;
                for (DataSnapshot dates : dataSnapshot.getChildren()) {
                    date = dates.getKey();
                    for (DataSnapshot meal : dates.getChildren()) {
                        time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + date);
                        Meal m = new Meal(date, time);
                        mealList.add(m);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipeschedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w("myApp", "atBindView");
        ((ViewHolder) holder).bindView(position);

        /*
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        Log.w("here", "" + MainActivity.mealList.size());
        return MainActivity.mealList.size();
        /*
        if (mealList != null) {
            Log.w("myApp", "atItemCount");
            return mealList.size();
        } else {
            Log.w("myApp", "atItemCount Zero");
            return 1;
        }
        */
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public final TextView dateView;
        public final TextView timeView;
        public final ImageView image;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateView = (TextView) view.findViewById(R.id.dateText);
            timeView = (TextView) view.findViewById(R.id.timeText);
            image = (ImageView) view.findViewById(R.id.image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), ViewRecipe.class);
                    intent.putExtra("index", getAdapterPosition());
                    mView.getContext().startActivity(intent);
                }
            });
            view.setOnClickListener(this);
        }

        public void bindView(int position) {
            dateView.setText(MainActivity.mealList.get(position).getDate());
            timeView.setText(MainActivity.mealList.get(position).getTime());
        }

        public void onClick(View view) {

        }

    }
}
