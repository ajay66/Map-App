package etg.com.mapfragmentssamp;

public class MyMarker {
	public String mLabel;
	public String mLabel1;
	public String mIcon;
	public Double mLatitude;
	public Double mLongitude;

	public MyMarker(String label, String label1, String icon, Double latitude,
			Double longitude) {
		this.mLabel = label;
		this.mLabel1 = label1;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mIcon = icon;
	}

	public String getmLabel() {
		return mLabel;
	}

	public void setmLabel(String mLabel) {
		this.mLabel = mLabel;
	}

	public String getmLabel1() {
		return mLabel1;
	}

	public void setmLabel1(String mLabel1) {
		this.mLabel1 = mLabel1;
	}

	public String getmIcon() {
		return mIcon;
	}

	public void setmIcon(String icon) {
		this.mIcon = icon;
	}

	public Double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(Double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public Double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(Double mLongitude) {
		this.mLongitude = mLongitude;
	}
}
