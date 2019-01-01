package bytedance.com.image.picker.style.wechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zlove.image.picker.support.entity.Album;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.model.AlbumCollection;
import com.zlove.image.picker.support.model.SelectedItemCollection;
import com.zlove.image.picker.support.ui.AlbumPreviewActivity;
import com.zlove.image.picker.support.ui.BasePreviewActivity;
import com.zlove.image.picker.support.ui.adapter.AlbumMediaAdapter;
import com.zlove.image.picker.support.ui.widget.AlbumsSpinner;
import com.zlove.picker.core.entity.Result;
import com.zlove.picker.core.ui.ActivityPickerViewController;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;
import com.zlove.picker.core.ui.IGalleryCustomPickerView;

import java.util.ArrayList;

import bytedance.com.image.picker.style.wechat.R;
import bytedance.com.image.picker.style.wechat.ui.adapter.WechatAlbumsAdapter;
import bytedance.com.image.picker.style.wechat.ui.widget.WechatAlbumsSpinner;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class WechatImagePickerFragment extends Fragment implements IGalleryCustomPickerView,
        AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener,
        View.OnClickListener, WechatImageListGridFragment.SelectionProvider,
        AlbumMediaAdapter.OnMediaClickListener, AlbumMediaAdapter.CheckStateListener {
    public static final String EXTRA_ORIGINAL_IMAGE = "EXTRA_ORIGINAL_IMAGE";

    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private AlbumsSpinner mAlbumsSpinner;
    private CursorAdapter mAlbumsAdapter;

    private PublishSubject<Result> publishSubject = PublishSubject.create();
    private SelectedItemCollection mSelectedCollection;

    private TextView mButtonPreview;
    private TextView mButtonApply;
    private RadioButton mRadioButton;
    private ImageView mButtonBack;

    private View mContainer;
    private View mEmptyView;

    private boolean imageOriginalMode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), SelectionSpec.getInstance().themeId);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        return localInflater.inflate(R.layout.fragment_picker_wechat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSelectedCollection = new SelectedItemCollection(getContext());

        mButtonPreview = view.findViewById(R.id.button_preview);
        mButtonApply = view.findViewById(R.id.button_apply);
        mRadioButton = view.findViewById(R.id.rb_original);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        mRadioButton.setOnClickListener(this);

        mContainer = view.findViewById(R.id.container);
        mEmptyView = view.findViewById(R.id.empty_view);

        mButtonBack = view.findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(this);

        mSelectedCollection.onCreate(savedInstanceState);
        updateBottomToolbar();

        mAlbumsAdapter = new WechatAlbumsAdapter(getContext(), null, false);
        mAlbumsSpinner = new WechatAlbumsSpinner(getContext());
        mAlbumsSpinner.setOnItemSelectedListener(this);
        mAlbumsSpinner.setSelectedTextView(view.findViewById(R.id.selected_album));
        mAlbumsSpinner.setPopupAnchorView(view.findViewById(R.id.bottom_toolbar));
        mAlbumsSpinner.setAdapter(mAlbumsAdapter);
        mAlbumCollection.onCreate(getActivity(), this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    @Override
    public void display(FragmentActivity fragmentActivity, int viewContainer, ICustomPickerConfiguration configuration) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(getTag());
        if (fragment == null) {
            if (viewContainer != 0)
                fragmentManager.beginTransaction().add(viewContainer, this, getTag()).commit();
            else
                throw new IllegalArgumentException(
                        "the viewContainer == 0, please configrate the containerViewId in the @Gallery annotation."
                );
        }
    }

    @Override
    public Observable<Result> pickImage() {
        publishSubject = PublishSubject.create();
        return publishSubject;
    }

    public void closure() {
        if (getActivity() instanceof WechatImagePickerActivity) {
            ((WechatImagePickerActivity) getActivity()).closure();
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        }
        SelectionSpec.getInstance().onFinished();
    }

    @Override
    public void onAlbumLoad(Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);
        // select default album.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            cursor.moveToPosition(mAlbumCollection.currentSelection);
            mAlbumsSpinner.setSelection(getContext(),
                    mAlbumCollection.currentSelection);
            Album album = Album.valueOf(cursor);
            if (album.isAll) {
                album.addCaptureCount();
            }
            onAlbumSelected(album);
        });
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        if (album.isAll) {
            album.addCaptureCount();
        }
        onAlbumSelected(album);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updateBottomToolbar() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.button_apply_default));
        } else if (selectedCount == 1 && SelectionSpec.instance.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setText(R.string.button_apply_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_apply, selectedCount));
        }
    }

    private void onAlbumSelected(Album album) {
        if (album.isAll && album.isEmpty) {
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            WechatImageListGridFragment fragment = WechatImageListGridFragment.instance(album);
            fragment.injectDependencies(this, this, this);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, WechatImageListGridFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumCollection.onDestory();
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        Intent intent = new Intent(getContext(), WechatAlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.dataWithBundle);
        startActivityForResult(intent, WechatImagePickerActivity.REQUEST_CODE_PREVIEW);
    }

    @Override
    public void onUpdate() {
        updateBottomToolbar();
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonPreview) {
            Intent intent = new Intent(getContext(), WechatSelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.dataWithBundle);
            startActivityForResult(intent, WechatImagePickerActivity.REQUEST_CODE_PREVIEW);
        } else if (v == mButtonApply) {
            emitSelectUri();
        } else if (v == mButtonBack) {
            getActivity().onBackPressed();
        } else if (v == mRadioButton) {
            switchImageOriginalMode();
        }
    }

    private void switchImageOriginalMode() {
        boolean original = !imageOriginalMode;
        this.imageOriginalMode = original;
        this.mRadioButton.setChecked(original);
    }

    private void emitSelectUri() {
        ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
        for (Uri uri : selectedUris) {
            publishSubject.onNext(instanceResult(uri));
        }
        endPickImage();
    }

    private void endPickImage() {
        publishSubject.onComplete();
        closure();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == WechatImagePickerActivity.REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {  // apply event
                if (selected != null) {
                    for (Item item : selected) {
                        if (getActivity() instanceof WechatImagePickerActivity) {
                            ActivityPickerViewController.getInstance().emitResult(instanceResult(item.contentUri));
                        } else {
                            publishSubject.onNext(instanceResult(item.contentUri));
                        }
                    }
                }
                closure();
            } else {         // back event
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment weChatListFragment = getChildFragmentManager().findFragmentByTag(WechatImageListGridFragment.class.getSimpleName());
                if (weChatListFragment instanceof WechatImageListGridFragment) {
                    ((WechatImageListGridFragment) weChatListFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        }
    }

    private Result instanceResult(Uri uri) {
        return new Result.Builder(uri)
                .putBooleanExtra(EXTRA_ORIGINAL_IMAGE, imageOriginalMode)
                .build();
    }
}
