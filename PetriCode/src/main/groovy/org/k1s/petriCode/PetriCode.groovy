package org.k1s.petriCode
import static org.k1s.petriCode.Conditionals.*

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.util.logging.StreamHandler;

import groovy.util.logging.Commons;
import groovy.util.logging.Log;
import groovy.util.logging.Log4j;

import org.k1s.petriCode.att.ATTFactory
import org.k1s.petriCode.att.ATTIO;
import org.k1s.petriCode.pragmatics.PragmaticsChecker;
import org.k1s.petriCode.pragmatics.PrgamaticsDescriptorDSL;
import org.k1s.petriCode.blocks.derived.PragmaticsDerivator;
import org.k1s.petriCode.generation.BindingsDSL;
import org.k1s.petriCode.generation.BindingsDSL;
import org.k1s.petriCode.generation.CodeGenerator
import org.k1s.petriCode.cpn.io.CpnIO;


//for timeing
import groovy.time.*

//@Log
class PetriCode {

	static def strict = false
	static def pragmaticsDescriptors
	static def LOG_LEVEL
	
	static Logger log 
	
	static times = [:]
	/**
	 * Main method of the program. This is where it all begins.
	 * @param args The arguments as a String array as per tradition.
	 */
	public static void main(String[] args){
		times.start = System.currentTimeMillis()
		log = Logger.getLogger("PetriCode")
		log.addHandler(new StreamHandler(System.out, new SimpleFormatter()))
		
		def cli = getCli()
		
		def options = cli.parse(args)

		if(options.v){
			this.LOG_LEVEL = java.util.logging.Level.FINEST
		}else {
			this.LOG_LEVEL = java.util.logging.Level.WARNING
		}
		
                

		log.level = this.LOG_LEVEL
		log.getHandlers()[0].level =this.LOG_LEVEL 
		
		log.fine "Starting PetriCode"
		log.finest "Log level ${log.level}"
		
		if(options.h || options.arguments().size() != 1  ){
			cli.usage()
			return
		}
		
		PetriCode.strict = options.hasOption('strict')
		
		times.startPragsModule = System.currentTimeMillis()
		/****** MODULE 1: Derive pragmatics! ***********/
		log.finest "Parsing pragmatics"
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
		this.pragmaticsDescriptors = pragmaticsDescriptor
		
		//Parse the model
		log.finest "Parsing the model"
		def model = new File(options.arguments().last())
		//println "pragmaticsDescriptors: $pragmaticsDescriptor"
		def io = new CpnIO(pragmaticsDescriptor)
		def cpn = io.readCPN(new FileInputStream(model))
		io.parsePragmatics(cpn)
		

		//Add derived pragmatics
		
		unless(options.hasOption('no-derived')){
			log.finest "Adding derived pragmatics"
			//do derivition stuff
			PragmaticsDerivator.addDerivedPragmatics(cpn, pragmaticsDescriptor)
		}
		
		unless(options.hasOption('no-constraint-checks')){
			log.finest "Checking model/pramatic constraints"
			def violations = []
			def constraintsOk = PragmaticsChecker.check(cpn, pragmaticsDescriptor, violations)
			unless(constraintsOk){
				def sb = new StringBuffer()
				violations.unique().each {
					sb.append(it).append('\n')
				}
				log.severe "Pragmatics constraints not fulfilled:\n${sb.toString()}"
				 
				System.exit(1);
			}
		}
		
		/************ Module 2: Generate ATT ****************/
		times.startATTModule = System.currentTimeMillis()
		if(options.hasOption('output-annotated-net') || options.hasOption('only-output-annotated-net')  ){
			throw new Exception("nyi")
		}
		log.finest "Generating ATT"
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
		times.startCGModule = System.currentTimeMillis()
		def bindings 
		if(options.b){
			def bConcats = new StringBuffer()
			options.bs.each{
				bConcats.append( new File(it).text ).append('\n') 
			}
			//def bindings = new File(options.b)
			bindings = BindingsDSL.makeBindings(bConcats.toString())
		} else {
			throw RuntimeException("no template bindings found")
		}
		log.finest "Generating code"
		def generator
		if(options.o){
			generator = new CodeGenerator(att, bindings, options.o)
		} else {
			generator = new CodeGenerator(att, bindings, "./")
		}
		def files = generator.generate()
		//println "calling write"
		log.finest "Writeing ${files.size()} files"
		generator.write(files)
		//files.each{ println it; println "\n\n\n\n\n"}

		times.end = System.currentTimeMillis()
                if(options.hasOption('timeings'))
  		  println times
	}
	
	/**
	 * Returns the CliBuilder used for this application.
	 * @return CliBuilder
	 */
	def static getCli(){
		def cli = new CliBuilder(usage: 'PetriCode model' )
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
                         
                        //More general options
			_(longOpt:'timeings', 'Computes and reports the time spent in each module.')
			v(longOpt: 'verbose', 'Verbose logging')
		}
//		cli.t('Specify template bindings')
//		cli.o('Specify derived pragmatics')
//		cli.o('Output directory (default: .)')
//		cli.u('Print usage statement')
		
		return cli
	}
	
	
	static hasServicePragmatic(node){
		def servicePragmatics = this.pragmaticsDescriptors.collect{ it.value }.findAll { it.containsService  }
		
		def retVal = false
		node.pragmatics.each{
			if(servicePragmatics.name.contains(it.name)) retVal = true
		}
		return retVal
	}
	
	/**
	 * Gets the core pragmatics definitions
	 * @return String
	 */
	static def getCorePragmaticsStr(){
		this.class.getResourceAsStream("/core.prags").text
	}
}
