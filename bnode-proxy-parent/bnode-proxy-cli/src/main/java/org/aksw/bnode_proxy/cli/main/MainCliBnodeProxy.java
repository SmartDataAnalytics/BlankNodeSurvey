package org.aksw.bnode_proxy.cli.main;

import org.apache.jena.query.ARQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainCliBnodeProxy {
	public static void main(String[] args) throws Exception {
		ARQ.enableBlankNodeResultLabels();
		ARQ.setFalse(ARQ.constantBNodeLabels);

		SpringApplication.run(MainCliBnodeProxy.class, args);
//		
//		// Retain blank node labels
//		// Note, that it is not sufficient to enable only input or output bnode labels
//		ARQ.enableBlankNodeResultLabels();
//		
//		// Jena (at least up to 3.11.0) handles pseudo iris for blank nodes on the parser level
//		// (which makes sense, because this way it prevents passing invalid IRIs up)
//		// {@link org.apache.jena.sparql.lang.ParserBase}
//		// This means, that blank nodes in SERVICE clauses would not be passed on as such
//		ARQ.setFalse(ARQ.constantBNodeLabels);
//
//		ExprTransformVirtualBnodeUris xform = new ExprTransformVirtualBnodeUris(vendorLabel, bnodeLabelFn);
////		RDFConnection result = RDFConnectionFactoryEx.wrapWithQueryTransform(conn, xform::rewrite);
//////		return result;
////
//////		RDFConnection core = new RDFConnectionRemoteBuilder()
//////				.build();
////		
////		Server server = FactoryBeanSparqlServer.newInstance()
////			.setPort(7531)
////			.setSparqlServiceFactory(new QueryExecutionFactorySparqlQueryConnection(RDFConnectionFactory.connect("http://dbpedia.org/sparql")))
////			.create();
////
////		server.start();
////		server.join();
	}
}
