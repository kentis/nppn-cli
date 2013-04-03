package org.k1s.nppn.generation

import org.k1s.cpn.nppn.pragmatics.Pragmatics;

/**
 * Sets up parameteres for templates
 * @author kent
 *
 */
class TemplateParameters {
	static final def DEFAULT_STRATEGY = ParameterStrategy.FROM_PRAGMATIC
	def bindings
	
	/**
	 * constructor
	 */
	public TemplateParameters(bindings){
		this.bindings = bindings
	}
	
	
	/**
	 *Gets the parameters for a pragmatic using the given strategy 
	 */
	def getParamtersFor(pragmatic, node, ParameterStrategy strategy = TemplateParameters.DEFAULT_STRATEGY){
		if(strategy == null) strategy = TemplateParameters.DEFAULT_STRATEGY
		def parameters
		switch(strategy){
			case ParameterStrategy.FROM_PRAGMATIC:
				parameters = getParametersFromPragmatic(pragmatic)
				break
			case ParameterStrategy.CONDITIONALS:
				parameters = getParametersFromPragmaticConditionals(pragmatic)
				break
			default:
				throw new Exception("Unimplemented parameter strategy: $strategy")
		}
		return parameters
	}
	
	
	
	/**
	 * gets paramteters using the default strategy
	 */
	def getParametersFromPragmatic(Pragmatics pragmatic){
		return [params: pragmatic.getArguments().split(",")]
	}
	
	/**
	 * gets paramteters using the Conditionals strategy
	 */
	def getParametersFromPragmaticConditionals(prag){
		def retval = [:]
		def addParams = []
		def pragParams = prag.arguments.split(",")
		prag.arguments = pragParams[0]
		pragParams.each {
			if(it.startsWith('cond: ')){
				//println "TRANSLATING EXPR: element.correspondingNetElement.pragmatics[0]"
				addParams << Conditionals.translateExpr(prag, bindings)
				//println "TO: ${element.parameters["cond"]}"
			}
		}
		
		retval['cond'] = addParams
		if(pragParams.size() >= 3){
		retval['condTrueExpr'] = pragParams[1].replaceAll("_",",").trim()
		retval['condFalseExpr'] = pragParams[2].replaceAll("_",",").trim()
		}
		
		//If no cond was found
		if(addParams == []){
			retval.params = pragParams
		}
		
		return retval
	}
}

/**
 * Enum of parameter strategies
 * @author kent
 *
 */
enum ParameterStrategy{
	FROM_PRAGMATIC, COMBINED_PRAGMATICS, CUSTOM,
	CONDITIONALS,
}