package org.k1s.petriCode.pragmatics

/**
 * Pragmatics are instances of this class
 * @author kent
 *
 */
class Pragmatics {
	String name
	PragmaticsDescriptor type
	
	String arguments
	
	/* just for safe-keeping */
	String pragmaticsText
	
	
	/**
	 * Constructor
	 */
	public Pragmatics(){
		
	}
	
	/**
	 * Constructor
	 */
	public Pragmatics(PragmaticsDescriptor pragDef){
		this.type = pragDef
		this.name = pragDef.name
	}
	
	
	/**
	 * Creates a pragmatic from the text input
	 * @param input String
	 * @return
	 */
	public static Pragmatics parse(String input) {
		Pragmatics prag
		try{
			prag = new Pragmatics();
			
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
		} catch(Exception ex){
			throw new RuntimeException("Problem parsing pragmatic: '${input}'",ex)
		}
		return prag;

	}
}
