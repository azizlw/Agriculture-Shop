package com.example.agroshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cateoryActvity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cateoryActvity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public cateoryActvity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cateoryActvity.
     */
    // TODO: Rename and change types and number of parameters
    public static cateoryActvity newInstance(String param1, String param2) {
        cateoryActvity fragment = new cateoryActvity();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cateory_actvity, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
      DatabaseReference Dataref = FirebaseDatabase.getInstance().getReference().child("Category");
        // Inflate the layout for this fragment
        FirebaseRecyclerAdapter<Category,CatHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Category, CatHolder>(
                        Category.class,
                        R.layout.single_view,
                        CatHolder.class,
                        Dataref
                ) {
                    @Override
                    protected void populateViewHolder(CatHolder catHolder, final Category category, int i) {
                        catHolder.setdetails(getActivity(), category.getName(), category.getImage());
                        catHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("working","good");
                                Intent intent = new Intent(getContext(),categoryproduct.class);
                                intent.putExtra("category",category.getName());
                                startActivity(intent);
                            }
                        });
                    }

                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        return view;
    }
   
    public void onBackPressed() {
        Intent BackpressedIntent = new Intent();
        BackpressedIntent .setClass(getContext(),HomeActivity.class);
        BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent);
    }
}