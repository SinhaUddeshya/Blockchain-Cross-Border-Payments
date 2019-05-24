public enum Currencies {
	GDB(1), EUR(2), USD(3), CHF(4), NZD(5);
	private char value;

	private Currencies(int value) {
		if (value == 1)
			this.value = '£';
		else if (value == 2)
			this.value = '€';
		else if (value == 3)
			this.value = '$';
		else if (value == 4)
			this.value = 'F';
		else if (value == 5)
			this.value = '$';

	}

	public char getValue() {
		return value;
	}

	public static float ConvertingCur(Currencies sender, Currencies Receipents, float Value) {

		switch (sender) {
		case GDB:
			if (Receipents == EUR)
				return Value * (float)1.12;
			else if (Receipents == USD)
				return Value * (float)1.25;
			else if (Receipents == CHF)
				return Value * (float)1.26;		
			else if (Receipents == NZD)
				return Value * (float)1.85;
			else return Value;
		case EUR:
			if (Receipents == GDB)
				return Value * (float)0.88;
			else if (Receipents == USD)
				return Value * (float)1.12;
			else if (Receipents == CHF)
				return Value * (float) 1.13;		
			else if (Receipents == NZD)
				return Value * (float)1.66;
			else return Value;

		case USD:
			if (Receipents == GDB)
				return Value * (float)0.75;
			else if (Receipents == EUR)
				return Value * (float)0.88;
			else if (Receipents == CHF)
				return Value * (float) 0.99;		
			else if (Receipents == NZD)
				return Value * (float)1.46;
			else return Value;

		case CHF:
			if (Receipents == GDB)
				return Value * (float)0.79;
			else if (Receipents == EUR)
				return Value * (float)0.89;
			else if (Receipents == USD)
				return Value * (float) 1.01;		
			else if (Receipents == NZD)
				return Value * (float)1.47;
		case NZD:
			if (Receipents == GDB)
				return Value * (float)0.54;
			else if (Receipents == EUR)
				return Value * (float)0.60;
			else if (Receipents == USD)
				return Value * (float) 0.68;		
			else if (Receipents == CHF)
				return Value * (float)0.69;
			else return Value;

		default:
			break;
		}
		return 10f;
	}

//public static void main (String args[]) {
//	String x ="GBP (£)";
//	String y ="EUR (€)";
//	String z ="USD ($)";
//	
//	
//	float k = 100f;
//	Currencies c =null;
//	
//	if (x.equals("GBP (£)")){
//		c = Currencies.GDB;
//	}
//	if (c==GDB)System.out.println("YES");
//	
//	System.out.println(	ConvertingCur(c,EUR,k));
//	System.out.println(c.getValue());
//
//}
}