package bytedance.com.image.picker.style.wechat.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zlove.image.picker.support.entity.Album;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.model.AlbumMediaCollection;
import com.zlove.image.picker.support.model.SelectedItemCollection;
import com.zlove.image.picker.support.ui.adapter.AlbumMediaAdapter;
import com.zlove.image.picker.support.ui.widget.MediaGridInset;
import com.zlove.image.picker.support.utils.UIUtils;

import bytedance.com.image.picker.style.wechat.R;
import bytedance.com.image.picker.style.wechat.ui.adapter.WechatAlbumMediaAdapter;

public class WechatImageListGridFragment extends Fragment implements AlbumMediaAdapter.CheckStateListener,
        AlbumMediaAdapter.OnMediaClickListener, AlbumMediaCollection.AlbumMediaCallbacks {

    private static final String EXTRA_ALBUM = "extra_album";

    private AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private RecyclerView mRecyclerView;
    private AlbumMediaAdapter mAdapter;
    private SelectionProvider mSelectionProvider;
    private AlbumMediaAdapter.CheckStateListener mCheckStateListener;
    private AlbumMediaAdapter.OnMediaClickListener mOnMediaClickListener;

    public static WechatImageListGridFragment instance(Album album) {
        WechatImageListGridFragment fragment = new WechatImageListGridFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    public void injectDependencies(SelectionProvider selectionProvider,
                                   AlbumMediaAdapter.CheckStateListener checkStateListener,
                                   AlbumMediaAdapter.OnMediaClickListener mediaClickListener) {
        if (null != selectionProvider) {
            this.mSelectionProvider = selectionProvider;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        this.mCheckStateListener = checkStateListener;
        this.mOnMediaClickListener = mediaClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), SelectionSpec.getInstance().themeId);
        LayoutInflater localInflater = inflater
                .cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_media_selection, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = getArguments().getParcelable(EXTRA_ALBUM);

        mAdapter = new WechatAlbumMediaAdapter(getContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        if (selectionSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(getContext(), selectionSpec.gridExpectedSize);
        } else {
            spanCount = selectionSpec.spanCount;
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mAlbumMediaCollection.onCreate(getActivity(), this);
        mAlbumMediaCollection.load(album, selectionSpec.capture);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
    }

    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdate() {
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick(getArguments().getParcelable(EXTRA_ALBUM), item, adapterPosition);
        }
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAdapter.swapCursor(null);
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }
}
