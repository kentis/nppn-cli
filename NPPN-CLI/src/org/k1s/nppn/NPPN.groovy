package org.k1s.nppn
import static org.k1s.nppn.Conditionals.*

import org.k1s.cpn.nppn.pragmatics.PrgamaticsDescriptorDSL;
import org.k1s.nppn.blocks.derived.PragmaticsDerivator;
import org.k1s.nppn.cpn.io.CpnIO

class NPPN {

	public static void main(String[] args){
		
		def options = cli.parse(args)
		
		if(options.h){
			println cli.usage()
			return
		}
		
		/****** MODULE 1: Derive pragmaitcs! ***********/
		
		//Parse the model
		def model = new File(options.arguments()[0]).text
		def io = new CpnIO()
		def cpn = io.readCPN(model)
		io.parsePragmatics(cpn)
		
		//get pragmatics descriptions
		def pragmaticsDescriptor = new StringBuffer()
		unless(options.hasOption('no-core-pragmatics')){
			pragmaticsDescriptor.append getCorePragmaticsStr()
		}
		
		options.'pragmatics-specifications'.each {
			pragmaticsDescriptor.append "\n"
			pragmaticsDescriptor.append new File(it).text
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
		
	}
	
	
	def static getCli(){
		def cli = new CliBuilder(usage: 'NPPN model' )
		cli.with {
			
			//General options
			h(longOpt: 'help', 'useage information')
			o(longOpt: 'output-dir', 'The output directory (default: .)' )
			
			//Pragmaticvs derivation options
			_(longOpt: 'no-derived', 'No derived pragmatics will be added')
			_(longOpt: 'output-annotated-net', 'write the annotated net to the output dir')
			_(longOpt: 'only-output-annotated-net','write the annotated net to the output dir and exit')
			p(longOpt: 'pragmatics-specification', args: Option.UNLIMITED_VALUES, valueSeparator: ',' as char, 'sets an additional set of pragmatics for the model')
			c(longOpt: 'no-core-pragmatics', 'disables the default core proagmatics')
			
			
			//ATT generation options
			_(longOpt: 'output-att','Outputs the ATT as an xml document')
			_(longOpt: 'only-output-att','Outputs the ATT as an xml document and exit')
			a(longOpt: 'use-att','Uses the ATT given as an argument and bypasses deriving pragmatics and ATT generation.')
			
						
			//Code generation options
			b(longOpt: 'template-bindings','specifies template bindigns')
		}
//		cli.t('Specify template bindings')
//		cli.o('Specify derived pragmatics')
//		cli.o('Output directory (default: .)')
//		cli.u('Print usage statement')
		
	}
	
	static def getCorePragmaticsStr(){
		this.class.getResourceAsStream("/core.prags").text
	}
}
