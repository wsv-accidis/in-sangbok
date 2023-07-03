package se.insektionen.songbook.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Songbook;
import se.insektionen.songbook.services.Repository;
import se.insektionen.songbook.services.RepositoryResultHandler;
import se.insektionen.songbook.utils.AndroidUtils;

/**
 * Fragment which displays information about the app and the current songbook.
 */
public final class AboutFragment extends Fragment implements MainActivity.HasNavigationItem {
	@Override
	public int getItemId() {
		return R.id.nav_about;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);

		int[] paragraphs = new int[]{R.id.about_para1, R.id.about_para2, R.id.about_para3};
		for (int id : paragraphs) {
			TextView textView = (TextView) view.findViewById(id);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
		}

		TextView appInfoView = (TextView) view.findViewById(R.id.about_app_info);
		appInfoView.setText(String.format(getString(R.string.about_app_info), AndroidUtils.getAppVersionName(getContext())));

		final TextView songbookInfoView = (TextView) view.findViewById(R.id.about_songbook_info);

		Repository repository = new Repository();
		repository.getSongbook(new RepositoryResultHandler<Songbook>() {
			@Override
			public void onError(int errorMessage) {
			}

			@Override
			public void onSuccess(Songbook songbook) {
				songbookInfoView.setText(String.format(getString(R.string.about_songbook_info), songbook.description(), songbook.updated()));
			}
		}, false);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		AndroidUtils.hideSoftKeyboard(getContext(), getView());
	}
}
