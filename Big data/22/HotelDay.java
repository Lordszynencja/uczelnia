public class HotelDay {
	int hotel;
	int day;

	public HotelDay(final int hotel, final int day) {
		this.hotel = hotel;
		this.day = day;
	}
	
	public boolean equals(final HotelDay pair) {
		return (this.hotel == pair.hotel) && (this.day == pair.day);
	}

	public boolean equals(final Object o) {
		if (o instanceof HotelDay) return this.equals((HotelDay)o);
		else return false;
	}

	public String toString() {
		return "(" + this.hotel + "," + this.day + ")";
	}
	
	public int hashCode() {
		return this.hotel*10000+this.day;
	}
}
