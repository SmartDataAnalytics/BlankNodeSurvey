package org.aksw.bnode_proxy.cli.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.aksw.jena_sparql_api.algebra.expr.transform.ExprTransformVirtualBnodeUris;
import org.aksw.jena_sparql_api.core.utils.RDFDataMgrEx;
import org.aksw.jena_sparql_api.core.utils.ReactiveSparqlUtils;
import org.aksw.jena_sparql_api.transform.result_set.QueryExecutionTransformResult;
import org.aksw.jena_sparql_api.user_defined_function.UserDefinedFunctions;
import org.aksw.jena_sparql_api.utils.Vars;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.function.user.UserDefinedFunctionDefinition;
import org.apache.jena.sparql.graph.NodeTransform;
import org.apache.jena.sparql.util.ExprUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerSparqlRewrite {

	@GetMapping("/transform")
	@CrossOrigin(origins = "*")
	public String transform(@RequestParam String profile, @RequestParam String json) {
		System.out.println("profile: " + profile);
		Model model = RDFDataMgr.loadModel("bnode-rewrites.ttl");
		RDFDataMgrEx.execSparql(model, "udf-inferences.sparql");

		Set<String> profiles = new HashSet<>(Arrays.asList("http://ns.aksw.org/profile/" + profile));
		Map<String, UserDefinedFunctionDefinition> macros = UserDefinedFunctions.load(model, profiles);
		
		//UserDefinedFunctionDefinition udf = macros.get("http://ns.aksw.org/profile/skolemizeBnodeLabel");
//		NodeTransform nodeTransform = QueryExecutionTransformResult.createBnodeLabelTransform(ExprUtils.parse("<http://ns.aksw.org/profile/skolemizeBnodeLabel>(?x)"), Vars.x);
		//ExprTransformVirtualBnodeUris xform = new ExprTransformVirtualBnodeUris(macros);

		
		Expr e = ExprUtils.parse("<http://ns.aksw.org/function/skolemizeBnodeLabel>(?x)"); //, Vars.x)
		Expr expr = ExprTransformVirtualBnodeUris.expandMacro(macros, e);
		System.out.println("Expanded " + e + " -> " + expr);
		
		NodeTransform nodeTransform = QueryExecutionTransformResult.createBnodeLabelTransform(expr, Vars.x);
		ResultSet rs = ResultSetFactory.fromJSON(new ByteArrayInputStream(json.getBytes()));
		
		ResultSet rs2 = QueryExecutionTransformResult.applyNodeTransform(nodeTransform, rs);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(out, rs2);
		String result = out.toString();
		return result;
	}

	
	@GetMapping("/profiles")
	@CrossOrigin(origins = "*")
	public List<String> profiles() {
		Model model = RDFDataMgr.loadModel("bnode-rewrites.ttl");
		
		QueryExecutionFactory.create("SELECT * { ?s ?p ?o }", model);
		RDFConnection conn = RDFConnectionFactory.connect(DatasetFactory.wrap(model));
		
		RDFDataMgrEx.execSparql(model, "udf-inferences.sparql");

		List<String> result = ReactiveSparqlUtils.execSelectQs(() -> QueryExecutionFactory.create("SELECT DISTINCT ?p { ?s <http://ns.aksw.org/jena/udf/profile> ?p }", model))
			.map(b -> b.get("p"))
			.filter(Objects::nonNull)
			.filter(RDFNode::isURIResource)
			.map(RDFNode::asResource)
			.map(Resource::getURI)
			.toList()
			.blockingGet();
		
		return result;
	}

	
	@GetMapping("/rewrite")
	@CrossOrigin(origins = "*")
	public String rewrite(@RequestParam String profile, @RequestParam String query) {
		Model model = RDFDataMgr.loadModel("bnode-rewrites.ttl");
		RDFDataMgrEx.execSparql(model, "udf-inferences.sparql");

		Set<String> profiles = new HashSet<>(Arrays.asList("http://ns.aksw.org/profile/" + profile));
		Map<String, UserDefinedFunctionDefinition> macros = UserDefinedFunctions.load(model, profiles);
		
		ExprTransformVirtualBnodeUris xform = new ExprTransformVirtualBnodeUris(macros);
		Query q = QueryFactory.create(query);
		Query result = xform.rewrite(q);
		return "" + result;
	}
}
