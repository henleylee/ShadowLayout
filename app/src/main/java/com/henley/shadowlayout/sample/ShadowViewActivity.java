package com.henley.shadowlayout.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.henley.shadowlayout.ShadowLayout;

public class ShadowViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_view);

        ShadowLayout shadow_view = findViewById(R.id.shadow_view);
        shadow_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        RecyclerView view_recycler = findViewById(R.id.view_recycler);
        view_recycler.setLayoutManager(new LinearLayoutManager(this));
        view_recycler.setAdapter(new ShadowViewRecyclerAdapter(shadow_view));
    }

    private class ShadowViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ShadowLayout shadowLayout;

        public ShadowViewRecyclerAdapter(ShadowLayout shadowLayout) {
            this.shadowLayout = shadowLayout;
        }

        @Override
        public int getItemViewType(int position) {
            int viewType;
            switch (SeekItem.values()[position]) {
                case FOREGROUND_COLOR:
                case BACKGROUND_COLOR:
                case SHADOW_COLOR:
                    viewType = R.layout.list_item_color_select;
                    break;
                default:
                    viewType = R.layout.list_item_seek_select;
                    break;
            }
            return viewType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == R.layout.list_item_color_select) {
                return new ShadowViewColorItemHolder(inflater.inflate(R.layout.list_item_color_select, parent, false));
            }
            return new ShadowViewSeekItemHolder(inflater.inflate(R.layout.list_item_seek_select, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (holder instanceof ShadowViewSeekItemHolder) {
                ((ShadowViewSeekItemHolder) holder).bind(SeekItem.values()[position], shadowLayout);
            } else if (holder instanceof ShadowViewColorItemHolder) {
                ((ShadowViewColorItemHolder) holder).bind(SeekItem.values()[position], shadowLayout);
                ((ShadowViewColorItemHolder) holder).onClickColor = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(position);
                    }
                };
            }
        }

        @Override
        public int getItemCount() {
            return SeekItem.values().length;
        }

        class ShadowViewSeekItemHolder extends RecyclerView.ViewHolder {

            private final TextView tvTitle;
            private final TextView tvValue;
            private final SeekBar seekBar;

            public ShadowViewSeekItemHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.text_title);
                tvValue = itemView.findViewById(R.id.text_value);
                seekBar = itemView.findViewById(R.id.seek_bar);
            }

            void bind(SeekItem seekItem, final ShadowLayout shadowLayout) {
                tvTitle.setText(seekItem.title);
                seekBar.setOnSeekBarChangeListener(null);
                switch (seekItem) {
                    case SHADOW_RADIUS:
                        tvValue.setText(String.valueOf((int) shadowLayout.getShadowRadius()));
                        seekBar.setProgress((int) shadowLayout.getShadowRadius());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowRadius(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case PADDING:
                        tvValue.setText(String.valueOf(shadowLayout.getPaddingLeft()));
                        seekBar.setProgress(shadowLayout.getPaddingLeft());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setPadding(progress, progress, progress, progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case SHADOW_MARGIN:
                        tvValue.setText(String.valueOf(shadowLayout.getShadowMarginLeft()));
                        seekBar.setProgress(shadowLayout.getShadowMarginLeft());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowMargin(progress, progress, progress, progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case SHADOW_MARGIN_LEFT:
                        tvValue.setText(String.valueOf(shadowLayout.getShadowMarginLeft()));
                        seekBar.setProgress(shadowLayout.getShadowMarginLeft());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowMarginLeft(progress);
                                tvValue.setText(String.valueOf(progress));
                                shadowLayout.requestLayout();
                            }
                        });
                        break;
                    case SHADOW_MARGIN_TOP:
                        tvValue.setText(String.valueOf(shadowLayout.getShadowMarginTop()));
                        seekBar.setProgress(shadowLayout.getShadowMarginTop());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowMarginTop(progress);
                                tvValue.setText(String.valueOf(progress));
                                shadowLayout.requestLayout();
                            }
                        });
                        break;
                    case SHADOW_MARGIN_RIGHT:
                        tvValue.setText(String.valueOf(shadowLayout.getShadowMarginRight()));
                        seekBar.setProgress(shadowLayout.getShadowMarginRight());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowMarginRight(progress);
                                tvValue.setText(String.valueOf(progress));
                                shadowLayout.requestLayout();
                            }
                        });
                        break;
                    case SHADOW_MARGIN_BOTTOM:
                        tvValue.setText(String.valueOf(shadowLayout.getShadowMarginBottom()));
                        seekBar.setProgress(shadowLayout.getShadowMarginBottom());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowMarginBottom(progress);
                                tvValue.setText(String.valueOf(progress));
                                shadowLayout.requestLayout();
                            }
                        });
                        break;
                    case CORNER_RADIUS:
                        tvValue.setText(String.valueOf((int) shadowLayout.getCornerRadiusTL()));
                        seekBar.setProgress((int) shadowLayout.getCornerRadiusTL());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setCornerRadius(progress, progress, progress, progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case CORNER_RADIUS_TOP_LEFT:
                        tvValue.setText(String.valueOf((int) shadowLayout.getCornerRadiusTL()));
                        seekBar.setProgress((int) shadowLayout.getCornerRadiusTL());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setCornerRadiusTL(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case CORNER_RADIUS_TOP_RIGHT:
                        tvValue.setText(String.valueOf((int) shadowLayout.getCornerRadiusTR()));
                        seekBar.setProgress((int) shadowLayout.getCornerRadiusTR());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setCornerRadiusTR(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case CORNER_RADIUS_BOTTOM_RIGHT:
                        tvValue.setText(String.valueOf((int) shadowLayout.getCornerRadiusBR()));
                        seekBar.setProgress((int) shadowLayout.getCornerRadiusBR());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setCornerRadiusBR(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case CORNER_RADIUS_BOTTOM_LEFT:
                        tvValue.setText(String.valueOf((int) shadowLayout.getCornerRadiusBL()));
                        seekBar.setProgress((int) shadowLayout.getCornerRadiusBL());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setCornerRadiusBL(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case SHADOW_DX:
                        tvValue.setText(String.valueOf((int) shadowLayout.getShadowDx()));
                        seekBar.setProgress((int) shadowLayout.getShadowDx());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowDx(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                    case SHADOW_DY:
                        tvValue.setText(String.valueOf((int) shadowLayout.getShadowDy()));
                        seekBar.setProgress((int) shadowLayout.getShadowDy());
                        seekBar.setOnSeekBarChangeListener(new OnSeekProgressChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                super.onProgressChanged(seekBar, progress, fromUser);
                                shadowLayout.setShadowDy(progress);
                                tvValue.setText(String.valueOf(progress));
                            }
                        });
                        break;
                }
            }
        }

        private class ShadowViewColorItemHolder extends RecyclerView.ViewHolder {

            private final TextView tvColorTitle;
            private final FlexboxLayout flexboxLayout;
            private View.OnClickListener onClickColor;

            public ShadowViewColorItemHolder(@NonNull View itemView) {
                super(itemView);
                tvColorTitle = itemView.findViewById(R.id.text_color_title);
                flexboxLayout = itemView.findViewById(R.id.view_flex);
            }

            void bind(SeekItem seekItem, final ShadowLayout shadowLayout) {
                tvColorTitle.setText(seekItem.title);
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                flexboxLayout.removeAllViews();
                switch (seekItem) {
                    case SHADOW_COLOR:
                        for (final ShadowColorEnum item : ShadowColorEnum.values()) {
                            View view = createView(inflater, item.ordinal());
                            view.setBackgroundColor(Color.parseColor(item.color));
                            view.setSelected(Color.parseColor(item.color) == shadowLayout.getShadowColor());
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shadowLayout.setShadowColor(Color.parseColor(item.color));
                                    onClickColor.onClick(v);
                                }
                            });
                        }
                        break;
                    case FOREGROUND_COLOR:
                        for (final ForegroundColorEnum itemf : ForegroundColorEnum.values()) {
                            View view = createView(inflater, itemf.ordinal());
                            view.setBackgroundColor(Color.parseColor(itemf.color));
                            view.setSelected(Color.parseColor(itemf.color) == shadowLayout.getForegroundColor());
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shadowLayout.setForegroundColor(Color.parseColor(itemf.color));
                                    onClickColor.onClick(v);
                                }
                            });
                        }
                        break;
                    case BACKGROUND_COLOR:
                        for (final BackgroundColorEnum itemb : BackgroundColorEnum.values()) {
                            View view = createView(inflater, itemb.ordinal());
                            view.setBackgroundColor(Color.parseColor(itemb.color));
                            view.setSelected(Color.parseColor(itemb.color) == shadowLayout.getBackgroundColor());
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shadowLayout.setBackgroundColor(Color.parseColor(itemb.color));
                                    onClickColor.onClick(v);
                                }
                            });
                        }
                        break;
                }
            }

            private View createView(LayoutInflater inflater, int position) {
                View view = inflater.inflate(R.layout.view_item_color, flexboxLayout, false);
                view.setTag(position);
                FlexboxLayout.LayoutParams p = new FlexboxLayout.LayoutParams(dp2px(42f, itemView.getContext()), dp2px(24f, itemView.getContext()));
                int margin = dp2px(4f, itemView.getContext());
                p.setMargins(margin, margin, margin, margin);
                flexboxLayout.addView(view, p);
                return view;
            }

            private int dp2px(float dipValue, Context context) {
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                return (int) (dipValue * metrics.density + 0.5f);
            }

        }
    }
}