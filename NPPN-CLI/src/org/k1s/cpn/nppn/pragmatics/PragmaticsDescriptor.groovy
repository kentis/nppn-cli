package org.k1s.cpn.nppn.pragmatics





/**
 * Defines the descriptor (type) information of a pragmatic.
 * @author kent
 *
 */
class PragmaticsDescriptor {

	OriginType origin = OriginType.EXPLICIT
	String name
	List paramters
	
	
	List derviationRules
	List validationRules
	
	
	void setOrigin(origin){
		if(origin instanceof String){
			if(origin == "explicit"){
				origin = OriginType.EXPLICIT
			} else if(origin == "derived"){
			origin = OriginType.DERIVED
			}
			
		}
		this.origin = origin
	}
	
}

enum OriginType {
	EXPLICIT, DERIVED
}

class ParamterDesciptor {
	String name
	String type
	List<String> legalValues
}