package org.k1s.nppn
import static org.k1s.nppn.Conditionals.*

import org.apache.commons.cli.Option;
import org.k1s.cpn.nppn.att.ATTFactory
import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL;
import org.k1s.nppn.blocks.derived.PragmaticsDerivator;
import org.k1s.nppn.cpn.io.CpnIO
import org.k1s.nppn.generation.BindingsDSL;
import org.k1s.nppn.generation.BindingsDSL;
import org.k1s.nppn.generation.CodeGenerator

class NPPN {

	public static void main(String[] args){
		
		def cli = getCli()
		
		def options = cli.parse(args)
		
		if(options.h){
			
			cli.usage()
			return
		}
		
		/****** MODULE 1: Derive pragmaitcs! ***********/
		
		//Parse the model
		//println options.arguments()
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
			//println it
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
		
		/************ Module 2: Generate ATT ****************/
		if(options.hasOption('output-annotated-net') || options.hasOption('only-output-annotated-net')  ){
			throw new Exception("nyi")
		}
			
		def factory = new ATTFactory(pragmaticsDescriptor)
		def att = factory.createATT(cpn, null, null)
		
		
		
		/********** Module 3: Generate code ***************/
		def bindings = new File(options.b)
		bindings = BindingsDSL.makeBindings(new FileInputStream(bindings))
		
		def generator
		if(options.o){
			generator = new CodeGenerator(att, bindings, options.o)
		} else {
			generator = new CodeGenerator(att, bindings, options.o)
		}
		def files = generator.generate()
		generator.write(files)
		//files.each{ println it; println "\n\n\n\n\n"}
	}
	
	
	def static getCli(){
		def cli = new CliBuilder(usage: 'NPPN model' )
		cli.with {
			
			//General options
			h(longOpt: 'help', 'useage information')
			o(longOpt: 'output-dir', args: 1, argName:'dir', 'The output directory (default: .)' )
			
			//Pragmaticvs derivation options
			_(longOpt: 'no-derived', 'No derived pragmatics will be added')
			_(longOpt: 'output-annotated-net', 'write the annotated net to the output dir')
			_(longOpt: 'only-output-annotated-net','write the annotated net to the output dir and exit')
			//p(longOpt: 'pragmatics-specification', args: Option.UNLIMITED_VALUES, valueSeparator: ',' as char, 'sets an additional set of pragmatics for the model')
			p(longOpt: 'pragmatics-specification',  'sets an additional set of pragmatics for the model')
			c(longOpt: 'no-core-pragmatics', 'disables the default core proagmatics')
			
			
			//ATT generation options
			_(longOpt: 'output-att','Outputs the ATT as an xml document')
			_(longOpt: 'only-output-att','Outputs the ATT as an xml document and exit')
			a(longOpt: 'use-att','Uses the ATT given as an argument and bypasses deriving pragmatics and ATT generation.')
			
						
			//Code generation options
			b(longOpt: 'template-bindings', args: 1, argName:'bindings', 'specifies template bindigns')
		}
//		cli.t('Specify template bindings')
//		cli.o('Specify derived pragmatics')
//		cli.o('Output directory (default: .)')
//		cli.u('Print usage statement')
		
		return cli
	}
	
	static def getCorePragmaticsStr(){
		this.class.getResourceAsStream("/core.prags").text
	}
}
