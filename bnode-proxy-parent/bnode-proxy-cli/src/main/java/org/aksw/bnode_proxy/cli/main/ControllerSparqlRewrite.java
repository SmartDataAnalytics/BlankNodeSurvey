package org.aksw.bnode_proxy.cli.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.aksw.jena_sparql_api.algebra.expr.transform.ExprTransformVirtualBnodeUris;
import org.aksw.jena_sparql_api.core.utils.RDFDataMgrEx;
import org.aksw.jena_sparql_api.core.utils.ReactiveSparqlUtils;
import org.aksw.jena_sparql_api.user_defined_function.UserDefinedFunctions;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.function.user.UserDefinedFunctionDefinition;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerSparqlRewrite {

	@GetMapping("/profiles")
	@CrossOrigin(origins = "*")
	public List<String> profiles() {
		Model model = RDFDataMgr.loadModel("bnode-rewrites.ttl");
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
