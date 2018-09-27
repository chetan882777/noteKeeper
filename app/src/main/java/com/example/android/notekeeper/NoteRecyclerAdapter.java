package com.example.android.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.Viewholder>{

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

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
        mCoursePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
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
        mCursor.moveToPosition(position);
        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        holder.mCourseText.setText(course);
        holder.mTextTitle.setText(noteTitle);
        holder.mId = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }



    public class Viewholder extends RecyclerView.ViewHolder{

        public final TextView mCourseText;
        public final TextView mTextTitle;
        public int mId;

        public Viewholder(View itemView) {
            super(itemView);
            mCourseText = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(mContext , NoteActivity.class);
                    intent.putExtra(NoteInfo.NOTE_POSITION , mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
