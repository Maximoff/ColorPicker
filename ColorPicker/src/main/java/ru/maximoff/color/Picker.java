package ru.maximoff.color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Picker {
	private OnColorSelect selectListener;
	private String title;
	private int selectedColor;
	private Context context;

	public Picker(Context ctx) {
		this.context = ctx;
		this.title = ctx.getString(R.string.maximoff_picker_title);
		this.selectedColor = Color.BLACK;
	}

	public Picker setColor(String hexColor) {
		try {
			this.selectedColor = Color.parseColor(hexColor);
		} catch (Exception e) {}
		return this;
	}

	public Picker setColor(int color) {
		this.selectedColor = color;
		return this;
	}

	public Picker setOnColorSelect(OnColorSelect listener) {
		this.selectListener = listener;
		return this;
	}

	public Picker setTitle(String str) {
		this.title = str;
		return this;
	}

	public void show() {
		String hexDefault = String.format("#%08x", (0xFFFFFFFF & selectedColor));
		View view = LayoutInflater.from(context).inflate(R.layout.maximoff_picker, null);
		final ImageView preview = view.findViewById(R.id.pickerImageView1);
		preview.setImageDrawable(new ColorDrawable(selectedColor));

		final SeekBar alphaBar = view.findViewById(R.id.pickerSeekBar1);
		final SeekBar redBar = view.findViewById(R.id.pickerSeekBar2);
		final SeekBar greenBar = view.findViewById(R.id.pickerSeekBar3);
		final SeekBar blueBar = view.findViewById(R.id.pickerSeekBar4);

		final TextView alphaView = view.findViewById(R.id.pickerTextView1);
		final TextView redView = view.findViewById(R.id.pickerTextView2);
		final TextView greenView = view.findViewById(R.id.pickerTextView3);
		final TextView blueView = view.findViewById(R.id.pickerTextView4);

		final EditText hexValue = view.findViewById(R.id.pickerEditText1);
		hexValue.setText(hexDefault);
		hexValue.setHint(hexDefault);
		hexValue.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void afterTextChanged(Editable p1) {
					try {
						selectedColor = Color.parseColor(p1.toString());
						alphaBar.setProgress(Color.alpha(selectedColor));
						redBar.setProgress(Color.red(selectedColor));
						greenBar.setProgress(Color.green(selectedColor));
						blueBar.setProgress(Color.blue(selectedColor));
					} catch (Exception e) {}
				}
			});

		SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
			private boolean touched = false;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String tag = seekBar.getTag().toString().toUpperCase();
				switch (tag.charAt(0)) {
					case 'A':
						selectedColor = Color.argb(progress, Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor));
						alphaView.setText(String.valueOf(progress));
						break;

					case 'R':
						selectedColor = Color.argb(Color.alpha(selectedColor), progress, Color.green(selectedColor), Color.blue(selectedColor));
						redView.setText(String.valueOf(progress));
						break;

					case 'G':
						selectedColor = Color.argb(Color.alpha(selectedColor), Color.red(selectedColor), progress, Color.blue(selectedColor));
						greenView.setText(String.valueOf(progress));
						break;

					case 'B':
						selectedColor = Color.argb(Color.alpha(selectedColor), Color.red(selectedColor), Color.green(selectedColor), progress);
						blueView.setText(String.valueOf(progress));
						break;
				}
				preview.setImageDrawable(new ColorDrawable(selectedColor));
				if (touched) {
					String hexDefault = String.format("#%08x", (0xFFFFFFFF & selectedColor));
					hexValue.setText(hexDefault);
					hexValue.setHint(hexDefault);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				touched = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				touched = false;
			}
		};
		alphaBar.setOnSeekBarChangeListener(seekListener);
		redBar.setOnSeekBarChangeListener(seekListener);
		greenBar.setOnSeekBarChangeListener(seekListener);
		blueBar.setOnSeekBarChangeListener(seekListener);

		alphaBar.setProgress(Color.alpha(selectedColor));
		redBar.setProgress(Color.red(selectedColor));
		greenBar.setProgress(Color.green(selectedColor));
		blueBar.setProgress(Color.blue(selectedColor));

		AlertDialog dialog = new AlertDialog.Builder(context)
			.setView(view)
			.setTitle(title)
			.setPositiveButton(R.string.maximoff_picker_select, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					if (selectListener != null) {
						selectListener.select(String.format("#%08x", (0xFFFFFFFF & selectedColor)));
						selectListener.select(selectedColor);
					}
					p1.dismiss();
				}
			})
			.setNegativeButton(R.string.maximoff_picker_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					p1.cancel();
				}
			})
			.create();
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface p1) {
					if (selectListener != null) {
						selectListener.cancel();
					}
					p1.dismiss();
				}
			});
		dialog.show();
	}

	public interface OnColorSelect {
		public void select(String hexColor);
		public void select(int intColor);
		public void cancel();
	}
}
