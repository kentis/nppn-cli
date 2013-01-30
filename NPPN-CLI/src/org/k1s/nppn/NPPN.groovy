package org.k1s.nppn

class NPPN {

	public static void main(String[] args){
		def cli = new CliBuilder(usage: 'NPPN model' )
		cli.t('Specify template bindings')
		cli.o('Specify derived pragmatics')
		cli.o('Output directory (default: .)')
		cli.u('Print usage statement')
		
		def options = cli.parse(args)
		
		if(options.u){
			println cli.usage()
			return
		}
		
		
		
	}
}
