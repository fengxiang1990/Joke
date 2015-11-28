package com.app.fxa.joke.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fxa.joke.R;
import com.app.fxa.joke.activity.MyJokeActivity;
import com.app.fxa.joke.fragment.MainFragment;

import java.util.List;

/**
 * fengxiang
 */
public class JokeAdapter extends BaseAdapter {

    String TAG = JokeAdapter.class.getName();
    MainFragment mainFragment;
    Activity activity;
    List<Joke> jokes;
    LayoutInflater inflater;
    ClipboardManager clipboardManager;

    public JokeAdapter(MainFragment mainFragment, List<Joke> jokes) {
        this.mainFragment = mainFragment;
        this.jokes = jokes;
        clipboardManager = (ClipboardManager) mainFragment.getActivity().getSystemService(Service.CLIPBOARD_SERVICE);
    }

    public JokeAdapter(Activity activity, List<Joke> jokes) {
        this.activity = activity;
        this.jokes = jokes;
    }

    @Override
    public int getCount() {
        return jokes.size();
    }

    @Override
    public Object getItem(int position) {
        return jokes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (activity != null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (mainFragment != null) {
            inflater = (LayoutInflater) mainFragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.joke_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.title);
            viewHolder.contenView = (TextView) convertView.findViewById(R.id.content);
            viewHolder.btnCollect = (ImageView) convertView.findViewById(R.id.btn_collection);
            viewHolder.btnShare = (ImageView) convertView.findViewById(R.id.btn_share);
            viewHolder.cardView = (CardView) convertView.findViewById(R.id.cardview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Joke joke = jokes.get(position);
        viewHolder.titleView.setText(joke.getTitle());
        viewHolder.contenView.setText(joke.getContent());
        if (mainFragment != null) {
            viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainFragment.shareTo(joke);
                }
            });
            viewHolder.btnCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainFragment.save(joke);
                }
            });
            viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainFragment.getActivity())
                            .setTitle("选择操作")
                            .setItems(new String[]{"复制", "分享", "收藏"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        ClipData clipData = ClipData.newPlainText(joke.getTitle(), joke.getContent());
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(mainFragment.getActivity(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
                                    }
                                    if (i == 1) {
                                        mainFragment.shareTo(joke);
                                    }
                                    if (i == 2) {
                                        mainFragment.save(joke);
                                    }
                                }
                            });
                    builder.show();
                    return false;
                }
            });

        }
        if (activity != null && activity instanceof MyJokeActivity) {
            viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyJokeActivity) activity).shareTo(joke);
                }
            });
            viewHolder.btnCollect.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView titleView;
        TextView contenView;
        ImageView btnCollect;
        ImageView btnShare;
        CardView cardView;
    }
}
