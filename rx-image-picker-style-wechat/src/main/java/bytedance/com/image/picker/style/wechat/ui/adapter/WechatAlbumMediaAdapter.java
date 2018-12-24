package bytedance.com.image.picker.style.wechat.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.zlove.image.picker.support.model.SelectedItemCollection;
import com.zlove.image.picker.support.ui.adapter.AlbumMediaAdapter;

import bytedance.com.image.picker.style.wechat.R;

public class WechatAlbumMediaAdapter extends AlbumMediaAdapter {

    public WechatAlbumMediaAdapter(Context context, SelectedItemCollection selectedItemCollection, RecyclerView recyclerView) {
        super(context, selectedItemCollection, recyclerView);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.wechat_media_grid_item;
    }
}
