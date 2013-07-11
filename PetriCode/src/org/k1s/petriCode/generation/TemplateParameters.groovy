package org.k1s.petriCode.generation

import org.cpntools.accesscpn.model.Transition;
import org.k1s.petriCode.blocks.Principal;
import org.k1s.petriCode.pragmatics.Pragmatics;

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
	def getParamtersFor(pragmatic, node, attNode, ParameterStrategy strategy = TemplateParameters.DEFAULT_STRATEGY){
		if(strategy == null) strategy = TemplateParameters.DEFAULT_STRATEGY
		def parameters
		switch(strategy){
			case ParameterStrategy.FROM_PRAGMATIC:
				parameters = getParametersFromPragmatic(pragmatic)
				break
			case ParameterStrategy.CONDITIONALS:
				parameters = getParametersFromPragmaticConditionals(pragmatic)
				break
			case ParameterStrategy.CONDITIONALS_ATT:
				parameters = getParametersFromPragmaticConditionalsATT(attNode, pragmatic)
				break
			case ParameterStrategy.NET:
				parameters = getParametersFromNET(pragmatic, node)
				break
			case ParameterStrategy.NET_ATT:
				parameters = getParametersFromNETATT(pragmatic, attNode, node)
				break
			default:
				throw new Exception("Unimplemented parameter strategy: $strategy")
		}
		parameters['indentLevel'] = attNode.level - 1 
		return parameters
	}
	
	
	
	/**
	 * gets paramteters using the default strategy
	 */
	def getParametersFromPragmatic(Pragmatics pragmatic){
		if(pragmatic.getArguments() == null) return [params:[]]
		
		return [params: pragmatic.getArguments().split(",").collect{ it.trim()}]
	}
	
	/**
	 * gets paramteters using the default strategy
	 */
	def getParametersFromNET(Pragmatics pragmatic, node){
		def retval = getParametersFromPragmatic(pragmatic)
		retval.node = node
		if(node instanceof Transition) retval.transition = node
		if(node.hasProperty("transition")) retval.transition = transition
		if(node.hasProperty("start_node")) retval.start = transition
		return retval	
	}
	
	def getParametersFromNETATT(Pragmatics pragmatic, attNode, node){
		def retval = getParametersFromNET(pragmatic, node)
		def principal = attNode
		while(!(principal instanceof Principal)){
			principal = principal.parent
		}
		
		retval['className'] =  principal.name
		return retval
	}
	
	def getParametersFromPragmaticConditionalsATT(prag, att_node){
		def retval = getParametersFromPragmaticConditionals(prag)
		def principal = att_node
		while(!(principal instanceof Principal)){
			principal = principal.parent
		}
		
		retval['className'] =  principal.name
		return retval
	}
	
	
	/**
	 * gets paramteters using the Conditionals strategy
	 */
	def getParametersFromPragmaticConditionals(prag){
		def retval = [:]
		def addParams = []
		if(prag.arguments == null) return retval
		def pragParams = prag.arguments.split(",")
		def argBu = prag.arguments
		//prag.arguments = pragParams[0]
		int i = 0;
		int condId = 0;
		pragParams.each {
			if(it.trim().startsWith('cond: ')){
				//println "TRANSLATING EXPR: element.correspondingNetElement.pragmatics[0]"
				prag.arguments = it.trim()
				addParams << Conditionals.translateExpr(prag, bindings)
				//println "TO: ${element.parameters["cond"]}"
				condId = i
			}
			i++
		}
		
		retval['cond'] = addParams
		if(pragParams.size() >= 3){
		  retval['condTrueExpr'] = pragParams[condId+1].replaceAll("_",",").trim()
		  retval['condTrueExpr'] = retval['condTrueExpr'].replaceAll(",,TOKEN,,", "__TOKEN__")
		  retval['condFalseExpr'] = pragParams[condId+2].replaceAll("_",",").trim()
                  retval['condFalseExpr'] = retval['condFalseExpr'].replaceAll(",,TOKEN,,", "__TOKEN__")

		}
		
		//If no cond was found
		//if(addParams == []){
			retval.params = pragParams
		//}
		
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
	CONDITIONALS_ATT,
	NET,
	NET_ATT, 
	DECLARATIONS_AND_TYPES,
}
