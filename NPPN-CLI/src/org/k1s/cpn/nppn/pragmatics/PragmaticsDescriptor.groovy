package org.k1s.cpn.nppn.pragmatics

/**
 * Defines the descriptor (type) information of a pragmatic.
 * @author kent
 *
 */
class PragmaticsDescriptor {

	Type type
	String name
	List paramters
	
	List derviationRules
	List validationRules
	
}

enum Type {
	EXPLICIT, DERIVED
}

class ParamterDesciptor {
	String name
	String type
	List<String> legalValues
}