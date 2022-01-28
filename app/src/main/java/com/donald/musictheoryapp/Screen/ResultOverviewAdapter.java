package com.donald.musictheoryapp.Screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.donald.musictheoryapp.Question.QuestionGroup;
import com.donald.musictheoryapp.Question.QuestionSection;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.R;

public class ResultOverviewAdapter extends RecyclerView.Adapter<ResultOverviewAdapter.ResultViewHolder>
{
    /*
     * ******************
     * NESTED CLASS START
     * ******************
     */
    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        private final ResultOverviewAdapter adapter;
        private final LayoutInflater inflater;
        private final TextView sectionNumber;
        private final TextView sectionName;
        private final TextView sectionScore;
        private final View sectionGroup;
        private final ResultOverviewScreen.OnProceedToDetailListener onProceedToDetailListener;
        private final LinearLayout groupsLinearLayout;
        private int groupIndexToDisplay;
        private QuestionArray questions;

        public ResultViewHolder(
            ResultOverviewAdapter adapter,
            LayoutInflater inflater,
            View itemView,
            ResultOverviewScreen.OnProceedToDetailListener listener
        ) {
            super(itemView);
            this.adapter = adapter;
            this.inflater = inflater;
            sectionNumber = itemView.findViewById(R.id.result_section_number);
            sectionName = itemView.findViewById(R.id.result_section_name);
            sectionScore = itemView.findViewById(R.id.result_section_score);
            sectionGroup = itemView.findViewById(R.id.result_section_group);
            groupsLinearLayout = itemView.findViewById(R.id.result_section_groups);
            groupsLinearLayout.setVisibility(View.GONE);
            onProceedToDetailListener = listener;

            sectionGroup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    groupsLinearLayout.setVisibility(
                        groupsLinearLayout.getVisibility() == View.VISIBLE ?
                            View.GONE :
                            View.VISIBLE
                    );
                }
            });


        }

        public void setQuestions(QuestionArray questions)
        {
            this.questions = questions;
        }

        public void bindData(int sectionIndex)
        {
            QuestionSection section = questions.sectionAt(sectionIndex);
            sectionNumber.setText(String.valueOf(section.number));
            sectionName.setText(section.name);
            sectionScore.setText(section.points() + "/" + section.maxPoints());
            this.groupIndexToDisplay = sectionIndex;

            groupsLinearLayout.removeAllViews();
            QuestionGroup[] groups = section.groups;
            for(int i = 0, groupsLength = groups.length; i < groupsLength; i++)
            {
                QuestionGroup group = groups[i];
                View groupItem = inflater.inflate(
                    R.layout.item_result_question_group, groupsLinearLayout, false
                );
                ((TextView) groupItem.findViewById(R.id.result_group_name)).setText(group.name);
                ((TextView) groupItem.findViewById(R.id.result_group_score)).setText(
                    group.points() + "/" + group.maxPoints()
                );
                groupItem.setOnClickListener(
                    (View view) ->
                    {
                        onProceedToDetailListener.onProceedToDetail(
                            questions, questions.groupIndexOf(group)
                        );
                    }
                );
                groupsLinearLayout.addView(groupItem);
            }
        }
    }
    /*
     * ****************
     * NESTED CLASS END
     * ****************
     */

    private final QuestionArray questions;
    private final ResultOverviewScreen.OnProceedToDetailListener onProceedToDetailListener;

    public ResultOverviewAdapter(
        QuestionArray questions,
        ResultOverviewScreen.OnProceedToDetailListener listener
    ) {
        this.questions = questions;
        onProceedToDetailListener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_result_overview, parent, false);
        return new ResultViewHolder(this, inflater, itemView, onProceedToDetailListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position)
    {
        holder.setQuestions(questions); // TODO: CALL THIS WHEN SETTING QUESTIONS IN ADAPTER, NOT HERE
        holder.bindData(position);
    }

    @Override
    public int getItemCount()
    {
        return questions.sectionCount();
    }
}
