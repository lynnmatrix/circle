package com.jadenine.circle.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.image.ImagePath;
import com.jadenine.circle.ui.utils.TimeFormatUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;
import timber.log.Timber;

/**
 * Created by linym on 7/24/15.
 */
public class TopicHeader extends LinearLayout {

    public static final int COMMENTS_COUNT_IN_HEADER = 5;
    @InjectView(R.id.avatar)
    ImageView avatarView;

    @InjectView(R.id.date)
    TextView dateView;

    @InjectView(R.id.content)
    TextView contentView;

    @InjectView(R.id.content_image)
    ImageView imageView;

    @InjectView(R.id.comment_flag)
    ImageView commentFlagView;

    @InjectView(R.id.message_count)
    TextView messageCountView;

    @InjectView((R.id.topic_header_comments))
    LinearLayout commentsView;

    private OnAvatarClickListener avatarClickListener;

    public interface OnAvatarClickListener{
        void onClick(Context context);
    }

    public TopicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnAvatarClickListener(OnAvatarClickListener listener) {
        this.avatarClickListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bind(final Bomb rootBomb, int commentCount, Drawable errorDrawable, AvatarBinder avatarBinder) {
        if(null == rootBomb) {
            Timber.wtf("Try to bind topic without root.");
            return;
        }
        setTag(rootBomb.getId());

        avatarView.setImageResource(avatarBinder.getAvatar(rootBomb.getFrom(), rootBomb.getRootMessageId()));

        dateView.setText(getFormattedTime(rootBomb.getTimestamp()));
        contentView.setText(rootBomb.getContent());
        contentView.setMaxLines(Integer.MAX_VALUE);
        messageCountView.setText("" + commentCount);
        if(null != rootBomb.getImages() && rootBomb.getImages().length() > 0) {
            imageView.setVisibility(View.VISIBLE);
            final Uri imageUri = Uri.parse(rootBomb.getImages());
            Picasso.with(imageView.getContext()).load(imageUri).centerCrop().error(errorDrawable)
                    .resize(440, 220).onlyScaleDown().into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Flow.get(getContext()).set(new ImagePath(rootBomb.getImages()));
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }
        commentsView.setVisibility(GONE);
        commentsView.removeAllViews();
        commentFlagView.setEnabled(false);

    }

    public void bindTopicWithComments(Group<Bomb> topic, AvatarBinder avatarBinder) {
        contentView.setMaxLines(3);
        List<Bomb> bombs = topic.getEntities();

        boolean hasComments = bombs.size() > 1;
        commentsView.setVisibility(hasComments ? VISIBLE : GONE);
        if(hasComments) {

            if(topic.getUnread()) {
                commentFlagView.setEnabled(true);
            }
            Collections.reverse(bombs);
            int textSize = getContext().getResources().getDimensionPixelSize(R.dimen
                    .topic_comment_text_size);

            boolean needCollapse = bombs.size() >= COMMENTS_COUNT_IN_HEADER + 2;
            List<CharSequence> lines = new ArrayList<>(COMMENTS_COUNT_IN_HEADER);
            if(needCollapse) {
                CharSequence comment1 = buildComment(avatarBinder, bombs.get(1), textSize);
                lines.add(comment1);
                CharSequence comment2 = buildComment(avatarBinder, bombs.get(2), textSize);
                lines.add(comment2);

                lines.add("    ...");

                CharSequence commentSecondLast = buildComment(avatarBinder, bombs.get(bombs.size
                        () - 2), textSize);
                lines.add(commentSecondLast);
                CharSequence commentLast = buildComment(avatarBinder, bombs.get(bombs.size() - 1)
                        , textSize);
                lines.add(commentLast);
            } else {
                for (Bomb bomb : bombs.subList(1, bombs.size())) {
                    lines.add(buildComment(avatarBinder, bomb, textSize));
                }
            }

            for(CharSequence line : lines) {
                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body1);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                textView.setText(line);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                commentsView.addView(textView);
            }
        }
    }

    private CharSequence buildComment(AvatarBinder avatarBinder, Bomb bomb, float textSize) {
        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.secondary_text));

        SpannableStringBuilder atSpannableStrBuilder = new SpannableStringBuilder("@");
        atSpannableStrBuilder.setSpan(fcs, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder colonSpannableStrBuilder = new SpannableStringBuilder(": ");
        colonSpannableStrBuilder.setSpan(fcs, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int fromResId = avatarBinder.getAvatar(bomb.getFrom(), bomb.getRootMessageId());
        CharSequence fromAvatar = avatarBinder.getAvatarSpan(getContext(), fromResId, textSize);

        SpannableStringBuilder commentBuilder = new SpannableStringBuilder();
        commentBuilder.append(fromAvatar);
        if (!TextUtils.isEmpty(bomb.getTo())) {
            int toResId = avatarBinder.getAvatar(bomb.getTo(), bomb.getRootMessageId());
            CharSequence toAvatar = avatarBinder.getAvatarSpan(getContext(), toResId, textSize);

            commentBuilder.append(atSpannableStrBuilder);
            commentBuilder.append(toAvatar);
        }
        commentBuilder.append(colonSpannableStrBuilder);
        commentBuilder.append(bomb.getContent());

        return commentBuilder;
    }

    private String getFormattedTime(long timestamp) {
        return TimeFormatUtils.getFormattedTime(timestamp);
    }

    @OnClick(R.id.avatar)
    public void onAvatarClicked(){
        if(null != avatarClickListener) {
            avatarClickListener.onClick(getContext());
        }
    }
}
