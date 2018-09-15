package com.example.android.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.Viewholder>{

    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCourses = courses;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.course_list_item , parent , false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        CourseInfo course = mCourses.get(position);
        holder.mCourseText.setText(course.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }



    public class Viewholder extends RecyclerView.ViewHolder{

        public final TextView mCourseText;
        public int mCurrentPosition;

        public Viewholder(View itemView) {
            super(itemView);
            mCourseText = (TextView) itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v , mCourseText.getText() , Snackbar.LENGTH_LONG ).show();
                }
            });
        }
    }
}
