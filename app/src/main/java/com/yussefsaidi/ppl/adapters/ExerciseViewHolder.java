package com.yussefsaidi.ppl.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yussefsaidi.ppl.R;
import com.yussefsaidi.ppl.models.Exercise;
import com.yussefsaidi.ppl.persistence.ExerciseRepository;

public class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "ExerciseViewHolder";
    private static final int EDIT_MODE_DISABLED = 0;
    private static final int EDIT_MODE_ENABLED = 1;


    TextView mViewName, mViewSets, mViewReps;
    EditText mEditName, mEditSets, mEditReps;

    ExerciseRepository mExerciseRepository;
    Exercise mExercise;
    private LinearLayout mSubItem;
    private Button mEditButton;
    private ImageButton mCheckContainer;


    View exerciseItem;
    private int mMode;
    Activity activity;


    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);

        // For editing
        mViewName = itemView.findViewById(R.id.exercise_text_name);
        mEditName = itemView.findViewById(R.id.exercise_edit_name); // GONE by default
        mViewSets = itemView.findViewById(R.id.exercise_sets);
        mEditSets = itemView.findViewById(R.id.exercise_edit_sets); // GONE by default
        mViewReps = itemView.findViewById(R.id.exercise_reps);
        mEditReps = itemView.findViewById(R.id.exercise_edit_reps); // GONE by default



        mSubItem = itemView.findViewById(R.id.exercise_subinfo);
        mEditButton = itemView.findViewById(R.id.edit_name_button);
        mCheckContainer = itemView.findViewById(R.id.check_container);
        exerciseItem = itemView.findViewById(R.id.exercise_item);
        exerciseItem.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mCheckContainer.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        activity = getActivity(view);
        if(view.getId() == exerciseItem.getId() && mMode != EDIT_MODE_ENABLED){
            if(mSubItem.getVisibility() == View.VISIBLE){
                mSubItem.setVisibility(View.GONE);
            } else
                mSubItem.setVisibility(View.VISIBLE);
        }

        if (view.getId() == mEditButton.getId()) {
            enableEditMode(activity);
        }
        // Check pressed in edit mode
        if (view.getId() == mCheckContainer.getId()) {
            disableEditMode(activity);
            saveChanges(activity);
        }
        // When edit mode is enabled, clicking anywhere results in disabling edit mode

    }


    private void enableEditMode(Activity activity) {
        //Make all 3 edit texts visible
        mMode = EDIT_MODE_ENABLED;
        mViewName.setVisibility(View.GONE);
        mViewReps.setVisibility(View.GONE);
        mViewSets.setVisibility(View.GONE);
        mEditName.setVisibility(View.VISIBLE);
        mEditSets.setVisibility(View.VISIBLE);
        mEditReps.setVisibility(View.VISIBLE);


        mCheckContainer.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.GONE);
        mEditName.requestFocus();
    }

    private void disableEditMode(Activity activity) {
        mMode = EDIT_MODE_DISABLED;

        mEditName.setVisibility(View.GONE);
        mEditSets.setVisibility(View.GONE);
        mEditReps.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.GONE);

        mViewName.setVisibility(View.VISIBLE);
        mViewSets.setVisibility(View.VISIBLE);
        mViewReps.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.VISIBLE);

        hideKeyboard(activity);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void onBackPressed(Activity activity){
        if(mMode == EDIT_MODE_ENABLED){
            onClick(mCheckContainer);
        }
        else {
            activity.onBackPressed();
        }
    }

    private void saveChanges(Activity activity){
        mExerciseRepository = new ExerciseRepository(activity);
        mExercise.setName(mEditName.getText().toString());
        mExercise.setSets(mEditSets.getText().toString());
        mExercise.setRepetitions(mEditReps.getText().toString());
        mExerciseRepository.updateExerciseTask(mExercise);
        Log.d(TAG, "saveChanges: UPDATE ITEM");
    }
}
