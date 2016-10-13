package com.android.mainactivity.listenerfactory;

import com.android.mainactivity.MainActivity;
import com.fyber.marmalade.FyberActivity;

public class FyberActivityFactory extends MainActivity.ListenerFactory {
	protected MainActivity.Listener makeListener() {
		return new FyberActivity();
	}
}
