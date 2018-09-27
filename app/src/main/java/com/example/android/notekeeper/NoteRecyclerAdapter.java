package com.example.android.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.Viewholder>{

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null){
            return;
        }
        // Get Column Indexes from mCursor
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.note_list_item , parent , false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        NoteInfo note = mNotes.get(position);
        holder.mCourseText.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition = note.getId();
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }



    public class Viewholder extends RecyclerView.ViewHolder{

        public final TextView mCourseText;
        public final TextView mTextTitle;
        public int mCurrentPosition;

        public Viewholder(View itemView) {
            super(itemView);
            mCourseText = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(mContext , NoteActivity.class);
                    intent.putExtra(NoteInfo.NOTE_POSITION , mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
