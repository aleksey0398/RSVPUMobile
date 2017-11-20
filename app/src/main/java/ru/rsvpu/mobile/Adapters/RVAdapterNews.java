package ru.rsvpu.mobile.Adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.rsvpu.mobile.Activity.NewsActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.ItemNews;

/**
 * Created by aleksej on 10.08.16.
 * 11.10.2017
 */
public class RVAdapterNews extends RecyclerView.Adapter<RVAdapterNews.newsViewHolder> {

    private Context context;
    private List<ItemNews> newsList = new ArrayList<>();

    public RVAdapterNews(Context context) {
        this.context = context;
    }

    @Override
    public newsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_news, parent, false);
        return new newsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final newsViewHolder holder, final int position) {
        final ItemNews news = newsList.get(position);

        holder.time.setText(news.getDate());
        holder.news.setText(news.getNews());
        holder.title.setText(news.getTitle());

        //если наша картинка стирается из памяти, то загружаем её снова
        if (news.getPicture() == null) {

//            print("NoBitmap!");
            new Thread(() -> {

                try {
                    URL url = new URL(news.getUrl_picture());
                    final Bitmap picture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ((Activity) context).runOnUiThread(() -> {
                        holder.imageView.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //05.11.17 ошибка неизвестного рода API 19
                                try {
                                    holder.imageView.setVisibility(View.INVISIBLE);
                                    holder.imageView.setImageBitmap(picture);
                                    holder.imageView.setVisibility(View.VISIBLE);
                                    holder.imageView.animate().alpha(1f).setDuration(300).start();
                                    newsList.get(position).setPicture(picture);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();


                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            holder.imageView.setImageBitmap(news.getPicture());
        }


        holder.listener.setUrl(news.getUrl_news());
        holder.listener.setTitle(news.getTitle());
        holder.listener.setImg(news.getPicture());


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (SaveGetFavorite.haveItemInFavoriteList(context, news.getUrl_news())) {
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.favorite.setTag("press");
//                            holder.favorite.setImageResource(R.drawable.ic_heart);
//                        }
//                    });
//
//                } else {
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.favorite.setTag("no_press");
//                            holder.favorite.setImageResource(R.drawable.ic_heart_outline);
//                        }
//                    });
//
//                }
//            }
//        }).start();


        holder.clickShared.setUrl(news.getUrl_news());
//        holder.clickFavorite.setHolder(holder, news);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    class newsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title, news, time;
        private CardView cv;
        private openFullActivity listener;
        private clickShared clickShared;
        //        private clickFavorite clickFavorite;
        private AppCompatImageView shared, favorite;

        newsViewHolder(View itemView) {
            super(itemView);
            listener = new openFullActivity();
            cv = itemView.findViewById(R.id.cardView_news);
            cv.setOnClickListener(listener);
            imageView = itemView.findViewById(R.id.news_ImageView);
            title = itemView.findViewById(R.id.news_Title);
            news = itemView.findViewById(R.id.news_News);
            time = itemView.findViewById(R.id.news_Time);
            shared = itemView.findViewById(R.id.news_shared);
            clickShared = new clickShared();
            shared.setOnClickListener(clickShared);

            favorite = itemView.findViewById(R.id.news_favorite);
            favorite.setVisibility(View.GONE);
//            clickFavorite = new clickFavorite();
//            favorite.setOnClickListener(clickFavorite);
        }
    }

    public void setContext(Context contextFrom) {
        context = contextFrom;
    }

    public void setList(List<ItemNews> list) {
        newsList = list;
        notifyDataSetChanged();
    }

    public void addArray(final ItemNews[] arrayItemNews) {
        newsList.clear();

        if (arrayItemNews != null) {

            newsList.addAll(Arrays.asList(arrayItemNews));

            notifyDataSetChanged();
        }
    }


    public void addNews(ItemNews newsToAdd) {
        newsList.add(newsToAdd);
        notifyDataSetChanged();
    }

    public boolean hasLoadingData() {
        if (newsList.size() < 1) {
            return true;
        } else {
            return false;
        }
    }

    class openFullActivity implements View.OnClickListener {
        String url;
        String title;
        Object img;

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, NewsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("nestedText", "Новости");
            context.startActivity(intent);
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImg(Object img) {
            this.img = img;
        }
    }

    //class for sharing news :)
    class clickShared implements View.OnClickListener {

        String url = "empty url";
        String text = "";

        @Override
        public void onClick(View v) {
            text = "Читайте " + url + "\nчерез приложение РГППУ https://play.google.com/store/apps/details?id=ru.alekseymitkin.rsvpu";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            ((Activity) context).startActivity(sendIntent);
        }

        void setUrl(String url) {
            this.url = url;
        }
    }

    //    class for click favorite
//    class clickFavorite implements View.OnClickListener {
//
//        newsViewHolder holder;
//        item_news_ad news;
//
//        @Override
//        public void onClick(View v) {
//            //change icon and add/remove item
//            print("First: " + holder.favorite.getTag());
//
//            holder.favorite.animate().scaleX((float) 1.5).scaleY((float) 1.5).setDuration(100).setListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    holder.favorite.animate().scaleX(1).scaleY(1).setDuration(100).setListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            holder.favorite.clearAnimation();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//
//                        }
//                    }).start();
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            }).start();
//
//
//            if (holder.favorite.getTag().equals("press")) {
//                //delete item
//                SaveGetFavorite.deleteItemFavorite(context, news.getUrl_news());
//
//                holder.favorite.setImageResource(R.drawable.ic_heart_outline);
//                holder.favorite.setTag("no_press");
//            } else {
//                //add item
//                SaveGetFavorite.saveItemFavorite(context, news);
//
//                holder.favorite.setImageResource(R.drawable.ic_heart);
//                holder.favorite.setTag("press");
//            }
//
//            //выполняем обновление на странице с обявлениями при изменении статуса избранности
//            if (fragment_advertisement.adapter != null) {
//                fragment_advertisement.adapter.notifyDataSetChanged();
//            }
//
//            print("Second: " + holder.favorite.getTag());
//        }
//
//        void setHolder(newsViewHolder holder, item_news_ad news) {
//            this.holder = holder;
//            this.news = news;
//        }
//    }

    private void print(Object msg) {
        String LOG_ARGS = "AdapterNews";
        Log.d(LOG_ARGS, String.valueOf(msg));
    }
}
