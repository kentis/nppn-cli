package org.k1s.cpn.nppn.pragmatics

/**
 * This DSL describes rules for defining derived pragmatics.
 * Conepts: pragmatics, rules
 * Example: pop(...)
 * @author kent
 *
 */
class PrgamaticsDescriptorDSL {
	
	def prags = [:]
	
	
	/**
	 * Runs a descriptor DSL
	 * 
	 * @param String containing a program of the DSL
	 * @return
	 */
	def build(closure){
		if(closure instanceof String){
			closure = new GroovyShell().evaluate("return {${closure}}")
		}
		if(!(closure instanceof Closure)) throw new IllegalArgumentException("Illegeal type of argument: $closure")
		
		closure.delegate = this
		closure()
	}
	
	
	/**
	 * Overrides the methodMissing method
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 */
	def methodMissing(String methodName, args) {
		PragmaticsDescriptor prag = new PragmaticsDescriptor(args.size() > 0? args[0] : [:])
		
		if(methodName.startsWith('_')) methodName = methodName.substring(1)
		
		prag.name = methodName
		prags[methodName] = prag

	}

		
	
}
