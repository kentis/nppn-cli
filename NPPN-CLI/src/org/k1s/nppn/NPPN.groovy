package org.k1s.nppn
import static org.k1s.nppn.Conditionals.*

class NPPN {

	public static void main(String[] args){
		
		def options = cli.parse(args)
		
		if(options.h){
			println cli.usage()
			return
		}
		
		unless(options.hasOption('no-derived')){
			//do derivition stuff
		}
		
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
			p(longOpt: 'pragmatics-specification', 'sets an additional set of pragmatics for the model')
			c(longOpt: 'no-core-pragmatics', 'disables the default core proagmatics')
			
			
			//ATT generation options
			_(longOpt: 'output-att','Outputs the ATT as an xml document')
			_(longOpt: 'only-output-att','Outputs the ATT as an xml document and exit')
			a(longOpt: 'use-att','Uses the ATT given as an argument and bypasses deriving pragmatics and ATT generation.')
			
						
			//Code generation options
			b(longOpt: 'tempålate-bindings','specifies template bindigns')
		}
//		cli.t('Specify template bindings')
//		cli.o('Specify derived pragmatics')
//		cli.o('Output directory (default: .)')
//		cli.u('Print usage statement')
		
	}
}
