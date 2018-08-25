package sheldon.com.android.gohomev2.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import sheldon.com.android.gohomev2.R;

public class ExpandableButton {

    public static void giveActionOnClick(final ImageButton imageButton, final RecyclerView recyclerView) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    imageButton.setImageResource(R.drawable.ic_expand_more_white_36dp);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    imageButton.setImageResource(R.drawable.ic_expand_less_white_36dp);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
