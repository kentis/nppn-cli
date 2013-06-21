package org.k1s.petriCode.generation

import org.codehaus.groovy.ast.expr.ClosureExpression;

import groovy.lang.Closure;

import static org.k1s.petriCode.generation.ParameterStrategy.*

/**
 * Implements a DSL for bindings
 * @author kent
 *
 */
class BindingsDSL {
	final String style
	
	/*static final String prefix = """Prefix(:=<http://k1s.org/orn/plattform/>)
Prefix(basic:=<http://t.k1s.org/OntologyReastrictedNets/basic/>)
Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
Prefix(owl:=<http://www.w3.org/2002/07/owl#>)
Prefix(cpn:=<http://hib.no/SADLTest/cpn#>)
Prefix(nppn:=<http://org.k1s/orn/customontology#>)

Ontology(<http://k1s.org/orn/plattform/>
Import( <http://t.k1s.org/OntologyReastrictedNets/basic.owl> )
Import( <http://t.k1s.org/OntologyReastrictedNets/cpn.owl> )


	Declaration(Class(:TemplateBinding))
	SubClassOf( :TemplateBinding ObjectAllValuesFrom( :pragmatic basic:Pragmatic ) )
	SubClassOf( :TemplateBinding ObjectAllValuesFrom( :template xsd:string ) )
	SubClassOf( :TemplateBinding ObjectAllValuesFrom( :container xsd:Boolean ) )
	SubClassOf( :TemplateBinding ObjectAllValuesFrom( :depends xsd:string ) )
	"""*/
	
	static final String postfix = "\n)"
	StringBuilder sb = new StringBuilder()	
	
	def bindings
	
	BindingsDSL(String style){
		this.style = style
	}
	
	/*static def makeOWL(closure){
		
		
		def bindings = new BindingsDSL("OWL")
		bindings.sb.append(prefix)
		closure.delegate = bindings	
		
		closure()
		bindings.sb.append(postfix)
		
		return bindings.getFowl()
	}*/
	
	
	static def makeBindings(InputStream is){
		makeBindings(is.text)
	}

	static def makeBindings(String is){
		def string = "import static org.k1s.petriCode.generation.ParameterStrategy.* \n return {\n${is}\n}"
		def closure = new GroovyShell().evaluate(string)
		return makeBindings(closure)
	}
	
	
	static def makeBindings(Closure closure){
		
		
		def dsl = new BindingsDSL("BINDINGS")
		dsl.bindings = new Bindings()
		closure.delegate = dsl	
		closure()

		
		return dsl.getBindings()
	}
	
	def methodMissing(String methodName, args) {
		if(style == "OWL"){
			def binding = new Binding(args.size() > 0? args[0] : [:])	
			binding.name = methodName
			sb.append binding.toFowl()
		} else if(style == "BINDINGS"){
			def binding = new Binding(args.size() > 0? args[0] : [:])
			binding.name = methodName
			bindings << binding
		}
	}
	
	
	String getFowl(){
		sb.toString()
	}
}

//class Binding {
//	def name
//	def template
//	def pragmatic
//	def dependencies
//	def isContainer = false
//	
//	def toFowl(){
//		StringBuilder fowl = new StringBuilder()
//		fowl.append """Declaration( NamedIndividual( :${name}))
//	ClassAssertion( :TemplateBinding :${name}) 
//	ObjectPropertyAssertion( :pragmatic :${name} nppn:${pragmatic} )
//	DataPropertyAssertion(:template :${name} \"${template}\"^^xsd:string)
//	DataPropertyAssertion(:container :${name} "${isContainer}"^^xsd:boolean)
//	"""
//		if(dependencies != null){
//			fowl.append "DataPropertyAssertion(:dependencies :${name} \"${dependencies}\"^^xsd:string)"
//		}
//		fowl.append "\n\n"
//		return fowl.toString()
//	}
//}
