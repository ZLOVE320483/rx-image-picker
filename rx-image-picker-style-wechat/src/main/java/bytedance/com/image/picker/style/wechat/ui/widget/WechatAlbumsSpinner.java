package bytedance.com.image.picker.style.wechat.ui.widget;

import android.content.Context;
import android.widget.ListPopupWindow;

import com.zlove.image.picker.support.ui.widget.AlbumsSpinner;

public class WechatAlbumsSpinner extends AlbumsSpinner {

    public WechatAlbumsSpinner(Context context) {
        super(context);

        mListPopupWindow = new ListPopupWindow(context);
        mListPopupWindow.setModal(true);

        mListPopupWindow.setContentWidth(context.getResources().getDisplayMetrics().widthPixels);
        mListPopupWindow.setOnItemClickListener(((parent, view, position, id) -> {
            onItemSelected(parent.getContext(), position);
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(parent, view, position, id);
            }
        }));
    }
}
