package org.k1s.nppn
import static org.k1s.nppn.Conditionals.*

import org.apache.commons.cli.Option;
import org.k1s.cpn.nppn.att.ATTFactory
import org.k1s.cpn.nppn.att.ATTIO;
import org.k1s.cpn.nppn.pragmatics.PragmaticsChecker;
import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL;
import org.k1s.nppn.blocks.derived.PragmaticsDerivator;
import org.k1s.nppn.cpn.io.CpnIO
import org.k1s.nppn.generation.BindingsDSL;
import org.k1s.nppn.generation.BindingsDSL;
import org.k1s.nppn.generation.CodeGenerator

class NPPN {

	static def strict = false
	
	/**
	 * Main method of the program. This is where it all begins.
	 * @param args The arguments as a String array as per tradition.
	 */
	public static void main(String[] args){
		
		def cli = getCli()
		
		def options = cli.parse(args)
		
		
		if(options.h || options.arguments().size() != 1  ){
			
			cli.usage()
			return
		}
		
		NPPN.strict = options.hasOption('strict')
		
		/****** MODULE 1: Derive pragmatics! ***********/
		
		//Parse the model

		def model = new File(options.arguments().last())
		
		def io = new CpnIO()
		def cpn = io.readCPN(new FileInputStream(model))
		io.parsePragmatics(cpn)
		
		//get pragmatics descriptions
		def pragmaticsDescriptor = new StringBuffer()
		unless(options.hasOption('no-core-pragmatics')){
			pragmaticsDescriptor.append getCorePragmaticsStr()
		}
		
		if(options.'pragmatics-specification'){
		options.'pragmatics-specifications'.each {
			
			pragmaticsDescriptor.append "\n"
			pragmaticsDescriptor.append new File(it).text
		}
		}
		//Parse pragmatics descriptions
		def prags = new PrgamaticsDescriptorDSL()
		prags.build(pragmaticsDescriptor.toString())
		pragmaticsDescriptor = prags.prags
		
		
		//Add derived pragmatics
		
		unless(options.hasOption('no-derived')){
			//do derivition stuff
			PragmaticsDerivator.addDerivedPragmatics(cpn, pragmaticsDescriptor)
		}
		
		unless(options.hasOption('no-constraint-checks')){
			def violations = []
			def constraintsOk = PragmaticsChecker.check(cpn, pragmaticsDescriptor, violations)
			unless(constraintsOk){
				def sb = new StringBuffer()
				violations.unique().each {
					sb.append(it).append('\n')
				}
				println "Pragmatics constraints not fulfilled:\n${sb.toString()}"
				 
				System.exit(1);
			}
		}
		
		/************ Module 2: Generate ATT ****************/
		
		if(options.hasOption('output-annotated-net') || options.hasOption('only-output-annotated-net')  ){
			throw new Exception("nyi")
		}
			
		def factory = new ATTFactory(pragmaticsDescriptor)
		def att = factory.createATT(cpn, null, null)
		
		
		if(options.hasOption('output-att') || options.hasOption('only-output-att')){
				ATTIO.writeATT(att, new File("att.xml"))
		}
		if(options.hasOption('output-att-image') || options.hasOption('only-output-att-image')){
			ATTIO.writeATTImage(att, "att.png")
		}
		
		if(options.hasOption('only-output-att') ||options.hasOption('only-output-att-image') ){
			return
		}
		
		/********** Module 3: Generate code ***************/
		def bindings = new File(options.b)
		bindings = BindingsDSL.makeBindings(new FileInputStream(bindings))
		
		def generator
		if(options.o){
			generator = new CodeGenerator(att, bindings, options.o)
		} else {
			generator = new CodeGenerator(att, bindings, "./")
		}
		def files = generator.generate()
		generator.write(files)
		//files.each{ println it; println "\n\n\n\n\n"}
	}
	
	/**
	 * Returns the CliBuilder used for this application.
	 * @return CliBuilder
	 */
	def static getCli(){
		def cli = new CliBuilder(usage: 'NPPN model' )
		cli.with {
			
			//General options
			h(longOpt: 'help', 'usage information')
			o(longOpt: 'output-dir', args: 1, argName:'dir', 'The output directory (default: .)' )
			
			//Pragmatics derivation options
			_(longOpt: 'no-derived', 'No derived pragmatics will be added')
			_(longOpt: 'output-annotated-net', 'write the annotated net to the output dir')
			_(longOpt: 'only-output-annotated-net','write the annotated net to the output dir and exit')
			//p(longOpt: 'pragmatics-specification', args: Option.UNLIMITED_VALUES, valueSeparator: ',' as char, 'sets an additional set of pragmatics for the model')
			p(longOpt: 'pragmatics-specification', args: 1, argName: "descriptions file", 'sets an additional set of pragmatics for the model')
			c(longOpt: 'no-core-pragmatics', 'disables the default core pragmatics')
			
			
			//ATT generation options
			_(longOpt: 'output-att','Outputs the ATT as an xml document')
			_(longOpt: 'output-att-image','Outputs the ATT as an image')
			_(longOpt: 'only-output-att-image','Outputs the ATT as an image')
			
			_(longOpt: 'only-output-att','Outputs the ATT as an xml document and exit')
			a(longOpt: 'use-att','Uses the ATT given as an argument and bypasses deriving pragmatics and ATT generation.')
			_(longOpt:'no-constraint-checks', 'Turns off constraint checks on pragmatics')
			_(longOpt:'strict', 'Forces stricter checks on pragmatic constraints and bindings')
						
			//Code generation options
			b(longOpt: 'template-bindings', args: 1, argName:'bindings', 'specifies template bindings')
		}
//		cli.t('Specify template bindings')
//		cli.o('Specify derived pragmatics')
//		cli.o('Output directory (default: .)')
//		cli.u('Print usage statement')
		
		return cli
	}
	
	/**
	 * Gets the core pragmatics definitions
	 * @return String
	 */
	static def getCorePragmaticsStr(){
		this.class.getResourceAsStream("/core.prags").text
	}
}
