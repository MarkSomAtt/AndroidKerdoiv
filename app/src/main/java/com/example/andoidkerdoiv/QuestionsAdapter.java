package com.example.andoidkerdoiv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> implements Filterable {
    private ArrayList<Questions> mQuestionsData=new ArrayList<>();
    private ArrayList<Questions> mQuestionsDataAll=new ArrayList<>();
    private Context context;
    private int lastPosition=-1;

    QuestionsAdapter(Context context, ArrayList<Questions> itemsData){
        this.mQuestionsData=itemsData;
        this.mQuestionsDataAll=itemsData;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(QuestionsAdapter.ViewHolder holder, int position) {
        Questions currentItem=mQuestionsData.get(position);

        holder.bndTo(currentItem);

        if (holder.getAdapterPosition()> lastPosition){
            Animation animation= AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition=holder.getAdapterPosition();
        }

    }

    @Override
    public int getItemCount() {
        return mQuestionsData.size();
    }

    @Override
    public Filter getFilter() {
        return QuestionsFilter;
    }

    private Filter QuestionsFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Questions> filteredList=new ArrayList<>();
            FilterResults results=new FilterResults();

            if (charSequence==null ||charSequence.length()==0){
                results.count=mQuestionsDataAll.size();
                results.values=mQuestionsDataAll;
            }else{
                String filterPattern= charSequence.toString().toLowerCase().trim();

                for (Questions item: mQuestionsDataAll){
                    if (item.getQuestion().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count=filteredList.size();
                results.values=filteredList;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mQuestionsData=(ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mQuestion;
        private TextView mAnswer1;
        private TextView mAnswer2;
        private TextView mAnswer3;
        private TextView mAnswer4;
        private TextView mAnswer5;
        private RatingBar mRatingbar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mQuestion =itemView.findViewById(R.id.itemQuestion);
             mAnswer1 =itemView.findViewById(R.id.answerOneRadioButton);
             mAnswer2 =itemView.findViewById(R.id.answerTwoRadioButton);
             mAnswer3 =itemView.findViewById(R.id.answerThreeRadioButton);
             mAnswer4 =itemView.findViewById(R.id.answerFourRadioButton);
             mAnswer5 =itemView.findViewById(R.id.answerFiveRadioButton);
             mRatingbar =itemView.findViewById(R.id.ratingBar);


        }

        public void bndTo(Questions currentItem) {
            mQuestion.setText(currentItem.getQuestion());
            mAnswer1.setText(currentItem.getAnswer1());
            mAnswer2.setText(currentItem.getAnswer2());
            mAnswer3.setText(currentItem.getAnswer3());
            mAnswer4.setText(currentItem.getAnswer4());
            mAnswer5.setText(currentItem.getAnswer5());
            mRatingbar.setRating(currentItem.getRated());

            itemView.findViewById(R.id.kitolt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activity","clicked");
                    ((questionnaireActiviti)context).updateAlertIcon(currentItem);

                }
            });
            itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((questionnaireActiviti)context).deleteItem(currentItem);
                }
            });

        }
    }

}

