package ru.rsvpu.mobile.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import ru.rsvpu.mobile.Activity.TutorialActivity;
import ru.rsvpu.mobile.R;

/**
 * Created by aleksej
 * on 02.11.2017.
 */

public class FragmentPageTutorial extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    static final int[] pictures = {
            R.drawable.logo_512,
            R.drawable.ic_tutorial_speed,
            R.drawable.ic_tutorial_internet_off,
            R.drawable.ic_tutorial_notification,
            R.drawable.ic_tutorial_vk};

    static final String[] titles = {
            "",
            "Приложение в 30 раз быстрее сайта",
            "Расписание доступно без сети",
            "Получайте уведомления о парах на текущий день.\nНу и остальные новости...",
            "Авторизуйтесь через ВК, нажав на иконку"
    };

    private static final String[] sMyScope = new String[]{
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.DOCS
    };

    private final String LOG_ARGS = "FragmentPageTutorial";
    private ImageView image;
    private TextView txtTitle;

    public static FragmentPageTutorial newInstance(int page) {
        FragmentPageTutorial pageFragment = new FragmentPageTutorial();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tutorial_pager, container, false);
        initView(v);

        image.setImageResource(pictures[getArguments().getInt(ARGUMENT_PAGE_NUMBER)]);
        txtTitle.setText(titles[getArguments().getInt(ARGUMENT_PAGE_NUMBER)]);

        if (getArguments().getInt(ARGUMENT_PAGE_NUMBER) == 0) {
            //animation
//            image.getLayoutParams().height = 900;
//            image.getLayoutParams().width = 900;
//            image.requestLayout();
            if (!TutorialActivity.showFirst) {
                image.setAlpha(0f);
                image.setTranslationY(100);
                image.animate().setDuration(2000).alpha(1f).translationY(0).setStartDelay(700).start();
                TutorialActivity.showFirst = true;
            }
        } else if (getArguments().getInt(ARGUMENT_PAGE_NUMBER) == 4) {
            image.setClickable(true);
            image.setOnClickListener(v1 -> {
                VKSdk.login(getActivity(), sMyScope);
            });
        }

        Log.d(LOG_ARGS, String.valueOf(getArguments().getInt(ARGUMENT_PAGE_NUMBER)));
        return v;
    }

    void initView(View view) {
        image = view.findViewById(R.id.fragment_tutorial_img);
        txtTitle = view.findViewById(R.id.fragment_tutorial_title);
    }
}
