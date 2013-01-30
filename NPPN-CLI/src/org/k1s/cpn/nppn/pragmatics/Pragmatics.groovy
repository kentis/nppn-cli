package org.k1s.cpn.nppn.pragmatics

class Pragmatics {
	String name
	PragmaticsDescriptor type
	
	String arguments
	
	/* just for safe-keeping */
	String pragmaticsText
	
	public static Pragmatics parse(String input) {
		Pragmatics prag = new Pragmatics();
		
		String pragDef = input;
		//System.out.println("PRAGDEF: " + pragDef);

		if (pragDef.indexOf('(') == -1) {
			prag.name = pragDef
		} else {
			prag.name = pragDef.subSequence(0, pragDef.indexOf('('))
					    .toString();
					
			String d = pragDef.substring(pragDef.indexOf('(') + 1,
					pragDef.lastIndexOf(')'));

			prag.setArguments(d);
		}
		return prag;

	}
}