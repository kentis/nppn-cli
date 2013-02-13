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
	
	def build(closure){
		if(closure instanceof String){
			closure = new GroovyShell().evaluate("return {${closure}}")
		}
		if(!(closure instanceof Closure)) throw new IllegalArgumentException("Illegeal type of argument: $closure")
		
		closure.delegate = this
		closure()
	}
	
	def methodMissing(String methodName, args) {
		PragmaticsDescriptor prag = new PragmaticsDescriptor(args.size() > 0? args[0] : [:])
		prag.name = methodName
		prags[methodName] = prag
	}

		
	
}
